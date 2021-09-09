package com.jd.bluedragon.distribution;

import com.jd.bluedragon.distribution.consumer.syncPictureInfo.SyncPictureInfoConsumer;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/9/7 6:18 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class SyncPictureInfoConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(SyncPictureInfoConsumerTest.class);

    @Autowired
    private SyncPictureInfoConsumer syncPictureInfoConsumer;

    @Test
    public void consumer() {
        try {
            String text = "{\n" +
                    "    \"waybillOrPackCode\":\"JDV000705034485-3-3-\",\n" +
                    "    \"siteCode\":\"910\",\n" +
                    "    \"url\":\"http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D\"\n" +
                    "}";
            SyncPictureInfoConsumer.PictureInfoMq pictureInfoMq = JsonHelper.fromJsonUseGson(text, SyncPictureInfoConsumer.PictureInfoMq.class);
            Message message = new Message();
            message.setText(JsonHelper.toJson(pictureInfoMq));

            syncPictureInfoConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("抽检包裹图片上传处理异常!", e);
            Assert.fail();
        }
    }
}
