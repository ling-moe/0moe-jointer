package cn.lingmoe.joint.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.lingmoe.joint.constant.InterruptPoilcy;
import cn.lingmoe.joint.anno.Joint;
import cn.lingmoe.joint.constant.UseReturnPoilcy;
import cn.lingmoe.joint.config.GlobalJointerConfigHelper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 参与协作的bean元数据
 * @author yukdawn@gmail.com
 */
public class JointBean {

    public static final String DEFAULT_BEAN_NAME = "default";

    final Joint joint;
    final String value;
    final int order;
    final InterruptPoilcy isInterrupt;
    final String interruptCondition;
    final UseReturnPoilcy isUseReturn;
    final String userReturnCondition;
    final String condition;
    final String beanName;
    final Object bean;
    final List<Method> declaredMethodList;

    public JointBean(Joint joint, String beanName, Object bean) {
        Assert.notNull(beanName, "beanName not null");
        Assert.notNull(bean, "bean not null");
        if (Objects.isNull(joint)){
            this.joint = null;
            this.value = DEFAULT_BEAN_NAME;
            this.order = Integer.MIN_VALUE;
            this.isInterrupt = GlobalJointerConfigHelper.getInterruptPoilcy();
            this.isUseReturn = GlobalJointerConfigHelper.getUseReturnPoilcy();
            this.condition = "";
            this.interruptCondition = "";
            this.userReturnCondition = "";
        }else {
            this.joint = joint;
            this.value = StringUtils.hasText(joint.value()) ? joint.value() : beanName;
            this.order = joint.order();
            this.isInterrupt = joint.isInterrupt();
            this.isUseReturn = joint.isUseReturn();
            this.condition = joint.condition();
            this.interruptCondition = joint.interruptCondition();
            this.userReturnCondition = joint.userReturnCondition();
        }
        this.beanName = beanName;
        this.bean = bean;
        this.declaredMethodList = Arrays.asList(bean.getClass().getDeclaredMethods());
    }

    public int getOrder() {
        return order;
    }

    public String getBeanName() {
        return beanName;
    }

    public Joint getJoint() {
        return joint;
    }

    public Object getBean() {
        return bean;
    }
}
