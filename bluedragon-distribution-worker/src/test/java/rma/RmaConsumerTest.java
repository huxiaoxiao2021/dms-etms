package rma;

import com.jd.bluedragon.distribution.consumer.senddetail.SendDetailConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 * Created by lixin39 on 2018/9/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class RmaConsumerTest {

    @Autowired
    private SendDetailConsumer sendDetailConsumer;

    @Test
    public void testConsumer(){
        Message message = new Message();
        message.setText("{\"boxCode\":\"VA66681425580-1-1-\",\"createSiteCode\":910,\"createUser\":\"邢松1\",\"createUserCode\":100531,\"operateTime\":1537610216869,\"packageBarcode\":\"VA66681425580-1-1-\",\"receiveSiteCode\":39,\"sendCode\":\"910-39-20180922170846366\",\"source\":\"DMS\"}");
        sendDetailConsumer.consume(message);
    }
}
