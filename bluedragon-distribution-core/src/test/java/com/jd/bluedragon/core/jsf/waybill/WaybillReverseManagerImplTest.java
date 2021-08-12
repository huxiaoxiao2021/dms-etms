package com.jd.bluedragon.core.jsf.waybill;

import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.receive.api.dto.OrderMsgDTO;
import com.jd.ql.dms.receive.api.jsf.GetOrderMsgServiceJsf;

@RunWith(MockitoJUnitRunner.class)
/**
 * 测试用例
 * @author wuyoude
 *
 */
public class WaybillReverseManagerImplTest {
    private static final Logger log = LoggerFactory.getLogger(WaybillReverseManagerImplTest.class);

    @InjectMocks
    WaybillReverseManagerImpl waybillReverseManagerImpl;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private GetOrderMsgServiceJsf getOrderMsgServiceJsf;

    @Mock
    private LDOPManager lDOPManager;

    @Mock
    private SiteService siteService;

    @Mock
    private WaybillPrintService waybillPrintService;

    @Mock
    private BaseService baseService;
    
    private static String oldWaybillCode = "JDO00000000001";
    private static String oldWaybillCodeToNew = oldWaybillCode+"-N";
    private static String newWaybillCode = "JDN00000000001";
    private static String newWaybillCodeToNew = newWaybillCode + "-N";
    
    @Before
    public void init(){
    	Waybill oldWaybill = new Waybill();
    	oldWaybill.setWaybillCode(oldWaybillCode);
    	oldWaybill.setWaybillExt(new WaybillExt());
    	oldWaybill.getWaybillExt().setOmcOrderCode(null);
    	when(waybillQueryManager.queryWaybillByWaybillCode(oldWaybillCode)).thenReturn(oldWaybill);
    	
    	OrderMsgDTO oldOrderMsgDTO = new OrderMsgDTO();
    	oldOrderMsgDTO.setDeliveryId(oldWaybillCodeToNew);
    	when(getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId(oldWaybillCode)).thenReturn(oldOrderMsgDTO);
    	
    	Waybill newWaybill = new Waybill();
    	newWaybill.setWaybillCode(newWaybillCode);
    	newWaybill.setWaybillExt(new WaybillExt());
    	newWaybill.getWaybillExt().setOmcOrderCode(newWaybillCode);
    	when(waybillQueryManager.queryWaybillByWaybillCode(newWaybillCode)).thenReturn(newWaybill);
    	
    	OrderMsgDTO newOrderMsgDTO = new OrderMsgDTO();
    	newOrderMsgDTO.setDeliveryId(newWaybillCodeToNew);
    	when(getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId(newWaybillCode)).thenReturn(newOrderMsgDTO);
    	
    	BaseEntity<BigWaybillDto> oldBigWaybillDto = new BaseEntity<BigWaybillDto>();
    	oldBigWaybillDto.setData(new BigWaybillDto());
    	oldBigWaybillDto.getData().setWaybill(oldWaybill);
    	WChoice a = new WChoice();
    	when(waybillQueryManager.getDataByChoice(oldWaybillCode,a)).thenReturn(oldBigWaybillDto);
    	
    	BaseEntity<BigWaybillDto> newBigWaybillDto = new BaseEntity<BigWaybillDto>();
    	newBigWaybillDto.setData(new BigWaybillDto());
    	newBigWaybillDto.getData().setWaybill(newWaybill);
    	when(waybillQueryManager.getDataByChoice(newWaybillCode,a)).thenReturn(newBigWaybillDto);
    	
    }
    
    @Test
    public void testGetQuickProduceWabillFromDrec(){
    	com.jd.bluedragon.common.domain.Waybill oldWaybill = waybillReverseManagerImpl.getQuickProduceWabillFromDrec(oldWaybillCode);
    	com.jd.bluedragon.common.domain.Waybill newWaybill = waybillReverseManagerImpl.getQuickProduceWabillFromDrec(newWaybillCode);
    }
    @Test
    public void testWaybillReverse(){
    	DmsWaybillReverseDTO dmsWaybillReverseDTO = waybillReverseManagerImpl.makeWaybillReverseDTO("waybillCode", 1, "operatorName", new Date(), 1, 1, 1, true);
    	waybillReverseManagerImpl.waybillReverse(dmsWaybillReverseDTO, new JdResponse<Boolean>());
    	waybillReverseManagerImpl.waybillReverse(dmsWaybillReverseDTO, new StringBuilder());
    	waybillReverseManagerImpl.queryReverseWaybill(dmsWaybillReverseDTO, new StringBuilder());
    	waybillReverseManagerImpl.queryWaybillCodeByOldWaybillCode(oldWaybillCode);
    }
}
