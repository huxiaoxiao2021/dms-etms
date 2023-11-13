package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.SealSyncOpenCloseSendTaskConsumer;
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

    @Test
    public void testConsume() {

//        int t = 0;
//        while(t++<100){
//            String bizId = jyBizTaskSendVehicleService.genMainTaskBizId();
//            System.out.println(bizId);
//        }



        SealSyncOpenCloseSendTaskDto param = new SealSyncOpenCloseSendTaskDto();

//        param.setStatus(SealUnsealStatusSyncAppSendTaskMQDto.STATUS_SEAL);
        param.setStatus(SealSyncOpenCloseSendTaskDto.STATUS_UNSEAL);
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




        int i = 0;
        while(i++ < 100) {
            try{
                Message message = new Message();
                message.setText(JsonHelper.toJson(param));
                message.setBusinessId(param.getSingleBatchCode());
                consume.consume(message);
                 System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
