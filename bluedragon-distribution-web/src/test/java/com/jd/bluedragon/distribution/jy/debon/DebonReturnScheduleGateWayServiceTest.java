package com.jd.bluedragon.distribution.jy.debon;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackageDetailResponse;
import com.jd.bluedragon.common.dto.predict.request.SendPredictAggsQuery;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleResult;
import com.jd.bluedragon.distribution.jsf.service.WaybillReturnScheduleGateWayService;
import com.jd.bluedragon.distribution.open.entity.OperatorInfo;
import com.jd.bluedragon.distribution.open.entity.RequestProfile;
import com.jd.bluedragon.external.gateway.service.JySendVehicleGatewayService;
import com.jd.bluedragon.external.gateway.service.PkgPredictGateWayService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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

    @Autowired
    private PkgPredictGateWayService pkgPredictGateWayService;

    @Test
    public void getSendPredictToScanPackageListTest(){

        SendPredictAggsQuery query = new SendPredictAggsQuery();
        query.setFlag(2);
        query.setPageSize(10);
        query.setPageNumber(1);
        query.setSiteCode(2005);
        query.setProductType("NONE");
        JdCResponse<SendVehicleToScanPackageDetailResponse> response = pkgPredictGateWayService.getSendPredictToScanPackageList(query);
        System.out.println(JSON.toJSONString(response));

    }

    @Autowired
    private JySendVehicleGatewayService jySendVehicleGatewayService;

    @Test
    public void sendVehicleToScanAggByProductTest(){

        SendVehicleCommonRequest request = new SendVehicleCommonRequest();
        request.setSendVehicleBizId("SST22071400000047");

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        request.setCurrentOperate(currentOperate);
        request.setFlag(1);
        JdCResponse<List<SendVehicleProductTypeAgg>> response = jySendVehicleGatewayService.getSendVehicleToScanAggByProduct(request);
        System.out.println(JSON.toJSONString(response));
    }
}
