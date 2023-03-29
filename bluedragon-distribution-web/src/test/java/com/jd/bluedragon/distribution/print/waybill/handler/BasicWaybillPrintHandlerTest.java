package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.BasicWaybillPrintHandler;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WaybillAttachmentDto;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class BasicWaybillPrintHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(BasicWaybillPrintHandlerTest.class);

    @InjectMocks
    BasicWaybillPrintHandler basicWaybillPrintHandler;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private BaseMinorManager baseMinorManager;

    @Mock
    private WaybillCommonServiceImpl waybillCommonService;

    @Mock
    private SiteService siteService;

    @Mock
    private WaybillPrintService waybillPrintService;

    @Mock
    private BaseService baseService;
    
    @Mock
    SysConfigService sysConfigService;
    
    @Mock
    private BaseMajorManager baseMajorManager;

    @Test
    public void testHandle(){
        /*构造参数*/
        WaybillPrintContext context = new WaybillPrintContext();
        WaybillPrintRequest request = new WaybillPrintRequest();
        request.setBarCode("JDV000488704633");
        context.setRequest(request);

        /*构造mock*/
        BaseEntity<BigWaybillDto> baseEntity  = this.makeWaybill();
        when(waybillQueryManager.getWaybillDataForPrint("JDV000488704633")).thenReturn(baseEntity);
        com.jd.bluedragon.common.domain.Waybill waybill = new com.jd.bluedragon.common.domain.Waybill();
        when(waybillCommonService.convWaybillWS(any(BigWaybillDto.class), anyBoolean(), anyBoolean(),anyBoolean(),anyBoolean())).thenReturn(waybill);
        when(baseMinorManager.getBaseTraderById(anyInt())).thenReturn(new BasicTraderInfoDTO());


        /*构造waybillCommonService依赖的mock*/
        when(siteService.getSiteNameByCode(null)).thenReturn(null);

        basicWaybillPrintHandler.handle(context);
    }

    /*
    * 构造运单数据
    * */
    BaseEntity<BigWaybillDto> makeWaybill(){
        BaseEntity<BigWaybillDto> result =  new BaseEntity<BigWaybillDto>();
        BigWaybillDto bigWaybillDto = new BigWaybillDto();
        WaybillVasDto waybillVasDto = new WaybillVasDto();
        waybillVasDto.setVasNo("festivalAttachment");
        bigWaybillDto.setWaybillVasList(Arrays.asList(waybillVasDto));

        bigWaybillDto.setWaybill(new Waybill());

        result.setData(bigWaybillDto);
        result.setResultCode(Constants.RESULT_SUCCESS);
        return result;
    }

    @Test
    public void testSetBasePrintInfoByWaybill(){
        /*构造参数*/
        BasePrintWaybill target = new BasePrintWaybill();
        target.setWaybillVasSign("1");
        target.setWaybillCode("JDV000488704633");
        com.jd.etms.waybill.domain.Waybill waybill = new Waybill();
        waybill.setWaybillSign("00000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        waybill.setWaybillCode("JDV000488704633");

        BaseEntity<BigWaybillDto> baseEntity  = this.makeWaybill();


        WaybillAttachmentDto waybillAttachmentDto = new WaybillAttachmentDto();
        waybillAttachmentDto.setAttachmentType(19);
        waybillAttachmentDto.setAttachmentUrl("test");
        BaseEntity<List<WaybillAttachmentDto>> waybillAttachments = new BaseEntity<List<WaybillAttachmentDto>>();
        waybillAttachments.setData(Arrays.asList(waybillAttachmentDto));

        /*构造mock*/
        when(siteService.getSiteNameByCode(null)).thenReturn(null);
        when(baseService.queryDmsBaseSiteByCode(null)).thenReturn(null);
        when(waybillQueryManager.getWaybillDataForPrint("JDV000488704633")).thenReturn(baseEntity);
        when(waybillQueryManager.getWaybillAttachmentByWaybillCodeAndType("JDV000488704633",19)).thenReturn(waybillAttachments);


        waybillCommonService.setBasePrintInfoByWaybill(target,waybill);
    }

}
