package cn.lingmoe.joint.constant;

/**
 * @author yukdawn@gmail.com
 */
public enum UseReturnPoilcy {
    TRUE,
    FALSE,
    EXPRESSION,
    NONE;

    public static boolean isTrue(UseReturnPoilcy useReturnPoilcy){
        return UseReturnPoilcy.TRUE.equals(useReturnPoilcy);
    }

    public static boolean isNone(UseReturnPoilcy useReturnPoilcy){
        return UseReturnPoilcy.NONE.equals(useReturnPoilcy);
    }
}
