package com.jd.bluedragon.distribution.sendCode.service;

import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.sendCode.domain.SendCodeDto;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.intelligent.common.model.enums.BusinessAttrEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * <p>
 *     批次号业务单号相关服务，
 *     包含批次号本身和单号属性等的服务，不包含批次号的发货明细等服务接口
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public class SendCodeServiceImpl implements SendCodeService {

    @Autowired
    private BusinessCodeDao businessCodeDao;

    @Override
    public Boolean createSendCode(Integer createSiteCode, Integer receiveSiteCode, String createUser, Boolean isFresh, String fromSource) {

        String sendCode = SerialRuleUtil.generateSendCode(createSiteCode, receiveSiteCode, new Date());

        /* 1. 创建业务单号的副表 */
        BusinessCodeAttributePo businessCodeAttributePo1 = new BusinessCodeAttributePo();
        businessCodeAttributePo1.setCode(sendCode);
        businessCodeAttributePo1.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code.name());
        businessCodeAttributePo1.setAttributeValue(String.valueOf(createSiteCode));
        businessCodeAttributePo1.setCreateUser(createUser);
        businessCodeAttributePo1.setUpdateUser(createUser);
        businessCodeAttributePo1.setFromSource(fromSource);

        /* 2. 创建业务单号的主表 */

        return null;
    }

    @Override
    public SendCodeDto queryByCode(String code) {
        return null;
    }

    @Override
    public Boolean isFreshSendCode(String sendCode) {
        return null;
    }
}
