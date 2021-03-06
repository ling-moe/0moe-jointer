package cn.lingmoe.joint.service.impl;

import cn.lingmoe.joint.anno.Joint;
import cn.lingmoe.joint.service.A;
import org.springframework.stereotype.Service;

/**
 * @author yukdawn@gmail.com
 */
@Service
@Joint(condition = "#code.equals('mainorder')")
public class C implements A {

    @Override
    public String say(String code) {
        return "C hello, " + code;
    }
}
