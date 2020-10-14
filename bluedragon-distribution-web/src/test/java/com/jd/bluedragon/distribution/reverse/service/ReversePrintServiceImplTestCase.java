package com.jd.bluedragon.distribution.reverse.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.eclp.EclpImportServiceManager;
import com.jd.bluedragon.distribution.business.service.BusinessReturnAdressService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reverse.domain.GuestBackSubTypeEnum;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeRequest;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeResponse;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.eclp.bbp.notice.domain.dto.BatchImportDTO;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.business.api.dto.request.BackAddressDTO;
/**
 * 
 * @author wuyoude
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ReversePrintServiceImplTestCase {
	@InjectMocks
	ReversePrintServiceImpl reversePrintServiceImpl;
	@Mock
    WaybillQueryManager waybillQueryManager;
	@Mock
	OBCSManager obcsManager;
	@Mock
	BaseMinorManager baseMinorManager;
	@Mock
	LDOPManager lDOPManager;
	@Mock
	EclpImportServiceManager eclpImportServiceManager;
	@Mock
	BusinessReturnAdressService businessReturnAdressService;
	/**
	 * 获取二次换单数据
	 * @throws Exception
	 */
    @Test
    public void testGetTwiceExchangeInfo() throws Exception{
    	
		String[] waybillCodes = {
			"V000000000011",
			"V000000000022",
			"V000000000033",
			"V000000000044",
			"V000000000055",
			"V000000000066",
		};
		String[] tWaybillCodes ={
				"V000000000011",
				"V000000000022",
				"V000000000033",
				"V000000000044",
				"V000000000055",
				"V000000000066",
		};
		GuestBackSubTypeEnum[] guestBackSubTypes ={
				GuestBackSubTypeEnum.SICK_01,
				GuestBackSubTypeEnum.CUSTOMER_01,
				GuestBackSubTypeEnum.CUSTOMER_02,
				GuestBackSubTypeEnum.CUSTOMER_03,
				GuestBackSubTypeEnum.PRE_SELL_01,
				GuestBackSubTypeEnum.PRE_SELL_01
		};
		String oldWaybillCode = "V000000000011";
		String waybillCode = "V000000000022";
		Integer busiId = 1;
		String busiCode = "010K010";
		TwiceExchangeRequest twiceExchangeRequest = new TwiceExchangeRequest();
		twiceExchangeRequest.setDmsSiteCode(910);
		twiceExchangeRequest.setDmsSiteName("马驹桥分拣中心");
		twiceExchangeRequest.setWaybillCode("V000000000011");
		BaseEntity<Waybill> oldWaybill = new BaseEntity<Waybill>();
		oldWaybill.setResultCode(1);
		oldWaybill.setData(new Waybill());
		oldWaybill.getData().setWaybillCode(oldWaybillCode);
		oldWaybill.getData().setBusiId(busiId);
		when(waybillQueryManager.getWaybillByReturnWaybillCode(Mockito.anyString())).thenReturn(oldWaybill);
		LocalClaimInfoRespDTO localClaimInfoRespDTO = new LocalClaimInfoRespDTO();
		localClaimInfoRespDTO.setGoodOwner(1);
		localClaimInfoRespDTO.setSettleSubjectCode("其他");
		when(obcsManager.getClaimListByClueInfo(1, "V000000000011")).thenReturn(localClaimInfoRespDTO);
		BasicTraderInfoDTO basicTraderInfoDTO = new BasicTraderInfoDTO();
		basicTraderInfoDTO.setTraderCode("010K010");
		basicTraderInfoDTO.setTraderName("测试商家名称");
		when(baseMinorManager.getBaseTraderById(busiId)).thenReturn(basicTraderInfoDTO);
		JdResult<List<BackAddressDTO>> businessReturnAdress = new JdResult<List<BackAddressDTO>>();
		businessReturnAdress.toSuccess();
		businessReturnAdress.setData(new ArrayList<BackAddressDTO>());
		businessReturnAdress.getData().add(new BackAddressDTO());
		businessReturnAdress.getData().get(0).setBackAddress("二次换单退货地址0010001001");
		businessReturnAdress.getData().get(0).setContractName("联系人姓名");
		businessReturnAdress.getData().get(0).setContractPhone("15010011002");
		when(lDOPManager.queryBackAddressByType(DmsConstants.RETURN_BACK_ADDRESS_TYPE_6, "010K010")).thenReturn(businessReturnAdress);
		JdResult<Boolean> sendResult = new JdResult<Boolean>();
		sendResult.toSuccess();
		sendResult.setData(true);
		BatchImportDTO noticeInfo = new BatchImportDTO();
		when(eclpImportServiceManager.batchImport(noticeInfo)).thenReturn(sendResult);
		System.err.println(waybillCode);
		JdResult<TwiceExchangeResponse> result = reversePrintServiceImpl.getTwiceExchangeInfo(twiceExchangeRequest);
		Assert.assertEquals(true,result.isSucceed());
		Assert.assertEquals("二次换单退货地址0010001001",result.getData().getAddress());
		Assert.assertEquals("联系人姓名",result.getData().getContact());
		Assert.assertEquals("15010011002",result.getData().getPhone());
		Assert.assertEquals("150^_^1002",result.getData().getHidePhone());
    }
}
