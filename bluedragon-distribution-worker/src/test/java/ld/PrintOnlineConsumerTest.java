package ld;

import com.jd.bluedragon.distribution.consumer.print.PrintOnlineConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @program: bluedragon-distribution
 * @description: 线上签单测
 * @author: liuduo8
 * @create: 2020-02-10 09:42
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class PrintOnlineConsumerTest {


    @Autowired
    private PrintOnlineConsumer printOnlineConsumer;

    @Test
    public void testMQ(){
        Message message = new Message();
        message.setBusinessId("1");
        message.setText("1");
        try {
            printOnlineConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //第二次消费重复
        try {
            printOnlineConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //第三次 修改业务主键 正常
        message.setBusinessId("2");

        try {
            printOnlineConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
