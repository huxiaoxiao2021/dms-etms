package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.SealSyncOpenCloseSendTaskConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsSealCarStatusConsumer;
import com.jd.bluedragon.distribution.jy.dto.task.SealSyncOpenCloseSendTaskDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/23 16:45
 * @Description
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class SealSyncOpenCloseSendTaskConsumerTest {

    public static final Date OFF_TIME = new Date(System.currentTimeMillis() + 3600l * 10l);
    public static final Date DOWN_TIME = new Date(System.currentTimeMillis() + 3600l * 24l);

    @Autowired
    private SealSyncOpenCloseSendTaskConsumer consume;

    @Autowired
    private TmsSealCarStatusConsumer tmsSealCarStatusConsumer;

    @Test
    public void  test(){
        String str = "{\n" +
                "    \"batchCodes\": [\n" +
                "        \"40240-10186-20231117151374153\"\n" +
                "    ],\n" +
                "    \"billCode\": \"TJ23111709211498\",\n" +
                "    \"endSiteCode\": \"010K001\",\n" +
                "    \"endSiteId\": 10186,\n" +
                "    \"endSiteName\": \"北京凉水河快运中心\",\n" +
                "    \"operateSiteCode\": \"010F016\",\n" +
                "    \"operateSiteId\": 40240,\n" +
                "    \"operateSiteName\": \"北京通州分拣中心\",\n" +
                "    \"operateTime\": \"2023-11-17 16:47:22\",\n" +
                "    \"operateUserCode\": \"huzhihao3\",\n" +
                "    \"operateUserName\": \"胡志浩\",\n" +
                "    \"sealCarCode\": \"SC23111700038850\",\n" +
                "    \"sealCarType\": 20,\n" +
                "    \"source\": 1,\n" +
                "    \"startSiteCode\": \"010F016\",\n" +
                "    \"startSiteId\": 40240,\n" +
                "    \"startSiteName\": \"北京通州分拣中心\",\n" +
                "    \"status\": 10,\n" +
                "    \"transJobCode\": \"TJ23111709211498\",\n" +
                "    \"transJobItemCode\": \"TJ23111709211498-001\",\n" +
                "    \"transWay\": 2,\n" +
                "    \"transWorkItemCode\": \"TW23111700983599-001\",\n" +
                "    \"transportCode\": \"T220311001531\",\n" +
                "    \"vehicleNumber\": \"京GH3333\",\n" +
                "    \"volume\": 0.0,\n" +
                "    \"weight\": 0.0\n" +
                "}";

        Message message = new Message();
        message.setText(str);
        try {
            tmsSealCarStatusConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConsume() {

//        int t = 0;
//        while(t++<100){
//            String bizId = jyBizTaskSendVehicleService.genMainTaskBizId();
//            System.out.println(bizId);
//        }



        SealSyncOpenCloseSendTaskDto param = new SealSyncOpenCloseSendTaskDto();

//        param.setStatus(SealSyncOpenCloseSendTaskDto.STATUS_SEAL);
        param.setStatus(SealSyncOpenCloseSendTaskDto.STATUS_CANCELSEAL);
//        String batchCode = "40240-910-20231108001282640"; //运输任务·
        String batchCode = "40240-910-20231104071243343";  //自建任务
        List<String> batchCodeList = Arrays.asList(batchCode,"40240-910-20220615216354663");



        param.setSysTime(System.currentTimeMillis());
        param.setSealCarCode("SC001");
        param.setTransWorkItemCode("TW001");
        param.setOperateUserCode("wuyoude");
        param.setOperateUserName("吴友德");
        param.setBatchCodes(batchCodeList);
        param.setOperateTime(System.currentTimeMillis() - 3600 * 1000);
        param.setSingleBatchCode(batchCode);


        String str = "{\n" +
                "    \"batchCodes\": [\n" +
                "        \"40240-910-20231201142214392\"\n" +
                "    ],\n" +
                "    \"operateTime\": 1701413437000,\n" +
                "    \"operateUserCode\": \"huzhihao3\",\n" +
                "    \"operateUserName\": \"胡志浩\",\n" +
                "    \"sealCarCode\": \"SC23120100039011\",\n" +
                "    \"singleBatchCode\": \"40240-910-20231201142214392\",\n" +
                "    \"status\": 1,\n" +
                "    \"sysTime\": 1701413438407\n" +
                "}";

        int i = 0;
        while(i++ < 100) {
            try{
                Message message = new Message();
                message.setText(str);
                message.setBusinessId("40240-910-20231201142214392");
                consume.consume(message);
                 System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
