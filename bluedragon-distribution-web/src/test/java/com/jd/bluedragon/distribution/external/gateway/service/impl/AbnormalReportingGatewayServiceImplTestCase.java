package com.jd.bluedragon.distribution.external.gateway.service.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.common.dto.abnormal.Dept;
import com.jd.bluedragon.common.dto.abnormal.DeptType;
import com.jd.bluedragon.common.dto.abnormal.request.DeptQueryRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.DeptServiceQcManager;
import com.jd.bluedragon.dms.utils.AreaData;

import junit.framework.Assert;

/**
 * 
 * @ClassName: AbnormalReportingGatewayServiceImplTestCase
 * @Description: 测试类
 * @author: wuyoude
 * @date: 2021年9月25日 下午1:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbnormalReportingGatewayServiceImplTestCase {
	@InjectMocks
    private AbnormalReportingGatewayServiceImpl abnormalReportingGatewayServiceImpl;
	@Mock
    private DeptServiceQcManager deptServiceQcManager;
	
    @Test
    public void testGetAreaDataList() throws Exception{
    	JdCResponse<List<AreaData>> getAreaDataList = abnormalReportingGatewayServiceImpl.getAreaDataList();
    	Assert.assertEquals(getAreaDataList.isSucceed()&&getAreaDataList.getData().size()>0,true);
	}
    @Test
    public void testGetDeptTypes() throws Exception{
    	JdCResponse<List<DeptType>> mockResult = new JdCResponse<List<DeptType>>();
    	mockResult.toSucceed();
    	mockResult.setData(new ArrayList<DeptType>());
    	mockResult.getData().add(new DeptType("test1","测试1"));
    	mockResult.getData().add(new DeptType("test2","测试2"));
    	when(deptServiceQcManager.getDeptTypes()).thenReturn(mockResult);
    	JdCResponse<List<DeptType>> getDeptTypes = abnormalReportingGatewayServiceImpl.getDeptTypes();
    	Assert.assertEquals(getDeptTypes.isSucceed()&&getDeptTypes.getData().size()==2,true);
	}
    @Test
    public void testGetDept() throws Exception{
    	DeptQueryRequest queryRequest = new DeptQueryRequest();
    	queryRequest.setDeptTypeCode("WW");
    	queryRequest.setRegionId(4);
    	queryRequest.setDeptName("test");
    	JdCResponse<List<Dept>> mockResult = new JdCResponse<List<Dept>>();
    	mockResult.toSucceed();
    	mockResult.setData(new ArrayList<Dept>());
    	mockResult.getData().add(new Dept("test1","测试test1"));
    	mockResult.getData().add(new Dept("test2","测试test2"));
    	mockResult.getData().add(new Dept("test3","测试3"));
    	when(deptServiceQcManager.getDept(4,"WW")).thenReturn(mockResult);
    	JdCResponse<List<Dept>> getDept = abnormalReportingGatewayServiceImpl.getDept(queryRequest);
    	Assert.assertEquals(getDept.isSucceed()&&getDept.getData().size()==2,true);
	}
}
