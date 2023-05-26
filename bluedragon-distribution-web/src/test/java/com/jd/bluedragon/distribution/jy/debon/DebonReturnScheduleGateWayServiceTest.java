package com.jd.bluedragon.distribution.jy.debon;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleResult;
import com.jd.bluedragon.distribution.jsf.service.WaybillReturnScheduleGateWayService;
import com.jd.bluedragon.distribution.open.entity.OperatorInfo;
import com.jd.bluedragon.distribution.open.entity.RequestProfile;
import com.jd.ql.dms.common.domain.JdResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/22 16:41
 * @Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class DebonReturnScheduleGateWayServiceTest {

    @Autowired
    private WaybillReturnScheduleGateWayService debonReturnScheduleGateWayService;



    @Test
    public void returnScheduleSiteInfoByWaybillTest(){

        ReturnScheduleRequest request = new ReturnScheduleRequest();

        RequestProfile requestProfile = new RequestProfile();
        requestProfile.setSysSource("test");
        requestProfile.setTimestamp(System.currentTimeMillis());
        request.setRequestProfile(requestProfile);

        OperatorInfo operatorInfo = new OperatorInfo();
        operatorInfo.setOperateTime(System.currentTimeMillis());
        operatorInfo.setOperateSiteId(910);
        operatorInfo.setOperateSiteCode("010Y100");
        operatorInfo.setOperateSiteName("测试站点");
        operatorInfo.setOperateUserErp("bjxings");
        operatorInfo.setOperateUserId(1001);
        request.setOperatorInfo(operatorInfo);
        //request.setWaybillCode("JDVB22493156674");
        request.setWaybillCode("JD0003420119555");

        JdResponse<ReturnScheduleResult> response = debonReturnScheduleGateWayService.returnScheduleSiteInfoByWaybill(request);
        System.out.println(JSON.toJSONString(response));
    }
}
