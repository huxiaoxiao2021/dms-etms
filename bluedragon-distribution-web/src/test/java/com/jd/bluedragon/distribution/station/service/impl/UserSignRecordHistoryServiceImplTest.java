package com.jd.bluedragon.distribution.station.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

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
import com.jd.bluedragon.distribution.station.service.impl.UserSignRecordHistoryServiceImpl;
import com.jd.bluedragon.utils.easydata.EasyDataClientUtil;
import com.jd.bluedragon.utils.easydata.UserSignRecordEasyDataConfig;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class UserSignRecordHistoryServiceImplTest {
	@InjectMocks
	UserSignRecordHistoryServiceImpl userSignRecordHistoryService;
	@Mock
	EasyDataClientUtil easyDataClientUtil;
    @Mock
    UserSignRecordEasyDataConfig userSignRecordEasyDataConfig;
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
    	easyDataClientUtil.setRestClient(new RestTemplate());
    	when(userSignRecordEasyDataConfig.getApiGroupName()).thenReturn("work_station_dim");
    	when(userSignRecordEasyDataConfig.getAppToken()).thenReturn("c2a2f5f245cf0e67558f04304606bffd");
    	when(userSignRecordEasyDataConfig.getQueryCountForFlow()).thenReturn("queryCountForFlow");
    	when(userSignRecordEasyDataConfig.getQueryDataListForFlow()).thenReturn("queryDataListForFlow");
    	when(userSignRecordEasyDataConfig.getQueryByIdForFlow()).thenReturn("queryByIdForFlow");
    	when(userSignRecordEasyDataConfig.getQueryCountForCheckSignTime()).thenReturn("queryCountForCheckSignTime");
    }
	/**
	 * @throws Exception
	 */
    @Test
    public void testAddSignFlow() throws Exception{
    	UserSignRecordFlowQuery query = new UserSignRecordFlowQuery();
    	query.setUserCode(userCode);
    	setMockPositionData();
    	Integer querySignCount = userSignRecordHistoryService.querySignCount(query);
    	
    	System.out.println(JsonHelper.toJson(querySignCount));
    }
}
