package cn.lingmoe.joint.config;

import java.lang.annotation.Annotation;

import cn.lingmoe.joint.constant.InterruptPoilcy;
import cn.lingmoe.joint.constant.UseReturnPoilcy;

/**
 * bean协同器全局配置工具类
 * @author yukdawn@gmail.com
 */
public class GlobalJointerConfigHelper {
    private static InterruptPoilcy interruptPoilcy;

    private static UseReturnPoilcy useReturnPoilcy;

    private static Class<? extends Annotation>[] clazzs;

    private GlobalJointerConfigHelper() throws InstantiationException {
        throw new InstantiationException();
    }

    protected static void init(InterruptPoilcy interruptPoilcy,
                               UseReturnPoilcy UseReturnPoilcy,
                               Class<? extends Annotation>[] clazzs){
        GlobalJointerConfigHelper.interruptPoilcy = interruptPoilcy;
        GlobalJointerConfigHelper.useReturnPoilcy = UseReturnPoilcy;
        GlobalJointerConfigHelper.clazzs = clazzs;
    }

    public static boolean isGlobalInterrupt() {
        return InterruptPoilcy.isTrue(interruptPoilcy);
    }

    public static boolean isGlobalUseReturn() {
        return UseReturnPoilcy.isTrue(useReturnPoilcy);
    }

    public static InterruptPoilcy getInterruptPoilcy() {
        return interruptPoilcy;
    }

    public static UseReturnPoilcy getUseReturnPoilcy() {
        return useReturnPoilcy;
    }

    public static Class<? extends Annotation>[] getClazzs() {
        return clazzs;
    }
}
