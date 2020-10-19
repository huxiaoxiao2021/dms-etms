package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
import com.jd.bluedragon.external.gateway.service.GoodsLoadingScanningService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GoodsLoadingScanningServiceImplTest {

    @Resource
    private GoodsLoadingScanningService goodsLoadingScanningService;

    @Test
    public void testFindExceptionGoodsLoading() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();
        param.setTaskId(123L);
        JdCResponse<List<GoodsExceptionScanningDto>> res =  goodsLoadingScanningService.findExceptionGoodsLoading(param);

        System.out.println("------------------");
    }


}
