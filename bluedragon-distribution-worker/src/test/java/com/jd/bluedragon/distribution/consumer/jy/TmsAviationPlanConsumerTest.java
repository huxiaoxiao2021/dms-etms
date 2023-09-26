package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.AirTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.CargoTypeEnum;
import com.jd.bluedragon.distribution.consumer.jy.task.aviation.TmsAviationPlanConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.TmsAviationPlanDto;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2023/8/23 16:45
 * @Description
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class TmsAviationPlanConsumerTest {

    public static final Date OFF_TIME = new Date(System.currentTimeMillis() + 3600l * 10l);
    public static final Date DOWN_TIME = new Date(System.currentTimeMillis() + 3600l * 24l);

    @Autowired
    private TmsAviationPlanConsumer tmsAviationPlanConsumer;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;

    @Test
    public void testConsume() {

//        int t = 0;
//        while(t++<100){
//            String bizId = jyBizTaskSendVehicleService.genMainTaskBizId();
//            System.out.println(bizId);
//        }

        TmsAviationPlanDto param = new TmsAviationPlanDto();
        param.setStartNodeCode("010F002");//910
        param.setStartNodeName("马驹桥分拣中心cs");
        param.setFlightNumber("CA-1001");
        param.setTakeOffTime(new Date());
        param.setTouchDownTime(new Date());
        param.setAirCompanyCode("nan.hang");
        param.setAirCompanyName("南方航空cs");
        param.setBeginNodeCode("bj0001");
        param.setBeginNodeName("北京首都机场cs");
        param.setEndNodeCode("bj1099");
        param.setEndNodeName("京东总部2B-14机场");
        param.setCarrierCode("JD.Air");
        param.setCarrierName("京东航空");
        param.setBookingWeight(1000d);
        param.setCargoType(CargoTypeEnum.SPECIAL_A.getCode());
        param.setAirType(AirTypeEnum.AIR_TYPE_BULK.getCode());

        int i = 100;
        while(i++ < 200) {
            try{
                String dch = DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss);
                param.setBookingCode("DCH" + i + dch);
                Message message = new Message();
                message.setText(JsonHelper.toJson(param));
                message.setBusinessId(param.getBookingCode());
                tmsAviationPlanConsumer.consume(message);

                param.setBookingWeight(param.getBookingWeight() - 10);
                Message message1 = new Message();
                message1.setText(JsonHelper.toJson(param));
                message1.setBusinessId(param.getBookingCode());
                tmsAviationPlanConsumer.consume(message1);

                 System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
