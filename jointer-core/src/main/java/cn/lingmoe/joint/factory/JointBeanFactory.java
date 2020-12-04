package cn.lingmoe.joint.factory;

import cn.lingmoe.joint.evaluator.JointExpressionEvaluator;
import cn.lingmoe.joint.interceptor.Jointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author yukdawn@gmail.com
 */
public class JointBeanFactory implements FactoryBean<Object> {

    public static final String FIELD_PROXY_INTERFACE = "proxyInterface";
    public static final String FIELD_PROXY_BEAN_NAMES = "proxyBeanNames";
    public static final String FIELD_JOINT_EVALUATOR = "jointEvaluator";

    private static final Logger log = LoggerFactory.getLogger(JointBeanFactory.class);

    private Class<?> proxyInterface;
    private String[] proxyBeanNames;
    private JointExpressionEvaluator jointEvaluator;

    public JointBeanFactory(Class<?> proxyInterface, String[] proxyBeanNames, JointExpressionEvaluator jointEvaluator) {
        this.proxyInterface = proxyInterface;
        this.proxyBeanNames = proxyBeanNames;
        this.jointEvaluator = jointEvaluator;
    }

    public JointBeanFactory() {
    }

    public Object getObject() {
        return createServiceProxy(this.proxyInterface, this.proxyBeanNames);
    }

    public Object createServiceProxy(Class<?> targetInterface, String[] proxyBeanNames) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(targetInterface);
        proxyFactory.addAdvice(new Jointer(proxyBeanNames, this.jointEvaluator));
        if (log.isInfoEnabled()){
            log.info("targetInterface [{}] used beanJoint and its subClassBean [{}] joined beanJoint", targetInterface, proxyBeanNames);
        }
        return proxyFactory.getProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return this.proxyInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<?> getProxyInterface() {
        return this.proxyInterface;
    }

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    public String[] getProxyBeanNames() {
        return this.proxyBeanNames;
    }

    public void setProxyBeanNames(String[] proxyBeanNames) {
        this.proxyBeanNames = proxyBeanNames;
    }

    public JointExpressionEvaluator getJointEvaluator() {
        return jointEvaluator;
    }

    public void setJointEvaluator(JointExpressionEvaluator jointEvaluator) {
        this.jointEvaluator = jointEvaluator;
    }
}
