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
 * @ClassName TibetSendDetailConsumerTest
 * @Description
 * @Author wyh
 * @Date 2021/6/8 18:28
 **/
@RunWith(MockitoJUnitRunner.class)
public class TibetSendDetailConsumerTest {

    @InjectMocks
    private TibetSendDetailConsumer consumer;

    @Mock
    private TibetBizService tibetBizService;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Test
    public void testConsume() {
        String text = "{\n" +
                "    \"bizSource\": 2,\n" +
                "    \"boxCode\": \"BC1106806211450905178691\",\n" +
                "    \"createSiteCode\": 3074,\n" +
                "    \"createUser\": \"王永贤\",\n" +
                "    \"createUserCode\": 20084624,\n" +
                "    \"operateTime\": 1623140968684,\n" +
                "    \"packageBarcode\": \"JDV000700254029-1-1-\",\n" +
                "    \"receiveSiteCode\": 672302,\n" +
                "    \"sendCode\": \"910-39-82575296443827533\",\n" +
                "    \"source\": \"DMS\"\n" +
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
