package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.bluedragon.distribution.material.enums.*;
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
 * @ClassName MaterialSendDaoTest
 * @Description
 * @Author wyh
 * @Date 2020/3/3 17:23
 **/
public class MaterialSendDaoTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private MaterialSendDao materialSendDao;

    private static final String USER_NAME = "邢松";
    private static final String USER_ERP = "bjxings";
    private static final long SITE_CODE = 910L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void batchInsertOnDuplicateTest() {
        List<DmsMaterialSend> list = new ArrayList<>();
        DmsMaterialSend send = new DmsMaterialSend();
        send.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        send.setMaterialCode("MZ111111111111");
        send.setSendType(MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode());
        send.setSendCode("B11111111111111");
        send.setSendNum(1);
        send.setCreateSiteCode(SITE_CODE);
        send.setCreateSiteType(Constants.DMS_SITE_TYPE);
        send.setReceiveSiteCode(SITE_CODE);
        send.setReceiveSiteType(Constants.DMS_SITE_TYPE);
        send.setUpdateUserName(USER_NAME);
        send.setUpdateUserErp(USER_ERP);
        send.setCreateUserName(USER_NAME);
        send.setCreateUserErp(USER_ERP);

        DmsMaterialSend send2 = new DmsMaterialSend();
        send2.setMaterialType(MaterialTypeEnum.WARM_BOX.getCode());
        send2.setMaterialCode("MZ111111111122");
        send2.setSendType(MaterialSendTypeEnum.SEND_BY_CONTAINER.getCode());
        send2.setSendCode("B11111111111122");
        send2.setSendNum(1);
        send2.setCreateSiteCode(SITE_CODE);
        send2.setCreateSiteType(Constants.DMS_SITE_TYPE);
        send2.setReceiveSiteCode(SITE_CODE);
        send2.setReceiveSiteType(Constants.DMS_SITE_TYPE);
        send2.setUpdateUserName(USER_NAME);
        send2.setUpdateUserErp(USER_ERP);
        send2.setCreateUserName(USER_NAME);
        send2.setCreateUserErp(USER_ERP);

        list.add(send);
        list.add(send2);
        Assert.assertTrue(materialSendDao.batchInsertOnDuplicate(list) > 0);
    }

    @Test
    public void deleteByReceiveCodeTest() {
        materialSendDao.deleteBySendCode("B11111111111111", 910L);
    }

    @Test
    public void queryByPagerConditionTest() {
        RecycleMaterialScanQuery query = new RecycleMaterialScanQuery();
        query.setStartTime(DateHelper.parseDateTime("2020-3-3 00:00:00"));
        query.setEndTime(DateHelper.parseDateTime("2020-3-3 19:00:00"));
        query.setMaterialStatus(MaterialOperationStatusEnum.INBOUND.getCode());
        query.setScanType(MaterialScanTypeEnum.INBOUND.getCode());
        query.setCreateSiteCode(910L);
        Assert.assertNotNull(materialSendDao.queryByPagerCondition(query));
    }
}
