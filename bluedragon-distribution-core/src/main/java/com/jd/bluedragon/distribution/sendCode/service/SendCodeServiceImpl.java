package com.jd.bluedragon.distribution.sendCode.service;

import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import com.jd.bluedragon.distribution.sendCode.domain.SendCodeDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *     批次号业务单号相关服务，
 *     包含批次号本身和单号属性等的服务，不包含批次号的发货明细等服务接口
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
@Service("sendCodeService")
public class SendCodeServiceImpl implements SendCodeService {

    private static final Logger logger = LoggerFactory.getLogger(SendCodeServiceImpl.class);

    @Autowired
    private BusinessCodeDao businessCodeDao;

    @Override
    public Boolean createSendCode(Integer createSiteCode, Integer receiveSiteCode, String createUser, Boolean isFresh, BusinessCodeFromSourceEnum fromSource) {

        String sendCode = SerialRuleUtil.generateSendCode(createSiteCode, receiveSiteCode, new Date());

        /* 1. 创建业务单号的副表 */
        BusinessCodeAttributePo businessCodeAttributePo1 = new BusinessCodeAttributePo();
        businessCodeAttributePo1.setCode(sendCode);
        businessCodeAttributePo1.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code.name());
        businessCodeAttributePo1.setAttributeValue(String.valueOf(createSiteCode));
        businessCodeAttributePo1.setCreateUser(createUser);
        businessCodeAttributePo1.setUpdateUser(createUser);
        businessCodeAttributePo1.setFromSource(fromSource.name());

        BusinessCodeAttributePo businessCodeAttributePo2 = new BusinessCodeAttributePo();
        businessCodeAttributePo2.setCode(sendCode);
        businessCodeAttributePo2.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name());
        businessCodeAttributePo2.setAttributeValue(String.valueOf(receiveSiteCode));
        businessCodeAttributePo2.setCreateUser(createUser);
        businessCodeAttributePo2.setUpdateUser(createUser);
        businessCodeAttributePo2.setFromSource(fromSource.name());

        BusinessCodeAttributePo businessCodeAttributePo3 = new BusinessCodeAttributePo();
        businessCodeAttributePo3.setCode(sendCode);
        businessCodeAttributePo3.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh.name());
        businessCodeAttributePo3.setAttributeValue(Boolean.TRUE.toString());
        businessCodeAttributePo3.setCreateUser(createUser);
        businessCodeAttributePo3.setUpdateUser(createUser);
        businessCodeAttributePo3.setFromSource(fromSource.name());

        List<BusinessCodeAttributePo> businessCodeAttributePos = new ArrayList<>();
        businessCodeAttributePos.add(businessCodeAttributePo1);
        businessCodeAttributePos.add(businessCodeAttributePo2);
        businessCodeAttributePos.add(businessCodeAttributePo3);

        Integer insertNum = businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePos);
        if (insertNum <= 0) {
            logger.warn("插入业务单号的属性值副表失败，创建批次号失败，始发：{}，目的：{}，生鲜：{}", createSiteCode, receiveSiteCode, isFresh);
            return Boolean.FALSE;
        }

        /* 2. 创建业务单号的主表 */
        BusinessCodePo businessCodePo = new BusinessCodePo();
        businessCodePo.setCode(sendCode);
        businessCodePo.setNodeType(BusinessCodeNodeTypeEnum.send_code.name());
        businessCodePo.setCreateUser(createUser);
        businessCodePo.setUpdateUser(createUser);
        businessCodePo.setFromSource(fromSource.name());
        Integer businessCodeInsertNum = businessCodeDao.insertBusinessCode(businessCodePo);
        if (businessCodeInsertNum <= 0) {
            logger.warn("插入业务单号的主表失败，创建批次号失败，始发：{}，目的：{}，生鲜：{}", createSiteCode, receiveSiteCode, isFresh);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public SendCodeDto queryByCode(String code) {
        if (!BusinessUtil.isSendCode(code)) {
            return null;
        }

        BusinessCodePo businessCodePo = businessCodeDao.findBusinessCodeByCode(code);
        if (null == businessCodePo || !BusinessCodeNodeTypeEnum.send_code.name().equals(businessCodePo.getNodeType())) {
            /* 如果查询到的批次号属性为空，则用正则进行解析，得到除了始发目的之外的其他属性 */
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(code);
            Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(code);

            SendCodeDto sendCodeDto = new SendCodeDto();
            sendCodeDto.setSendCode(code);
            sendCodeDto.setCreateSiteCode(createSiteCode);
            sendCodeDto.setReceiveSiteCode(receiveSiteCode);
            sendCodeDto.setFresh(Boolean.FALSE);//无属性

            return sendCodeDto;
        }

        /* 如果有业务单号的主表的话，则查询业务单号副表，进行批次号的属性查询 */
        List<BusinessCodeAttributePo> businessCodeAttributePos = businessCodeDao.findAllAttributesByCode(code);
        if (businessCodeAttributePos == null || businessCodeAttributePos.size() == 0) {
            logger.error("该批次号：{}存在业务单号的主表记录，但不存在业务单号的副表记录，查询失败", code);
            return null;
        }

        SendCodeDto sendCodeDto = new SendCodeDto();
        sendCodeDto.setSendCode(code);
        for (BusinessCodeAttributePo item : businessCodeAttributePos) {
            if (BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code.name().equals(item.getAttributeKey())) {
                sendCodeDto.setCreateSiteCode(Integer.valueOf(item.getAttributeValue()));
            } else if (BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name().equals(item.getAttributeKey())) {
                sendCodeDto.setReceiveSiteCode(Integer.valueOf(item.getAttributeValue()));
            } else if (BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh.name().equals(item.getAttributeKey())) {
                sendCodeDto.setFresh(Boolean.parseBoolean(item.getAttributeValue()));
            } else {
                logger.error("解析批次号属性失败，未知的属性值与解析关系,批次号:{},属性key:{},属性value:{}",code, item.getAttributeKey(), item.getAttributeValue());
            }
        }
        return sendCodeDto;
    }

    @Override
    public Boolean isFreshSendCode(String sendCode) {
        if (!BusinessUtil.isSendCode(sendCode)) {
            return Boolean.FALSE;
        }

        /*
            理论上是要先从主表中的nodeType字段判断是不是批次号，然后在去属性值表中判断是否具有该属性
            但是上面的工具类已经初步判断了批次号，故省略从主表中判断批次号的枚举这一步骤。
         */
        BusinessCodeAttributePo condition = new BusinessCodeAttributePo();
        condition.setCode(sendCode);
        condition.setAttributeKey(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh.name());
        BusinessCodeAttributePo businessCodeAttributePo = businessCodeDao.findAttributeByCodeAndKey(condition);
        return businessCodeAttributePo != null && Boolean.parseBoolean(businessCodeAttributePo.getAttributeValue());
    }
}
