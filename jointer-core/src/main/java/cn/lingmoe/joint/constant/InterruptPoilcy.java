package cn.lingmoe.joint.constant;

/**
 * @author yukdawn@gmail.com
 */
public enum InterruptPoilcy {
    TRUE,
    FALSE,
    EXPRESSION,
    NONE;

    public static boolean isTrue(InterruptPoilcy interruptPoilcy){
        return InterruptPoilcy.TRUE.equals(interruptPoilcy);
    }

    public static boolean isNone(InterruptPoilcy interruptPoilcy){
        return InterruptPoilcy.NONE.equals(interruptPoilcy);
    }
}
