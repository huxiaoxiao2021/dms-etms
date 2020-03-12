package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialRelation;
import com.jd.bluedragon.distribution.material.enums.MaterialOperationStatusEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialScanTypeEnum;
import com.jd.bluedragon.distribution.material.enums.MaterialTypeEnum;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.utils.DateHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MaterialRelationDaoTest
 * @Description
 * @Author wyh
 * @Date 2020/3/3 17:47
 **/
public class MaterialRelationDaoTest extends AbstractDaoIntegrationH2Test {

    private static final String USER_NAME = "邢松";
    private static final String USER_ERP = "bjxings";
    private static final long SITE_CODE = 910L;

    @Autowired
    private MaterialRelationDao materialRelationDao;

    @Test
    public void batchInsertOnDuplicateTest() {
        List<DmsMaterialRelation> list = new ArrayList<>();
        DmsMaterialRelation relation = new DmsMaterialRelation();
        relation.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        relation.setMaterialCode("MZ111111111111");
        relation.setReceiveCode("B11111111111111");
        relation.setReceiveNum(1);
        relation.setCreateSiteCode(SITE_CODE);
        relation.setCreateSiteType(Constants.DMS_SITE_TYPE);
        relation.setUpdateSiteCode(SITE_CODE);
        relation.setUpdateSiteType(Constants.DMS_SITE_TYPE);
        relation.setUpdateUserName(USER_NAME);
        relation.setUpdateUserErp(USER_ERP);
        relation.setCreateUserName(USER_NAME);
        relation.setCreateUserErp(USER_ERP);

        DmsMaterialRelation relation2 = new DmsMaterialRelation();
        relation2.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        relation2.setMaterialCode("MZ111111111122");
        relation2.setReceiveCode("B11111111111122");
        relation2.setReceiveNum(1);
        relation2.setCreateSiteCode(SITE_CODE);
        relation2.setCreateSiteType(Constants.DMS_SITE_TYPE);
        relation2.setUpdateSiteCode(SITE_CODE);
        relation2.setUpdateSiteType(Constants.DMS_SITE_TYPE);
        relation2.setUpdateUserName(USER_NAME);
        relation2.setUpdateUserErp(USER_ERP);
        relation2.setCreateUserName(USER_NAME);
        relation2.setCreateUserErp(USER_ERP);

        list.add(relation);
        list.add(relation2);
        Assert.assertTrue(materialRelationDao.batchInsertOnDuplicate(list) > 0);
    }

    @Test
    public void listRelationsByReceiveCodeTest() {
        String receiveCode = "B11111111111111";
        Assert.assertNotNull(materialRelationDao.listRelationsByReceiveCode(receiveCode));
    }

    @Test
    public void deleteByReceiveCode() {
        String receiveCode = "B11111111111111";
        materialRelationDao.deleteByReceiveCode(receiveCode);
    }

    @Test
    public void queryReceiveAndSendTest() {
        RecycleMaterialScanQuery query = new RecycleMaterialScanQuery();
        query.setStartTime(DateHelper.parseDateTime("2020-3-3 00:00:00"));
        query.setEndTime(DateHelper.parseDateTime("2020-3-3 19:00:00"));
        query.setMaterialStatus(MaterialOperationStatusEnum.INBOUND.getCode());
        query.setScanType(MaterialScanTypeEnum.INBOUND.getCode());
        query.setCreateSiteCode(910L);
        Assert.assertNotNull(materialRelationDao.queryReceiveAndSend(query));
    }
}
