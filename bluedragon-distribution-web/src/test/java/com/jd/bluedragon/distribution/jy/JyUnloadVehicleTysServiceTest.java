package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.QueryGoodsCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/28 15:27
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class JyUnloadVehicleTysServiceTest {

    @Autowired
    private JyUnloadVehicleTysService jyUnloadVehicleTysService;

    @Test
    public void queryGoodsCategoryByDiffDimension(){

        QueryGoodsCategory queryGoodsCategory = new QueryGoodsCategory();
        queryGoodsCategory.setBizId("TEST003");
        queryGoodsCategory.setBoardCode("TEST003");
        InvokeResult<List<GoodsCategoryDto>> listInvokeResult = jyUnloadVehicleTysService.queryGoodsCategoryByDiffDimension(queryGoodsCategory);
        System.out.println(JSON.toJSONString(listInvokeResult));
    }
}
