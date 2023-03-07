package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.MessageFormat;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/2/10 22:52
 */
@RunWith(MockitoJUnitRunner.class)
public class OverLengthRemindHandlerTest {

    @InjectMocks
    private OverLengthRemindHandler overLengthRemindHandler;

    private WaybillPrintContext context;

    private double maxLength = 160;
    private double maxWidth = 100;
    private double maxHigh = 100;
    private String waybillSign = "60001000310800010000000050008020000030000002004000002002010000000000001000000010000300000000100000000600000010000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private String traderSign = "000010000030030070100002040001060110000000ed000001000011001010010100120011010000000000000011111001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    @Before
    public void before(){
        context = new WaybillPrintContext();

        WaybillPrintRequest request = new WaybillPrintRequest();
        WeightOperFlow weightOperFlow = new WeightOperFlow();
        weightOperFlow.setLength(maxLength);
        weightOperFlow.setWidth(maxWidth);
        weightOperFlow.setHigh(maxHigh);
        request.setWeightOperFlow(weightOperFlow);
        context.setRequest(request);

        Waybill waybill = new Waybill();
        waybill.setWaybillSign(waybillSign);
        context.setWaybill(waybill);

        context.setTraderSign(traderSign);

        context.setResponse(new WaybillPrintResponse());
    }

    @Test
    public void handler(){
        try {
            InterceptResult<String> interceptResult = overLengthRemindHandler.handle(context);
            Assert.assertEquals(interceptResult.getStatus(),interceptResult.STATUS_WEAK_PASSED);
            Assert.assertEquals(interceptResult.getMessage(),
                    MessageFormat.format(WaybillPrintMessages.MESSAGE_PACKAGE_OVER_LENGTH_REMIND,150,maxWidth,maxHigh));
        }catch (Exception e){
            Assert.assertTrue(Boolean.FALSE);
        }

    }

}
