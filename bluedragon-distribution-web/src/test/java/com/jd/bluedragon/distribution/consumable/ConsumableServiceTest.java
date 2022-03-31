package com.jd.bluedragon.distribution.consumable;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.test.AbstractTestCase;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable
 * @ClassName: ConsumableServiceTest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/16 18:38
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class ConsumableServiceTest extends AbstractTestCase {

    @Autowired
    private WaybillConsumableRelationService waybillConsumableRelationService;


    @Test
    public void testQuery() {
        WaybillConsumableRelationCondition condition = JsonHelper.fromJson("{waybillCode: \"JDX000174089768\", offset: 0, limit: 10}", WaybillConsumableRelationCondition.class);
        System.out.println(waybillConsumableRelationService.queryDetailInfoByPagerCondition(condition));
    }

    @Test
    public void testQuery1() {
        List<String> waybillCodes = new ArrayList<>();
        waybillCodes.add("JDX000174089768");
        System.out.println(waybillConsumableRelationService.queryByWaybillCodes(waybillCodes));
    }
}
