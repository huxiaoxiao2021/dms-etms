package com.jd.bluedragon.distribution.external.gateway.service.impl;


import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.strand.JyStrandReportScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.strand.JyStrandReportScanResp;
import com.jd.bluedragon.external.gateway.service.JyStrandReportGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
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
public class JyStrandReportGatewayServiceImplTestCase {

	@Autowired
	private JyStrandReportGatewayService jyStrandReportGatewayService;

	@Test
	public void testSelectSiteList() {
		JyStrandReportScanReq jyStrandReportScanReq = new JyStrandReportScanReq();
		CurrentOperate currentOperate = new CurrentOperate();
		currentOperate.setSiteCode(40240);
		jyStrandReportScanReq.setCurrentOperate(currentOperate);
		jyStrandReportScanReq.setScanType(4);
		jyStrandReportScanReq.setSiteCode(40240);
		jyStrandReportScanReq.setBizId("STRAND23090100000002");
		jyStrandReportScanReq.setOperateUserErp("huzhihao3");
		jyStrandReportScanReq.setPositionCode("GW00019001");
//		jyStrandReportScanReq.setScanBarCode("JD0003421619772-1-1-");
		jyStrandReportScanReq.setScanBarCode("BC1001230830280000400203");

		JdCResponse<JyStrandReportScanResp> jyStrandReportScanRespJdCResponse = jyStrandReportGatewayService.strandScan(jyStrandReportScanReq);
		System.out.println("22222222222" + JsonHelper.toJson(jyStrandReportScanRespJdCResponse));
	}
}