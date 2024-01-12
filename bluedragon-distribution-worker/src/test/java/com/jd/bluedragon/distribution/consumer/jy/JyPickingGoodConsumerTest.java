package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.task.aviation.TmsAviationPickingGoodConsumer;
import com.jd.bluedragon.distribution.consumer.jy.task.dto.AirTransportBillDto;
import com.jd.bluedragon.distribution.consumer.jy.task.dto.TmsAviationPickingGoodMqBody;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提货岗相关mq自测
 * @Author zhengchengfa
 * @Date 2023/8/23 16:45
 * @Description
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class JyPickingGoodConsumerTest  {

    @Autowired
    private TmsAviationPickingGoodConsumer tmsAviationPickingGoodConsumer;




    @Test
    public void tmsAviationPickingGoodConsumer() {
        List<TmsAviationPickingGoodMqBody> list = new ArrayList<>();
        for (int i = 1; i < 10; i ++) {

            TmsAviationPickingGoodMqBody mqBody = new TmsAviationPickingGoodMqBody();
            mqBody.setTplBillCode(String.format("%s000%s", DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyyyMMdd), i));
            mqBody.setFlightNumber(String.format("FN00%s", i/4 + 1));
            mqBody.setBeginNodeCode("START");
            mqBody.setBeginNodeName("始发机场测试");
            mqBody.setEndNodeCode("END");
            mqBody.setEndNodeName("目的机场测试");
            mqBody.setPlanTakeOffTime(DateHelper.addHours(DateHelper.getCurrentDayWithOutTimes(), -12));
            mqBody.setRealTakeOffTime(DateHelper.addHours(DateHelper.getCurrentDayWithOutTimes(), -10));
            mqBody.setPlanTouchDownTime(DateHelper.getCurrentDayWithOutTimes());
            mqBody.setRealTouchDownTime(null);//
            mqBody.setOperateType(10);
            mqBody.setDepartCargoAmount(5000);
            mqBody.setDepartCargoRealWeight(50d * 1000d);
            mqBody.setOperateTime(new Date());
            mqBody.setTransbillList(this.getAirTransportBillDto());


            list.add(mqBody);
        }


        while (true) {
            for (TmsAviationPickingGoodMqBody body : list) {

                Message message = new Message();
                message.setBusinessId(body.getTplBillCode());
                message.setText(JsonHelper.toJson(body));
                try {
                    tmsAviationPickingGoodConsumer.consume(message);
                } catch (Exception e) {
                    System.out.println("纳尼，发现error");
                    e.printStackTrace();
                }
            }
        }
    }


    private List<AirTransportBillDto> getAirTransportBillDto() {
        List<AirTransportBillDto> transbillList = new ArrayList<>();

        AirTransportBillDto dto1 = new AirTransportBillDto();
        dto1.setBatchCode("910-40240-20220621176414243");
        dto1.setSealCarCode("SC22062100017955");
        dto1.setBeginNodeCode("010F002");//910
        dto1.setBeginNodeName("北京马驹桥分拣中心");
        dto1.setEndNodeCode("010F016");//40240
        dto1.setEndNodeName("北京通州分拣中心");
        dto1.setTransportCode("T180929000517");
        transbillList.add(dto1);

        AirTransportBillDto dto2 = new AirTransportBillDto();
        dto2.setBatchCode("910-40240-20220801098213455");
        dto2.setSealCarCode("SC22062100017955");
        dto2.setBeginNodeCode("010F002");//910
        dto2.setBeginNodeName("北京马驹桥分拣中心");
        dto2.setEndNodeCode("010F016");//40240
        dto2.setEndNodeName("北京通州分拣中心");
        dto2.setTransportCode("T180929000517");
        transbillList.add(dto2);

        AirTransportBillDto dto3 = new AirTransportBillDto();
        dto3.setBatchCode("910-10186-20211214182344566");
        dto3.setSealCarCode("SC21121400011833");
        dto3.setBeginNodeCode("010F002");//910
        dto3.setBeginNodeName("北京马驹桥分拣中心");
        dto3.setEndNodeCode("010K001");//10186
        dto3.setEndNodeName("北京凉水河快运中心");
        dto3.setTransportCode("T211108001405");
        transbillList.add(dto3);

        return transbillList;
    }



}


