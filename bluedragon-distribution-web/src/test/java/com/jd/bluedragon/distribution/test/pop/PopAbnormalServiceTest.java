package com.jd.bluedragon.distribution.test.pop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalService;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-23 下午12:47:12
 *
 * 类说明
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/distribution-core-context.xml" })
public class PopAbnormalServiceTest {
	
	@Autowired
	private PopAbnormalService popAbnormalService;
	
	@Test
	public void testUpdatePopPackNum() {
		PopAbnormal popAbnormal = new PopAbnormal();
		popAbnormal.setId(1l);
		popAbnormal.setSerialNumber("170303939");
		popAbnormal.setAbnormalType(1);
		popAbnormal.setWaybillCode("170303939");
		popAbnormal.setOrderCode("170303939");
		popAbnormal.setPopSupNo("1000");
		popAbnormal.setCurrentNum(10);
		popAbnormal.setActualNum(3);
		popAbnormal.setConfirmNum(30);
		popAbnormal.setOperatorCode(110);
		popAbnormal.setOperatorName("赵恒冲");
		popAbnormal.setCreateSiteCode(1000);
		popAbnormal.setCreateSiteName("北京分拣中心");
		popAbnormal.setMemo("备注");
		int resultNum = this.popAbnormalService.updatePopPackNum(popAbnormal);
		System.out.println(resultNum);
	}
	
	@Test
	public void testFindList() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pageSize", 100);
		paramMap.put("createSiteCode", 1000);
//		paramMap.put("popSupNo", "1000");
//		paramMap.put("waybillCode", "123456");
//		paramMap.put("startCreateTime", new Date());
//		paramMap.put("endCreateTime", new Date());
//		paramMap.put("abnormalState", PopAbnormal.POP_UNPASS);
//		paramMap.put("operatorCode", 10000);
//		paramMap.put("orderCode", "123456");
		List<PopAbnormal> popAbnormals = popAbnormalService.findList(paramMap);
		System.out.println(popAbnormals.size());
	}
	
	@Test
	public void testAdd() {
		PopAbnormal popAbnormal = new PopAbnormal();
		popAbnormal.setSerialNumber("12345678");
		popAbnormal.setAbnormalType(1);
		popAbnormal.setWaybillCode("10020");
		popAbnormal.setOrderCode("10020");
		popAbnormal.setPopSupNo("1000");
		popAbnormal.setCurrentNum(10);
		popAbnormal.setActualNum(1);
		popAbnormal.setOperatorCode(110);
		popAbnormal.setOperatorName("赵恒冲");
		popAbnormal.setCreateSiteCode(1000);
		popAbnormal.setCreateSiteName("北京分拣中心");
		popAbnormal.setMemo("备注");
		int addCount = popAbnormalService.add(popAbnormal);
		System.out.println(addCount);
	}
}
