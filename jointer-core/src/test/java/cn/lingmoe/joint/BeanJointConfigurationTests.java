package cn.lingmoe.joint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.lingmoe.joint.anno.EnableBeanJoint;
import cn.lingmoe.joint.service.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {EnableBeanJoint.class})
@SpringBootApplication
@EnableBeanJoint
class BeanJointConfigurationTests {

    @Autowired
    private A a;

    @Test
    void contextLoads() {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println(a.say(Thread.currentThread().getName() + "order"));
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            executorService.execute(() -> {
                System.out.println(a.say(Thread.currentThread().getName() + "order" + finalI));
            });
        }
    }

}
