package cn.lingmoe.joint.evaluator;

import java.util.Map;

import cn.lingmoe.joint.anno.Joint;
import cn.lingmoe.joint.interceptor.JointBean;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author yukdawn@gmail.com
 */
public interface JointExpressionEvaluator {

    boolean condition(String expression, JointBean jointBean, MethodInvocation invocation);

    class JointBeanExpressionRootObject {

        private final Joint joint;

        public JointBeanExpressionRootObject(Joint joint) {
            this.joint = joint;
        }

        public Joint getJoint() {
            return joint;
        }
    }
}
