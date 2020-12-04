package cn.lingmoe.joint;

import cn.lingmoe.joint.evaluator.DefaultJointExpressionEvaluator;
import cn.lingmoe.joint.evaluator.JointExpressionEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yukdawn@gmail.com
 */
@Configuration
public class BeanJointConfiguration {

    @Bean
    @ConditionalOnMissingBean(JointExpressionEvaluator.class)
    public JointExpressionEvaluator jointExpressionEvaluator(){
        return new DefaultJointExpressionEvaluator();
    }
}
