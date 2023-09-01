package com.jd.bluedragon.distribution.external.gateway.service.impl;


import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.request.GetFlowDirectionQuery;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;


import com.jd.bluedragon.external.gateway.service.BaseDataGatewayService;
import com.jd.bluedragon.utils.JsonHelper;

import com.jd.ql.dms.report.domain.StreamlinedBasicSite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @ClassName: AbnormalReportingGatewayServiceImplTestCase
 * @Description: 测试类
 * @author: wuyoude
 * @date: 2021年9月25日 下午1:47:58
 */
//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class BaseDataGatewayServiceImplTestCase {

	@Autowired
	private BaseDataGatewayService baseDataGatewayService;

	@Test
	public void testSelectSiteList() {
//		Pager<StreamlinedBasicSiteQuery> streamlinedBasicSiteQueryPager = new Pager<StreamlinedBasicSiteQuery>();
//		StreamlinedBasicSiteQuery streamlinedBasicSiteQuery = new StreamlinedBasicSiteQuery();
//		streamlinedBasicSiteQuery.setSearchStr("JD0003421508148-1-1");
//		streamlinedBasicSiteQueryPager.setSearchVo(streamlinedBasicSiteQuery);
//		JdCResponse<Pager<StreamlinedBasicSite>> pagerJdCResponse = baseDataGatewayService.selectSiteList(streamlinedBasicSiteQueryPager);
		//System.out.println("111111111111"+ JsonHelper.toJson(pagerJdCResponse));

		Pager<GetFlowDirectionQuery> getFlowDirectionQueryPager = new Pager<>();
		GetFlowDirectionQuery getFlowDirectionQuery = new GetFlowDirectionQuery();
		getFlowDirectionQuery.setSearchStr("JD0003421619772-1-1-");
		CurrentOperate currentOperate = new CurrentOperate();
		currentOperate.setSiteCode(40240);
		getFlowDirectionQuery.setCurrentOperate(currentOperate);
		getFlowDirectionQueryPager.setSearchVo(getFlowDirectionQuery);
		JdCResponse<Pager<StreamlinedBasicSite>> flowDirection = baseDataGatewayService.getFlowDirection(getFlowDirectionQueryPager);
		System.out.println("22222222222"+ JsonHelper.toJson(flowDirection));
	}
}