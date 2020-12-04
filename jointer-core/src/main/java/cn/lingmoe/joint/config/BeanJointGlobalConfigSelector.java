package cn.lingmoe.joint.config;

import java.lang.annotation.Annotation;
import java.util.Objects;

import cn.lingmoe.joint.anno.EnableBeanJoint;
import cn.lingmoe.joint.constant.InterruptPoilcy;
import cn.lingmoe.joint.constant.UseReturnPoilcy;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author yukdawn@gmail.com
 */
@Component
public class BeanJointGlobalConfigSelector implements DeferredImportSelector {

    private final Class<?> annotationClass = EnableBeanJoint.class;

    @Override
    @SuppressWarnings("unchecked")
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(getAnnotationClass().getName()));
        if (Objects.isNull(attributes)){
            return new String[0];
        }
        InterruptPoilcy isGlobalInterrupt = attributes.getEnum("isGlobalInterrupt");
        UseReturnPoilcy isGlobalUseReturn = attributes.getEnum("isGlobalUseReturn");
        Class<? extends Annotation>[] jointBeanTypes = (Class<? extends Annotation>[]) attributes.getClassArray("jointBeanType");
        GlobalJointerConfigHelper.init(isGlobalInterrupt, isGlobalUseReturn, jointBeanTypes);
        return new String[0];
    }

    private Class<?> getAnnotationClass() {
        return this.annotationClass;
    }
}
