package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/2/10 21:07
 */
@RunWith(MockitoJUnitRunner.class)
public class SpecialTextWaybillHandlerTest {

    @InjectMocks
    private SpecialTextWaybillHandler specialTextWaybillHandler;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    private WaybillPrintContext context;
    private BaseEntity<Waybill> baseEntity;

    @Before
    public void before(){
        context = new WaybillPrintContext();
        WaybillPrintRequest request = new WaybillPrintRequest();
        request.setOperateType(100104);
        context.setRequest(request);

        WaybillPrintResponse printInfo = new WaybillPrintResponse();
        printInfo.setPrepareSiteCode(910);
        printInfo.setPrepareSiteName("");
        context.setResponse(printInfo);

        BasePrintWaybill basePrintWaybill = new BasePrintWaybill();
        basePrintWaybill.setDmsBusiAlias("CMBC");
        context.setBasePrintWaybill(basePrintWaybill);

        BigWaybillDto bigWaybillDto = new BigWaybillDto();
        Waybill newWaybill = new Waybill();
        newWaybill.setWaybillCode("JDV000050736223");
        bigWaybillDto.setWaybill(newWaybill);
        context.setBigWaybillDto(bigWaybillDto);

        baseEntity = new BaseEntity<Waybill>(1);
        Waybill oldWaybill = new Waybill();
        oldWaybill.setWaybillCode("JDV000050736195");
        baseEntity.setData(oldWaybill);
    }

    @Test
    public void getPopularizeMatrixCode(){
        try {
            when(waybillQueryManager.getWaybillByReturnWaybillCode(Mockito.anyString())).thenReturn(baseEntity);
            specialTextWaybillHandler.handle(context);
            WaybillPrintResponse response = context.getResponse();
            Assert.assertEquals(response.getPopularizeMatrixCode(),baseEntity.getData().getWaybillCode());
        }catch (Exception e){
            Assert.assertTrue(Boolean.FALSE);
        }

    }
}
