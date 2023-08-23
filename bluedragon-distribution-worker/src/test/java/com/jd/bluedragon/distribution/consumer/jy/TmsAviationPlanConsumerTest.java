package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.AirTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.CargoTypeEnum;
import com.jd.bluedragon.distribution.consumer.jy.task.aviation.TmsAviationPlanConsumer;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.send.TmsAviationPlanDto;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
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

    @Test
    public void testConsume() {
        String bc = "DCH20230823000";
        TmsAviationPlanDto param = new TmsAviationPlanDto();
        param.setBookingCode("DCH20230823000");
        param.setStartNodeCode("010F002");//910
        param.setStartNodeName("马驹桥分拣中心cs");
        param.setFlightNumber("CA-1001");
        param.setTakeOffTime(OFF_TIME);
        param.setTouchDownTime(DOWN_TIME);
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

        int i = 0;
        while(i++ < 100) {
            try{
                param.setBookingCode(bc + i);
                Message message = new Message();
                message.setText(JsonHelper.toJson(param));
                message.setBusinessId(param.getBookingCode());
                tmsAviationPlanConsumer.consume(message);

                param.setBookingWeight(param.getBookingWeight() - i);
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
