package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceiveFlow;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MaterialReceiveFlowDaoTest
 * @Description
 * @Author wyh
 * @Date 2020/3/3 18:04
 **/
public class MaterialReceiveFlowDaoTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private MaterialReceiveFlowDao materialReceiveFlowDao;

    private static final String USER_NAME = "邢松";
    private static final String USER_ERP = "bjxings";
    private static final long SITE_CODE = 910L;

    @Test
    public void batchInsertTest() {
        List<DmsMaterialReceiveFlow> flows = new ArrayList<>();
        DmsMaterialReceiveFlow flow = new DmsMaterialReceiveFlow();
        flow.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        flow.setMaterialCode("MZ111111111111");
        flow.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode());
        flow.setReceiveCode("B11111111111111");
        flow.setReceiveNum(1);
        flow.setCreateSiteCode(SITE_CODE);
        flow.setCreateSiteType(Constants.DMS_SITE_TYPE);
        flow.setUpdateUserName(USER_NAME);
        flow.setUpdateUserErp(USER_ERP);
        flow.setCreateUserName(USER_NAME);
        flow.setCreateUserErp(USER_ERP);
        flows.add(flow);
        Assert.assertTrue(materialReceiveFlowDao.batchInsert(flows));
    }
}
