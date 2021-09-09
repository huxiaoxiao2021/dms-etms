package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.external.crossbow.itms.service.TibetBizService;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @ClassName TibetCancelSendDetailConsumerTest
 * @Description
 * @Author wyh
 * @Date 2021/6/8 22:00
 **/
@RunWith(MockitoJUnitRunner.class)
public class TibetCancelSendDetailConsumerTest {

    @InjectMocks
    private TibetCancelSendDetailConsumer consumer;

    @Mock
    private TibetBizService tibetBizService;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Test
    public void testConsume() {
        String text = "{\n" +
                "  \"packageBarcode\" : \"JDT000000307470-1-1-\",\n" +
                "  \"waybillCode\" : \"JDT000000307470\",\n" +
                "  \"sendCode\" : \"364605-910-20210122175843016\",\n" +
                "  \"operateTime\" : 1611309839226,\n" +
                "  \"operatorErp\" : \"hujiping1\"\n" +
                "}";
        Message message = new Message();
        message.setText(text);
        try {
            Mockito.when(tibetBizService.tibetModeSwitch(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
            Mockito.when(baseMajorManager.getBaseSiteBySiteId(Mockito.anyInt())).thenReturn(new BaseStaffSiteOrgDto());
            consumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
