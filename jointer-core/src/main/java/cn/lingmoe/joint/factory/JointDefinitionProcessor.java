package cn.lingmoe.joint.factory;

import java.util.*;

import cn.lingmoe.joint.anno.Joint;
import cn.lingmoe.joint.config.GlobalJointerConfigHelper;
import cn.lingmoe.joint.evaluator.JointExpressionEvaluator;
import cn.lingmoe.joint.util.ArrayUtil;
import cn.lingmoe.joint.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.stereotype.Component;

/**
 * @author yukdawn@gmail.com
 */
@Component
public class JointDefinitionProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JointDefinitionProcessor.class);

    private static final Set<Class<?>> jointBeanInterfaceCache = new HashSet<>();

    public JointDefinitionProcessor() {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        String[] jointBeans = beanFactory.getBeanNamesForAnnotation(Joint.class);
        if (ArrayUtil.isEmpty(jointBeans)) {
            return;
        }
        for (String beanName : jointBeans) {
            if (checkBeanType(beanFactory, beanName)) {
                continue;
            }
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> clazz = Class.forName(className);
                // 递归创建bean定义
                do {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    clazz = clazz.getSuperclass();
                    if (ArrayUtil.isNotEmpty(interfaces)) {
                        for (Class<?> interfaceClazz : interfaces) {
                            this.processJointBeanImpl(interfaceClazz, beanFactory);
                        }
                    }
                } while (clazz != null);
            } catch (Exception var15) {
                logger.warn("JointBean take over failed!", var15);
            }
        }
    }

    private boolean checkBeanType(ConfigurableListableBeanFactory beanFactory, String beanName) {
        return Arrays.stream(GlobalJointerConfigHelper.getClazzs()).noneMatch(beanType -> beanFactory.findAnnotationOnBean(beanName, beanType) != null);
    }

    protected void processJointBeanImpl(Class<?> interfaceClazz, ConfigurableListableBeanFactory beanFactory) {
        // 如果已经存在则不进行二次bean定义
        if (jointBeanInterfaceCache.contains(interfaceClazz)) {
            return;
        }
        logger.info("JointDefinition take over the Bean Definition of [{}]" + interfaceClazz.toString());
        String[] jointBeanImpls = beanFactory.getBeanNamesForType(interfaceClazz);
        // 接口实现为空或者仅有一个实现直接返回，无需联动
        if (ArrayUtil.isEmpty(jointBeanImpls) || jointBeanImpls.length == 1) {
            return;
        }
        // 说明当前接口存在两个以上实现的实现
        if (jointBeanImpls.length > 1) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(JointBeanFactory.class);
            beanDefinition.getPropertyValues().addPropertyValue(JointBeanFactory.FIELD_PROXY_INTERFACE, interfaceClazz);
            beanDefinition.getPropertyValues().addPropertyValue(JointBeanFactory.FIELD_PROXY_BEAN_NAMES, jointBeanImpls);
            beanDefinition.getPropertyValues().addPropertyValue(JointBeanFactory.FIELD_JOINT_EVALUATOR, beanFactory.getBean(JointExpressionEvaluator.class));
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinition.setPrimary(true);
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)beanFactory;
            defaultListableBeanFactory.registerBeanDefinition(interfaceClazz.getSimpleName(), beanDefinition);
        }
        jointBeanInterfaceCache.add(interfaceClazz);
    }
}
