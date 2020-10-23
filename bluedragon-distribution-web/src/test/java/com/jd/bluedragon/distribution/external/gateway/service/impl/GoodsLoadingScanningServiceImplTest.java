package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 装车发货相关功能测试
 * @Author zhengchengfa
 * @Date 2020年10月22日
 */
@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GoodsLoadingScanningServiceImplTest {

    @Resource
    private GoodsLoadScanGatewayService goodsLoadingScanningService;

    @Test //不齐异常数据查询测试
    public void testFindExceptionGoodsLoading() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();
        param.setTaskId(40L);

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(1241136);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        JdCResponse<List<GoodsExceptionScanningDto>> res = null;
        for(int i=0; i< 3; i++) {
            res = goodsLoadingScanningService.findExceptionGoodsLoading(param);
        }
        System.out.println(res.getCode() + "----" + res.getMessage());
        for(GoodsExceptionScanningDto r : res.getData()) {
            System.out.println(r.getTaskId());
            System.out.println("运单：" + r.getWaybillCode());
            System.out.println("已装：" + r.getLoadAmount());
            System.out.println("未装：" + r.getUnloadAmount());
        }

        System.out.println("------------------");
    }

    @Test //强制下发测试
    public void goodsCompulsoryDeliver() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(1241136);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        param.setTaskId(1L);

        List<String> list = new ArrayList<>();
        list.add("JD0026827277756");
        list.add("JD0026829398865");
        param.setWaybillCode(list);


        JdCResponse res = goodsLoadingScanningService.goodsCompulsoryDeliver(param);
        System.out.println(res.getCode() + "----" + res.getMessage());
    }

    @Test //完成发货测试
    public void testGoodsLoadingDeliver() {
        GoodsLoadingReq param = new GoodsLoadingReq();

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(755380);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        param.setTaskId(1L);
        param.setSendCode("910-364605-20190111122142011");
        param.setReceiveSiteCode(852798);

        JdCResponse res = goodsLoadingScanningService.goodsLoadingDeliver(param);

        System.out.println(res.getCode() + "----" + res.getMessage());
    }

    @Test //取消发货测试
    public void testGoodsRemoveScanning() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(755380);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        param.setTaskId(1L);

        param.setPackageCode("PA002");//多个改数
        JdCResponse res = goodsLoadingScanningService.goodsRemoveScanning(param);
        System.out.println(res.getCode() + "----" + res.getMessage());

        param.setPackageCode("PA001");//多个改数
        res = goodsLoadingScanningService.goodsRemoveScanning(param);
        System.out.println(res.getCode() + "----" + res.getMessage());

        System.out.println("-----------------end-----------------");

    }


}
