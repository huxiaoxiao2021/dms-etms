package com.jd.bluedragon.distribution.external.gateway.service.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.common.dto.abnormal.response.SiteDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.common.dto.abnormal.Dept;
import com.jd.bluedragon.common.dto.abnormal.DeptType;
import com.jd.bluedragon.common.dto.abnormal.TraceDept;
import com.jd.bluedragon.common.dto.abnormal.request.DeptQueryRequest;
import com.jd.bluedragon.common.dto.abnormal.request.TraceDeptQueryRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.DeptServiceQcManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.dms.utils.AreaData;

import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @ClassName: AbnormalReportingGatewayServiceImplTestCase
 * @Description: 测试类
 * @author: wuyoude
 * @date: 2021年9月25日 下午1:47:58
 *
 */
//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class AbnormalReportingGatewayServiceImplTestCase {
//	@InjectMocks
	@Autowired
    private AbnormalReportingGatewayServiceImpl abnormalReportingGatewayServiceImpl;
//	@Mock
	@Autowired
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
    @Test
    public void testGetTraceDept() throws Exception{
    	TraceDeptQueryRequest queryRequest = new TraceDeptQueryRequest();
    	JdCResponse<List<TraceDept>> mockResult = new JdCResponse<List<TraceDept>>();
    	mockResult.toSucceed();
    	mockResult.setData(new ArrayList<TraceDept>());
    	TraceDept d1 = new TraceDept();
    	d1.setCode("code1");
    	mockResult.getData().add(d1);
    	TraceDept d2 = new TraceDept();
    	d2.setCode("code2");
    	mockResult.getData().add(d2);
    	TraceDept d3 = new TraceDept();
    	d3.setCode("code3");
    	d3.setIsDealDept(true);
    	mockResult.getData().add(d3);
    	TraceDept d4 = new TraceDept();
    	d4.setCode("code4");
    	mockResult.getData().add(d4);
    	System.out.println(JsonHelper.toJson(mockResult));
    	//按是否推荐部门排序
    	Collections.sort(mockResult.getData(), new Comparator<TraceDept>() {
            @Override
            public int compare(TraceDept o1, TraceDept o2) {
                if(o1 != null
                		&& Boolean.TRUE.equals(o1.getIsDealDept())) {
                	return -1;
                }
                return 1;
            }
        });
    	System.out.println(JsonHelper.toJson(mockResult));
    	when(deptServiceQcManager.getTraceDept(queryRequest)).thenReturn(mockResult);
    	JdCResponse<List<TraceDept>> getTraceDept = abnormalReportingGatewayServiceImpl.getTraceDept(queryRequest);
    	
    	Assert.assertEquals(getTraceDept.isSucceed()&&getTraceDept.getData().size()==2,true);
	}

	@Test
	public void testQuerySite(){
		QuerySiteRequest case1 = new QuerySiteRequest("", "", "405147");
		JdCResponse<List<SiteDto>> response = abnormalReportingGatewayServiceImpl.querySite(case1.orgId, case1.siteName,case1.siteCode);
		System.out.println(response.getData());

	}

	class QuerySiteRequest{
		//操作机构ID
		String orgId;
		//站点名称
		String siteName;
		//站点码
		String siteCode;
		/**
		 *  @description    构造函数
		 *  @param
		 *  @return
		 *  @author         laoqingchang1
		 *  @date           2022/7/28
		 */
		public QuerySiteRequest(String orgId, String siteName, String siteCode){
			this.orgId = orgId;
			this.siteName = siteName;
			this.siteCode = siteCode;
		}
	}
}
