package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.enums.MaterialOperationStatusEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialReceiveTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialScanTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.utils.DateHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MaterialReceiveDaoTest
 * @Description
 * @Author wyh
 * @Date 2020/3/3 16:24
 **/
public class MaterialReceiveDaoTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private MaterialReceiveDao materialReceiveDao;

    private static final String USER_NAME = "邢松";
    private static final String USER_ERP = "bjxings";
    private static final long SITE_CODE = 910L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void batchInsertOnDuplicateTest() {
        List<DmsMaterialReceive> list = new ArrayList<>();
        DmsMaterialReceive receive = new DmsMaterialReceive();
        receive.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        receive.setMaterialCode("MZ111111111111");
        receive.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode());
        receive.setReceiveCode("B11111111111111");
        receive.setReceiveNum(1);
        receive.setCreateSiteCode(SITE_CODE);
        receive.setCreateSiteType(Constants.DMS_SITE_TYPE);
        receive.setUpdateUserName(USER_NAME);
        receive.setUpdateUserErp(USER_ERP);
        receive.setCreateUserName(USER_NAME);
        receive.setCreateUserErp(USER_ERP);

        DmsMaterialReceive receive2 = new DmsMaterialReceive();
        receive2.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        receive2.setMaterialCode("MZ111111111111");
        receive2.setReceiveType(MaterialReceiveTypeEnum.RECEIVE_BY_CONTAINER.getCode());
        receive2.setReceiveCode("B11111111111111");
        receive2.setReceiveNum(1);
        receive2.setCreateSiteCode(SITE_CODE);
        receive2.setCreateSiteType(Constants.DMS_SITE_TYPE);
        receive2.setUpdateUserName(USER_NAME);
        receive2.setUpdateUserErp(USER_ERP);
        receive2.setCreateUserName(USER_NAME);
        receive2.setCreateUserErp(USER_ERP);

        list.add(receive);
        list.add(receive2);
        Assert.assertTrue(materialReceiveDao.batchInsertOnDuplicate(list) > 0);
    }
    @Test
    public void deleteByReceiveCodeTest() {
        materialReceiveDao.deleteByReceiveCode("B11111111111111", 910L);
    }

    @Test
    public void queryByPagerConditionTest() {
        RecycleMaterialScanQuery query = new RecycleMaterialScanQuery();
        query.setStartTime(DateHelper.parseDateTime("2020-3-3 00:00:00"));
        query.setEndTime(DateHelper.parseDateTime("2020-3-3 19:00:00"));
        query.setMaterialStatus(MaterialOperationStatusEnum.INBOUND.getCode());
        query.setScanType(MaterialScanTypeEnum.INBOUND.getCode());
        query.setCreateSiteCode(910L);
        Assert.assertNotNull(materialReceiveDao.queryByPagerCondition(query));
    }
}
