package cn.lingmoe.joint.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import cn.lingmoe.joint.constant.InterruptPoilcy;
import cn.lingmoe.joint.anno.Joint;
import cn.lingmoe.joint.constant.UseReturnPoilcy;
import cn.lingmoe.joint.config.GlobalJointerConfigHelper;
import cn.lingmoe.joint.evaluator.JointExpressionEvaluator;
import cn.lingmoe.joint.util.SpringUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

/**
 * @author yukdawn@gmail.com
 */
public class Jointer implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(Jointer.class);

    private final ThreadLocal<List<JointBean>> jBThreadLocal = new ThreadLocal<List<JointBean>>(){
        @Override
        protected List<JointBean> initialValue() {
            return initList();
        }

        @Override
        public List<JointBean> get() {
            return Optional.ofNullable(super.get()).orElse(Collections.emptyList());
        }

        @Override
        public void set(List<JointBean> value) {
            throw new RuntimeException("Dont set joinBean in application started");
        }
    };
    private final JointExpressionEvaluator evaluator;
    private final String[] proxyBeanNames;

    public Jointer(String[] proxyBeanNames, JointExpressionEvaluator evaluator) {
        this.proxyBeanNames = proxyBeanNames;
        this.evaluator = evaluator;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 这里使用的是Jointer, 默认为null
        this.preJoint(invocation);
        return "toString".equals(invocation.getMethod().getName()) && invocation.getArguments().length == 0 ?
                this.toString() : this.execCooperation(invocation);
    }

    private void preJoint(MethodInvocation invocation){
        if (log.isDebugEnabled()){
            log.debug("method cooperation started! current method [{}]", invocation.getMethod().getName());
        }
    }

    private Object execCooperation(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        Object invokeResult;
        for (JointBean jointBean : generateExecQueue(invocation)) {
            // 在当前类有重写此方法的时候，才去调用此方法
            if (jointBean.declaredMethodList.stream().noneMatch(jointMethod -> equalsMethod(jointMethod, invocation.getMethod()))){
                continue;
            }
            if (log.isDebugEnabled()){
                log.debug("jointBean bean: [{}], invoke method: [{}]", jointBean.beanName, invocation.getMethod().toGenericString());
            }
            invokeResult = invocation.getMethod().invoke(jointBean.bean, invocation.getArguments());
            if (log.isTraceEnabled()){
                log.trace("jointBean invoke Result : [{}]", Objects.toString(invokeResult, "method return null"));
            }
            // 检查返回值赋值
            if (this.isUseReturn(jointBean, invocation)){
                result = invokeResult;
            }
            // 检查中断
            if (this.isInterrupt(jointBean, invocation)){
                break;
            }
        }
        return result;
    }

    /**
     * 默认使用默认bean（即未加协作注解但又参与协作的bean，这种bean只会存在一个或不存在；并且因排序策略，必然是第一个执行的bean）的返回值，
     * 其他参与协作的bean按照顺序来参与赋值，值由最后一个isUseReturn == true的bean决定。
     *
     * 如果协作注解未标识是否使用返回值，则由全局配置控制
     *
     * @param jointBean 协作bean元数据
     * @param invocation 被拦截方法
     * @return 是否使用当前bean被拦截方法的返回值
     */
    private boolean isUseReturn(JointBean jointBean, MethodInvocation invocation) {
        boolean isUseReturn;
        UseReturnPoilcy poilcy = UseReturnPoilcy.isNone(jointBean.isUseReturn) ?
                GlobalJointerConfigHelper.getUseReturnPoilcy() : jointBean.isUseReturn;
        switch (poilcy){
            case TRUE:
                isUseReturn = true;
                break;
            case EXPRESSION:
                isUseReturn = this.evaluator.condition(jointBean.userReturnCondition, jointBean, invocation);
                break;
            default:
                isUseReturn = false;
                break;
        }
        return JointBean.DEFAULT_BEAN_NAME.equals(jointBean.value) || isUseReturn;
    }

    /**
     * 根据中断标识判断是否中断，默认情况下由全局配置控制
     *
     * @param jointBean 协作bean元数据
     * @param invocation 被拦截方法
     * @return 当前bean被拦截方法执行后是否中断
     */
    private boolean isInterrupt(JointBean jointBean, MethodInvocation invocation) {
        boolean isInterrupt;
        InterruptPoilcy poilcy = InterruptPoilcy.isNone(jointBean.isInterrupt) ?
                GlobalJointerConfigHelper.getInterruptPoilcy() : jointBean.isInterrupt;
        switch (poilcy){
            case TRUE:
                isInterrupt = true;
                break;
            case EXPRESSION:
                isInterrupt = this.evaluator.condition(jointBean.interruptCondition, jointBean, invocation);
                break;
            default:
                isInterrupt = false;
                break;
        }
        return isInterrupt;
    }

    private boolean equalsMethod(Method m1, Method m2){
        return Objects.equals(m1.getName(), m2.getName())
                && Objects.equals(m1.getReturnType(), m2.getReturnType())
                && Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes());
    }

    private Queue<JointBean> generateExecQueue(MethodInvocation invocation) {
        return this.jBThreadLocal.get().stream()
                .filter(jointBean -> this.isAddExecQueue(jointBean, invocation))
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    /**
     * 1.1 如果无joint注解，说明是默认bean，默认加入序列
     * 1.2 无表达式或表达式验证通过，加入执行序列
     * @param jointBean 协作bean元数据
     * @param invocation 切入方法
     * @return 是否可以加入执行序列
     */
    private boolean isAddExecQueue(JointBean jointBean, MethodInvocation invocation){
        return Objects.isNull(jointBean.joint) || !StringUtils.hasText(jointBean.joint.condition()) || this.evaluator.condition(jointBean.condition, jointBean, invocation);
    }

    private List<JointBean> initList() {
        List<JointBean> list = new ArrayList<>();
        for (String beanName : this.proxyBeanNames) {
            Object bean = SpringUtil.getBean(beanName);
            Joint joint = AnnotationUtils.findAnnotation(bean.getClass(), Joint.class);
            list.add(new JointBean(joint, beanName, bean));
        }
        list.sort(Comparator.comparing(JointBean::getOrder).thenComparing(JointBean::getBeanName));
        return list;
    }
}
