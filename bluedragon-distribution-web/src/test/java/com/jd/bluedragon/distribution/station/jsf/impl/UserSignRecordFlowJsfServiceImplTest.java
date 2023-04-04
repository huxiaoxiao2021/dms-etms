package com.jd.bluedragon.distribution.station.jsf.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.station.domain.UserSignFlowRequest;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class UserSignRecordFlowJsfServiceImplTest {
	@InjectMocks
	UserSignRecordFlowJsfServiceImpl userSignRecordFlowJsfService;
    @Mock
    UserSignRecordService userSignRecordService;
    @Mock
    PositionManager positionManager;
    
    static String userCode ="bjxings";
    static String scanUserCode = "1bjxings";
    static String positionCode1 = "GW0000010001";
    static String positionCode2 = "GW0000010002";
    static String msg = "test";
//    static Result<com.jdl.basic.api.domain.position.PositionData> positionResult;
    static JdCResponse<UserSignRecordData> lastUnSignOutRecordData;
    static {
//    	positionResult = new Result<com.jdl.basic.api.domain.position.PositionData>();
//    	positionResult.toSuccess();
//    	com.jdl.basic.api.domain.position.PositionData positionData = new com.jdl.basic.api.domain.position.PositionData();
//    	positionData.setGridCode("G0000001");
//    	positionData.setDefaultMenuCode("testMenuCode");
//    	positionResult.setData(positionData);
    	
    	lastUnSignOutRecordData = new JdCResponse<UserSignRecordData>();
    	lastUnSignOutRecordData.toSucceed();
    	UserSignRecordData unSignOutData = new UserSignRecordData();
    	unSignOutData.setPositionCode(positionCode2);
    	unSignOutData.setWorkName("工序02");
    	unSignOutData.setGridName("网格02");
    	lastUnSignOutRecordData.setData(unSignOutData);
    }
    private void setMockPositionData() {
    	when(positionManager.queryPositionWithIsMatchAppFunc(positionCode1)).thenReturn(null);
    }
    private void setLastUnSignOutRecordData() {
    	when(userSignRecordService.queryLastUnSignOutRecordData(any(UserSignQueryRequest.class))).thenReturn(lastUnSignOutRecordData);
    }
	/**
	 * @throws Exception
	 */
    @Test
    public void testAddSignFlow() throws Exception{
    	UserSignFlowRequest addRequest = new UserSignFlowRequest();
    	addRequest.setPositionCode(positionCode1);
    	addRequest.setUserCode(userCode);
    	setMockPositionData();
    	Result<Boolean> result = userSignRecordFlowJsfService.addSignFlow(addRequest);
    	Assert.assertTrue(JdCResponse.CODE_SUCCESS==result.getCode());
    	
    	System.out.println(JsonHelper.toJson(result));
    }
	/**
	 * @throws Exception
	 */
    @Test
    public void testQueryFlowPageList() throws Exception{
    	UserSignRecordFlowQuery query = new UserSignRecordFlowQuery();
    	query.setUserCode(userCode);
    	setMockPositionData();
    	Result<PageDto<UserSignRecordFlow>> result = userSignRecordFlowJsfService.queryFlowPageList(query);
    	Assert.assertTrue(JdCResponse.CODE_SUCCESS==result.getCode());
    	
    	System.out.println(JsonHelper.toJson(result));
    }    
}
