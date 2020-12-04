package cn.lingmoe.joint.anno;

import java.lang.annotation.*;

import cn.lingmoe.joint.constant.InterruptPoilcy;
import cn.lingmoe.joint.constant.UseReturnPoilcy;
import cn.lingmoe.joint.interceptor.JointBean;

/**
 * bean协同注解
 * @author yukdawn@gmail.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Joint {

    /**
     * 唯一标识，作为配置项可用于condition，实现自定义协作流程
     * @return 默认为beanName, 若参与协作的bean存在未标注{@link Joint}, 默认为{@link JointBean#DEFAULT_BEAN_NAME}
     */
    String value() default "";

    /**
     * @return bean执行顺序
     */
    int order() default 0;

    /**
     * @return 是否执行完毕后中断，中断后会直接返回
     */
    InterruptPoilcy isInterrupt() default InterruptPoilcy.NONE;

    /**
     * 使用表达式控制中断条件, 当isInterrupt设置为{@link InterruptPoilcy#EXPRESSION}时生效
     * 基础参数包括joint注解参数(#root.joint.*)及当前调用的方法参数(#root.args[*]).
     * @return 中断条件，使用spel表达式
     */
    String interruptCondition() default "";

    /**
     * @return 是否使用此bean的返回值
     */
    UseReturnPoilcy isUseReturn() default UseReturnPoilcy.NONE;

    /**
     * 使用表达式控制返回值, 当isUseReturn设置为{@link UseReturnPoilcy#EXPRESSION}时生效
     * 基础参数包括joint注解参数(#root.joint.*)及当前调用的方法参数(#root.args[*]).
     * @return 中断条件，使用spel表达式
     */
    String userReturnCondition() default "";

    /**
     * 基础参数包括joint注解参数(#root.joint.*)及当前调用的方法参数(#root.args[*]).
     * @return bean协同条件，使用spel表达式
     */
    String condition() default "";


}
