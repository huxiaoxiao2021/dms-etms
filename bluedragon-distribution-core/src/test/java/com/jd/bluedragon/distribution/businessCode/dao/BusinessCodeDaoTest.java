package com.jd.bluedragon.distribution.businessCode.dao;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/2/26
 **/
public class BusinessCodeDaoTest extends AbstractDaoIntegrationH2Test {

    private String sendCode = "";

    private Integer createSiteCOde = 755380;
    private Integer receiveSiteCode = 4055;

    private String user = "bjxings";

    @Autowired
    private BusinessCodeDao businessCodeDao;

    @Before
    public void setUp() throws Exception {
        sendCode = SerialRuleUtil.generateSendCode(createSiteCOde, receiveSiteCode, new Date());
    }

    @Test
    public void insertBusinessCode() {
        BusinessCodePo businessCodePo = new BusinessCodePo();
//        businessCodePo.setId(sequenceGen.newId("BUSINESS_CODE"));
        businessCodePo.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodePo.setCode(sendCode);
        businessCodePo.setNodeType(BusinessCodeNodeTypeEnum.send_code.name());
        businessCodePo.setCreateUser(user);
        businessCodePo.setUpdateUser(user);
        Assert.assertFalse(businessCodeDao.insertBusinessCode(businessCodePo) == 0);
    }

    @Test
    public void findBusinessCodeByCode() {
        BusinessCodePo businessCodePo = new BusinessCodePo();
        businessCodePo.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodePo.setCode(sendCode);
        businessCodePo.setNodeType(BusinessCodeNodeTypeEnum.send_code.name());
        businessCodePo.setCreateUser(user);
        businessCodePo.setUpdateUser(user);
        Assert.assertFalse(businessCodeDao.insertBusinessCode(businessCodePo) == 0);

        Assert.assertNotNull(businessCodeDao.findBusinessCodeByCode(sendCode));
    }

    @Test
    public void batchInsertBusinessCodeAttribute() {
        BusinessCodeAttributePo businessCodeAttributePo1 = new BusinessCodeAttributePo();
        businessCodeAttributePo1.setCode(sendCode);
        businessCodeAttributePo1.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code.name());
        businessCodeAttributePo1.setAttributeValue(String.valueOf(createSiteCOde));
        businessCodeAttributePo1.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodeAttributePo1.setCreateUser(user);
        businessCodeAttributePo1.setUpdateUser(user);
        BusinessCodeAttributePo businessCodeAttributePo2 = new BusinessCodeAttributePo();
        businessCodeAttributePo2.setCode(sendCode);
        businessCodeAttributePo2.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name());
        businessCodeAttributePo2.setAttributeValue(String.valueOf(receiveSiteCode));
        businessCodeAttributePo2.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodeAttributePo2.setCreateUser(user);
        businessCodeAttributePo2.setUpdateUser(user);

        List<BusinessCodeAttributePo> businessCodeAttributePoList = new ArrayList<>();
        businessCodeAttributePoList.add(businessCodeAttributePo1);
        businessCodeAttributePoList.add(businessCodeAttributePo2);

        Assert.assertEquals(2, (int) businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePoList));
    }

    @Test
    public void findAllAttributesByCode() {
        BusinessCodeAttributePo businessCodeAttributePo = new BusinessCodeAttributePo();
        businessCodeAttributePo.setCode(sendCode);
        businessCodeAttributePo.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name());
        businessCodeAttributePo.setAttributeValue(String.valueOf(receiveSiteCode));
        businessCodeAttributePo.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodeAttributePo.setCreateUser(user);
        businessCodeAttributePo.setUpdateUser(user);

        List<BusinessCodeAttributePo> businessCodeAttributePoList = new ArrayList<>();
        businessCodeAttributePoList.add(businessCodeAttributePo);

        businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePoList);

        Assert.assertNotNull(businessCodeDao.findAllAttributesByCode(sendCode));

    }

    @Test
    public void findAttributeByCodeAndKey() {
        BusinessCodeAttributePo businessCodeAttributePo = new BusinessCodeAttributePo();
        businessCodeAttributePo.setCode(sendCode);
        businessCodeAttributePo.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name());
        businessCodeAttributePo.setAttributeValue(String.valueOf(receiveSiteCode));
        businessCodeAttributePo.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodeAttributePo.setCreateUser(user);
        businessCodeAttributePo.setUpdateUser(user);

        List<BusinessCodeAttributePo> businessCodeAttributePoList = new ArrayList<>();
        businessCodeAttributePoList.add(businessCodeAttributePo);

        businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePoList);

        Assert.assertNotNull(businessCodeDao.findAttributeByCodeAndKey(businessCodeAttributePo));
    }


    @Test
    public void updateAttribute() {
        BusinessCodeAttributePo businessCodeAttributePo = new BusinessCodeAttributePo();
        businessCodeAttributePo.setCode(sendCode);
        businessCodeAttributePo.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name());
        businessCodeAttributePo.setAttributeValue(String.valueOf(receiveSiteCode));
        businessCodeAttributePo.setFromSource(BusinessCodeFromSourceEnum.DMS_WEB_SYS.name());
        businessCodeAttributePo.setCreateUser(user);
        businessCodeAttributePo.setUpdateUser(user);

        List<BusinessCodeAttributePo> businessCodeAttributePoList = new ArrayList<>();
        businessCodeAttributePoList.add(businessCodeAttributePo);

        businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePoList);

        Assert.assertFalse(businessCodeDao.updateAttribute(businessCodeAttributePo) == 0);
    }
}