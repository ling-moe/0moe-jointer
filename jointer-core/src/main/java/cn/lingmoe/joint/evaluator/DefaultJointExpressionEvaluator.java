package cn.lingmoe.joint.evaluator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.lingmoe.joint.anno.Joint;
import cn.lingmoe.joint.interceptor.JointBean;
import cn.lingmoe.joint.util.SpringUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

/**
 * @author yukdawn@gmail.com
 */
public class DefaultJointExpressionEvaluator extends CachedExpressionEvaluator implements JointExpressionEvaluator {

    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<>(64);

    @Override
    public boolean condition(String expression, JointBean jointBean, MethodInvocation invocation) {
        return exec(expression, jointBean.getJoint(),
                invocation.getMethod(), new AnnotatedElementKey(jointBean.getBean().getClass(), jointBean.getBean().getClass()),
                invocation.getArguments(), SpringUtil.getApplicationContext());
    }

    private boolean exec(String expression, Joint joint, Method targetMethod,
                             AnnotatedElementKey methodKey, Object[] args, @Nullable BeanFactory beanFactory) {
        JointBeanExpressionRootObject root = new JointBeanExpressionRootObject(joint);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(root, targetMethod, args, getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }

        return (Boolean.TRUE.equals(getExpression(this.conditionCache, methodKey, expression).getValue(
                evaluationContext, Boolean.class)));
    }
}
