package com.jd.bluedragon.distribution.station.gateway.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanForLoginRequest;
import com.jd.bluedragon.common.dto.station.ScanUserData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoComposeServiceImpl;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.CustomerAndConsignerInfoHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jdl.basic.common.utils.Result;
import com.jd.bluedragon.dms.utils.SendPayConstants;
import com.sun.el.stream.Stream;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.distribution.print.waybill.handler.CustomerAndConsignerInfoHandler;

@RunWith(MockitoJUnitRunner.class)
public class UserSignGatewayServiceImplTest {
	@InjectMocks
	UserSignGatewayServiceImpl userSignGatewayService;
    @Mock
    UserSignRecordService userSignRecordService;
    @Mock
    PositionManager positionManager;
    
    static String userCode ="bjxings";
    static String scanUserCode = "1bjxings";
    static String positionCode1 = "GW0000010001";
    static String positionCode2 = "GW0000010002";
    static String msg = "test";
    static Result<com.jdl.basic.api.domain.position.PositionData> positionResult;
    static JdCResponse<UserSignRecordData> lastUnSignOutRecordData;
    static {
    	positionResult = new Result<com.jdl.basic.api.domain.position.PositionData>();
    	positionResult.toSuccess();
    	com.jdl.basic.api.domain.position.PositionData positionData = new com.jdl.basic.api.domain.position.PositionData();
    	positionData.setGridCode("G0000001");
    	positionData.setDefaultMenuCode("testMenuCode");
    	positionResult.setData(positionData);
    	
    	lastUnSignOutRecordData = new JdCResponse<UserSignRecordData>();
    	lastUnSignOutRecordData.toSucceed();
    	UserSignRecordData unSignOutData = new UserSignRecordData();
    	unSignOutData.setPositionCode(positionCode2);
    	unSignOutData.setWorkName("工序02");
    	unSignOutData.setGridName("网格02");
    	lastUnSignOutRecordData.setData(unSignOutData);
    }
    private void setMockPositionData() {
    	when(positionManager.queryPositionWithIsMatchAppFunc(positionCode1)).thenReturn(positionResult);
    }
    private void setLastUnSignOutRecordData() {
    	when(userSignRecordService.queryLastUnSignOutRecordData(any(UserSignQueryRequest.class))).thenReturn(lastUnSignOutRecordData);
    }
	/**
	 * @throws Exception
	 */
    @Test
    public void testQueryPositionDataForLogin() throws Exception{
    	ScanForLoginRequest scanRequest = new ScanForLoginRequest();
    	scanRequest.setPositionCode(positionCode1);
    	scanRequest.setUserCode(userCode);
    	setMockPositionData();
    	JdCResponse<PositionData> result = userSignGatewayService.queryPositionDataForLogin(scanRequest);
    	Assert.assertEquals(JdCResponse.CODE_SUCCESS,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    	
    	setLastUnSignOutRecordData();
    	result = userSignGatewayService.queryPositionDataForLogin(scanRequest);
    	Assert.assertEquals(JdCResponse.CODE_CONFIRM,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    }
    @Test
    public void testQueryUserDataForLogin() throws Exception{
    	ScanForLoginRequest scanRequest = new ScanForLoginRequest();
    	scanRequest.setPositionCode(positionCode1);
    	scanRequest.setScanUserCode(scanUserCode);
    	JdCResponse<ScanUserData> result = userSignGatewayService.queryUserDataForLogin(scanRequest);
    	Assert.assertEquals(JdCResponse.CODE_SUCCESS,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    	
    	setLastUnSignOutRecordData();
    	result = userSignGatewayService.queryUserDataForLogin(scanRequest);
    	Assert.assertEquals(JdCResponse.CODE_CONFIRM,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    }
    @Test
    public void testCheckBeforeSignIn() throws Exception{
    	UserSignRequest userSignRequest = new UserSignRequest();
    	userSignRequest.setPositionCode(positionCode1);
    	userSignRequest.setScanUserCode(scanUserCode);
    	JdCResponse<UserSignRecordData> result = userSignGatewayService.checkBeforeSignIn(userSignRequest);
    	Assert.assertEquals(JdCResponse.CODE_SUCCESS,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    	
    	setLastUnSignOutRecordData();
    	result = userSignGatewayService.checkBeforeSignIn(userSignRequest);
    	Assert.assertEquals(JdCResponse.CODE_CONFIRM,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    }
    @Test
    public void testQueryLastUnSignOutRecordData() throws Exception{
    	UserSignQueryRequest query = new UserSignQueryRequest();
    	query.setPositionCode(positionCode1);
    	query.setUserCode(userCode);
    	setLastUnSignOutRecordData();
    	JdCResponse<UserSignRecordData> result = userSignGatewayService.queryLastUnSignOutRecordData(query);
    	Assert.assertEquals(JdCResponse.CODE_SUCCESS,result.getCode());
    	System.out.println(JsonHelper.toJson(result));
    }
}
