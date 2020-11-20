package com.jd.bluedragon.distribution.sendCode.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodeAttributePo;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import com.jd.bluedragon.distribution.sendCode.domain.SendCodeDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.coo.sa.sn.GenContextItem;
import com.jd.coo.sa.sn.SmartSNGen;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    @Qualifier("smartSendCodeSNGen")
    private SmartSNGen smartSendCodeSNGen;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    @JProfiler(jKey = "DMS.CORE.SendCodeService.createSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public String createSendCode(Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> attributeKeyMap, BusinessCodeFromSourceEnum fromSource, String createUser) {

        if (attributeKeyMap == null || attributeKeyMap.isEmpty()) {
            logger.error("创建批次参数不正确：{}, 数据来源：{}", JsonHelper.toJson(attributeKeyMap), fromSource.name());
            return StringUtils.EMPTY;
        }
        String sendCode = "";
        /* 判断UCC的批次开关是否开启：开启则使用新的生成器生成，未开启则使用原来的工具类生成 */
        if (uccPropertyConfiguration.isSendCodeGenSwitchOn()) {
            GenContextItem[] genContextItems = new GenContextItem[3];
            genContextItems[0] = GenContextItem.create("createSiteCode", attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code));
            genContextItems[1] = GenContextItem.create("receiveSiteCode", attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code));
            genContextItems[2] = GenContextItem.create("currentTimeLong", DateHelper.formatDate(new Date(),"yyyyMMddHH"));
            sendCode = smartSendCodeSNGen.gen(fromSource.name(), genContextItems);
        } else {
          sendCode = SerialRuleUtil.generateSendCode(
                  (Long) attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code),
                  (Long) attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code),
                  new Date()
                  ) ;
        }

        /* 写入business_code表：1. 写入副表；*/
        List<BusinessCodeAttributePo> businessCodeAttributePos = new ArrayList<>(attributeKeyMap.size());
        for (Map.Entry<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> attributeEntry : attributeKeyMap.entrySet()) {
            BusinessCodeAttributePo businessCodeAttributePoItem = new BusinessCodeAttributePo();
            businessCodeAttributePoItem.setCode(sendCode);
            businessCodeAttributePoItem.setAttributeKey(attributeEntry.getKey().name());
            businessCodeAttributePoItem.setAttributeValue(String.valueOf(attributeEntry.getValue()));
            businessCodeAttributePoItem.setCreateUser(createUser);
            businessCodeAttributePoItem.setUpdateUser(createUser);
            businessCodeAttributePoItem.setFromSource(fromSource.name());
            businessCodeAttributePos.add(businessCodeAttributePoItem);
        }
        Integer insertNum = businessCodeDao.batchInsertBusinessCodeAttribute(businessCodeAttributePos);
        if (insertNum <= 0) {
            logger.warn("插入业务单号的属性值副表失败，创建批次号失败，批次属性值：{}", JsonHelper.toJson(attributeKeyMap));
            return StringUtils.EMPTY;
        }
        /* 创建业务单号的主表 */
        BusinessCodePo businessCodePo = new BusinessCodePo();
        businessCodePo.setCode(sendCode);
        businessCodePo.setNodeType(BusinessCodeNodeTypeEnum.send_code.name());
        businessCodePo.setCreateUser(createUser);
        businessCodePo.setUpdateUser(createUser);
        businessCodePo.setFromSource(fromSource.name());
        Integer businessCodeInsertNum = businessCodeDao.insertBusinessCode(businessCodePo);
        if (businessCodeInsertNum <= 0) {
            logger.warn("插入业务单号的主表失败，创建批次号失败，批次属性值：{}", JsonHelper.toJson(attributeKeyMap));
            return StringUtils.EMPTY;
        }

        return sendCode;
    }

    @Override
    public String createSendCode(Integer createSiteCode, Integer receiveSiteCode, String createUser, Boolean isFresh, Date date, BusinessCodeFromSourceEnum fromSource) {

        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, createSiteCode);
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, receiveSiteCode);
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh, isFresh);

        return createSendCode(attributeKeyEnumObjectMap, fromSource, createUser);
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
