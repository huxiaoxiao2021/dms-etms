package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.collectNew.JyCancelScanCollectConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.JyCancelScanConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author zhengchengfa
 * @Date 2023/6/12 14:23
 * @Description
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")

public class JyCancelScanConsumerTest {


    @Autowired
    private JyCancelScanConsumer jyCancelScanConsumer;

    @Autowired
    private JyCancelScanCollectConsumer jyCancelScanCollectConsumer;

    public static final String CANCEL_ALL_SELECT_PDA = "{\n" +
            "    \"bizSource\": \"pdaScan\",\n" +
            "    \"buQiAllSelectFlag\": true,\n" +
            "    \"collectionCodes\": [\n" +
            "        \"JQ23061217273200032\"\n" +
            "    ],\n" +
            "    \"jyPostType\": \"104\",\n" +
            "    \"mainTaskBizId\": \"SST22081600000007\",\n" +
            "    \"operateSiteId\": 223094,\n" +
            "    \"operateTime\": 1686734380441,\n" +
            "    \"operatorErp\": \"wuyoude\",\n" +
            "    \"operatorName\": \"北京马驹桥接货仓\"\n" +
            "}";

    public static final String CANCEL_PACKAGE_PDA = "{\n" +
            "    \"barCode\": \"JD0003420509939-1-1-\",\n" +
            "    \"barCodeType\": \"102\",\n" +
            "    \"bizSource\": \"pdaScan\",\n" +
            "    \"buQiAllSelectFlag\": false,\n" +
            "    \"jyPostType\": \"104\",\n" +
            "    \"mainTaskBizId\": \"SST22081600000007\",\n" +
            "    \"operateSiteId\": 223094,\n" +
            "    \"operateTime\": 1686735300853,\n" +
            "    \"operatorErp\": \"wuyoude\",\n" +
            "    \"operatorName\": \"北京马驹桥接货仓\"\n" +
            "}";

    @Test
    public void consume() {
        while (true) {


            String packageBusinessId = "businessId=JD0003420509939-1-1-:SST22081600000007:104";
            String allSelectBusinessId = "selectAll:SST22081600000007:104";

            try {
                Message message1 = new Message();
                message1.setText(CANCEL_ALL_SELECT_PDA);
                message1.setBusinessId(allSelectBusinessId);
//                jyCancelScanConsumer.consume(message1);

                Message message2 = new Message();
                message2.setText(CANCEL_PACKAGE_PDA);
                message2.setBusinessId(packageBusinessId);
                jyCancelScanConsumer.consume(message2);
            } catch (Exception e) {
                System.out.println("服务异常!");
                e.printStackTrace();
            }
        }
    }










    public static final String CANCEL_PACKAGE = "{\n" +
            "  \"operatorErp\" : \"wuyoude\",\n" +
            "  \"operatorName\" : \"北京马驹桥接货仓\",\n" +
            "  \"operateSiteId\" : 223094,\n" +
            "  \"operateTime\" : 1686735300853,\n" +
            "  \"barCode\" : \"JD0003420509939-1-1-\",\n" +
            "  \"barCodeType\" : \"102\",\n" +
            "  \"mainTaskBizId\" : \"SST22081600000007\",\n" +
            "  \"jyPostType\" : \"104\"\n" +
            "}";
    public static final String CANCEL_WAYBILL = "{\n" +
            "  \"operatorErp\" : \"wuyoude\",\n" +
            "  \"operatorName\" : \"北京马驹桥接货仓\",\n" +
            "  \"operateSiteId\" : 223094,\n" +
            "  \"operateTime\" : 1686735300853,\n" +
            "  \"barCode\" : \"JD0003420509939\",\n" +
            "  \"barCodeType\" : \"101\",\n" +
            "  \"mainTaskBizId\" : \"SST22081600000007\",\n" +
            "  \"jyPostType\" : \"104\"\n" +
            "}";



    @Test
    public void consumeCollect() {
        while (true) {



            try {
                Message message1 = new Message();
                message1.setText(CANCEL_PACKAGE);
                jyCancelScanCollectConsumer.consume(message1);

                Message message2 = new Message();
                message2.setText(CANCEL_WAYBILL);
                jyCancelScanCollectConsumer.consume(message2);
            } catch (Exception e) {
                System.out.println("服务异常!");
                e.printStackTrace();
            }
        }
    }







}
