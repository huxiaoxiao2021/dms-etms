package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GoodsLoadingScanningServiceImplTest {

    @Resource
    private GoodsLoadScanGatewayService goodsLoadingScanningService;

    @Test
    public void testFindExceptionGoodsLoading() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();
        param.setTaskId(1021005L);

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(1241136);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        JdCResponse<List<GoodsExceptionScanningDto>> res =  goodsLoadingScanningService.findExceptionGoodsLoading(param);
        System.out.println(res.getCode() + "----" + res.getMessage());
        for(GoodsExceptionScanningDto r : res.getData()) {
            System.out.println(r.getTaskId());
            System.out.println("运单：" + r.getWaybillCode());
            System.out.println("已装：" + r.getLoadAmount());
            System.out.println("未装：" + r.getUnloadAmount());
        }

        System.out.println("------------------");
    }

    @Test
    public void goodsCompulsoryDeliver() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();

//        param.setTaskId();
        goodsLoadingScanningService.goodsCompulsoryDeliver(param);
    }


}
