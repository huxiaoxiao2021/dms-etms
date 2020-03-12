package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceiveFlow;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSendFlow;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialSendTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName MaterialSendFlowDaoTest
 * @Description
 * @Author wyh
 * @Date 2020/3/3 18:04
 **/
public class MaterialSendFlowDaoTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private MaterialSendFlowDao materialSendFlowDao;

    private static final String USER_NAME = "邢松";
    private static final String USER_ERP = "bjxings";
    private static final long SITE_CODE = 910L;

    @Test
    public void batchInsertTest() {
        DmsMaterialSendFlow flow = new DmsMaterialSendFlow();
        flow.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        flow.setMaterialCode("MZ111111111111");
        flow.setSendType(MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode());
        flow.setSendCode("B11111111111111");
        flow.setSendNum(1);
        flow.setCreateSiteCode(SITE_CODE);
        flow.setCreateSiteType(Constants.DMS_SITE_TYPE);
        flow.setReceiveSiteCode(SITE_CODE);
        flow.setReceiveSiteType(Constants.DMS_SITE_TYPE);
        flow.setUpdateUserName(USER_NAME);
        flow.setUpdateUserErp(USER_ERP);
        flow.setCreateUserName(USER_NAME);
        flow.setCreateUserErp(USER_ERP);
        List<DmsMaterialSendFlow> flows = Collections.singletonList(flow);
        Assert.assertTrue(materialSendFlowDao.batchInsert(flows));
    }
}
