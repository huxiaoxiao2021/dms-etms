package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.inspection.domain.InspectionPackProgress;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName InspectionServiceTest
 * @Description
 * @Author wangyinhui3
 * @Date 2019/12/12
 **/
public class InspectionServiceTest extends AbstractDaoIntegrationTest {

    @Autowired
    private InspectionService inspectionService;

    @Test
    public void getWaybillCheckProgressTest() {
        String waybillCode = "JDVA00049175690";
        InspectionPackProgress result = inspectionService.getWaybillCheckProgress(waybillCode, 910);
        System.out.println("check result:" + JsonHelper.toJson(result));
    }
}
