package cn.lingmoe.joint.anno;

import java.lang.annotation.*;

import cn.lingmoe.joint.BeanJointConfiguration;
import cn.lingmoe.joint.constant.InterruptPoilcy;
import cn.lingmoe.joint.config.BeanJointGlobalConfigSelector;
import cn.lingmoe.joint.constant.UseReturnPoilcy;
import cn.lingmoe.joint.factory.JointDefinitionProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @author yukdawn@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
@Import({BeanJointGlobalConfigSelector.class, JointDefinitionProcessor.class, BeanJointConfiguration.class})
public @interface EnableBeanJoint {

    InterruptPoilcy isGlobalInterrupt() default InterruptPoilcy.FALSE;

    UseReturnPoilcy isGlobalUseReturn() default UseReturnPoilcy.FALSE;

    Class<? extends Annotation>[] jointBeanType() default {Service.class};

}
