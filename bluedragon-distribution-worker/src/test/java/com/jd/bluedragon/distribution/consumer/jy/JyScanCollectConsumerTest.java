package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.collectNew.JyScanCollectConsumer;
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

public class JyScanCollectConsumerTest {


    public static final String pdaScanPackageJson = "{\n" +
            "\"operatorErp\" : \"shipeilin\",\n" +
            "\"operatorName\" : \"石培林\",\n" +
            "\"operateSiteId\" : 223094,\n" +
            "\"operateSiteName\" : \"北京马驹桥接货仓\",\n" +
            "\"operateTime\" : 1687919407542,\n" +
            "\"barCode\" : \"JDVA00272331207-1-15-\",\n" +
            "\"barCodeType\" : \"102\",\n" +
            "\"codeType\" : \"102\",\n" +
            "\"mainTaskBizId\" : \"SST23062100000032\",\n" +
            "\"detailTaskBizId\" : \"TW23062100962018-001\",\n" +
            "\"sendCode\" : \"223094-40240-20230621146414064\",\n" +
            "\"jyPostType\" : \"WAREHOUSE_SEND_POSITION\",\n" +
            "\"bizSource\" : \"pda_scan\",\n" +
            "\"waybillCode\" : \"JDVA00272331207\",\n" +
            "\"packageCode\" : \"JDVA00272331207-1-15-\"\n" +
            "}";

    public static final String pdaScanWaybillJson = "{\n" +
            "\"operatorErp\" : \"wuyoude\",\n" +
            "\"operatorName\" : \"吴有德\",\n" +
            "\"operateSiteId\" : 40240,\n" +
            "\"operateSiteName\" : \"北京通州分拣中心\",\n" +
            "\"operateTime\" : 1686310155417,\n" +
            "\"barCode\" : \"JD0003420475846-1-1-\",\n" +
            "\"barCodeType\" : \"101\",\n" +
            "\"codeType\" : \"101\",\n" +
            "\"mainTaskBizId\" : \"SST22081600000007\",\n" +
            "\"detailTaskBizId\" : \"TW22081600806720-001\",\n" +
            "\"sendCode\" : \"40240-910-20220816028362740\",\n" +
            "\"jyPostType\" : \"104\",\n" +
            "\"bizSource\" : \"pda_scan\"\n" +
            "}";


    public static final String pdaScanBoxJson = "{\n" +
            "\"operatorErp\" : \"wuyoude\",\n" +
            "\"operatorName\" : \"吴有德\",\n" +
            "\"operateSiteId\" : 40240,\n" +
            "\"operateSiteName\" : \"北京通州分拣中心\",\n" +
            "\"operateTime\" : 1686310155417,\n" +
            "\"barCode\" : \"BC1001230602280000401314\",\n" +
            "\"barCodeType\" : \"104\",\n" +
            "\"codeType\" : \"104\",\n" +
            "\"mainTaskBizId\" : \"SST22081600000007\",\n" +
            "\"detailTaskBizId\" : \"TW22081600806720-001\",\n" +
            "\"sendCode\" : \"40240-910-20220816028362740\",\n" +
            "\"jyPostType\" : \"104\",\n" +
            "\"bizSource\" : \"pda_scan\"\n" +
            "}";

    public static final String mqWaybillToPackageJson = "{\n" +
            "    \"barCode\": \"JD0003420475846-1-1-\",\n" +
            "    \"barCodeType\": \"101\",\n" +
            "    \"bizSource\": \"mq_waybill_split\",\n" +
            "    \"codeType\": \"102\",\n" +
            "    \"collectionCode\": \"JQ23061217273200032\",\n" +
            "    \"detailTaskBizId\": \"TW22081600806720-001\",\n" +
            "    \"jyPostType\": \"104\",\n" +
            "    \"mainTaskBizId\": \"SST22081600000007\",\n" +
            "    \"operateSiteId\": 40240,\n" +
            "    \"operateSiteName\": \"北京通州分拣中心\",\n" +
            "    \"operateTime\": 1686310155417,\n" +
            "    \"operatorErp\": \"wuyoude\",\n" +
            "    \"operatorName\": \"吴有德\",\n" +
            "    \"packageCode\": \"JD0003420475846-1-1-\",\n" +
            "    \"sendCode\": \"40240-910-20220816028362740\",\n" +
            "    \"waybillCode\": \"JD0003420475846\"\n" +
            "}";




    @Autowired
    private JyScanCollectConsumer jyScanCollectConsumer;




    @Test
    public void consume() {
        while (true) {


//            String businessId = "JD0003420475846-1-1-:104:40240-910-20220816028362740:pda_scan";
            String businessId = "JDVA00272331207-1-15-:WAREHOUSE_SEND_POSITION:223094-40240-20230621146414064:pda_scan";



            try {
                Message message = new Message();
                message.setText(pdaScanPackageJson);
//                message.setText(pdaScanWaybillJson);
//                message.setText(mqWaybillToPackageJson);
//                message.setText(pdaScanBoxJson);
                message.setBusinessId(businessId);
                jyScanCollectConsumer.consume(message);
            } catch (Exception e) {
                System.out.println("服务异常!");
                e.printStackTrace();
            }
        }
    }
}
