package com.jd.bluedragon.distribution.busineCode.sendCode.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.api.response.sendcode.SendCodeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.BusinessCodeManager;
import com.jd.bluedragon.distribution.busineCode.sendCode.domain.SendCodeDto;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.businessCode.domain.BusinessCodePo;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.coo.sa.sn.GenContextItem;
import com.jd.coo.sa.sn.SmartSNGen;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
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
    @Qualifier("smartSendCodeSNGen")
    private SmartSNGen smartSendCodeSNGen;

    @Autowired
    private BusinessCodeManager businessCodeManager;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    @JProfiler(jKey = "DMS.CORE.SendCodeService.createSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public String createSendCode(Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyMap, BusinessCodeFromSourceEnum fromSource, String createUser) {

        if (attributeKeyMap == null || attributeKeyMap.isEmpty()) {
            logger.error("创建批次参数不正确：{}, 数据来源：{}", JsonHelper.toJson(attributeKeyMap), fromSource.name());
            return StringUtils.EMPTY;
        }
        String sendCode = "";
        /* 判断UCC的批次开关是否开启：开启则使用新的生成器生成，未开启则使用原来的工具类生成 */
        CallerInfo callerInfo = Profiler.registerInfo("DMS.CORE.SendCodeService.createSendCode.gen",Constants.UMP_APP_NAME_DMSWEB,false, true);
        if (dmsConfigManager.getPropertyConfig().isSendCodeGenSwitchOn()) {
            GenContextItem[] genContextItems = new GenContextItem[3];
            genContextItems[0] = GenContextItem.create("createSiteCode", attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code));
            genContextItems[1] = GenContextItem.create("receiveSiteCode", attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code));
            genContextItems[2] = GenContextItem.create("currentTimeLong", DateHelper.formatDate(new Date(),"yyyyMMddHH"));
            sendCode = smartSendCodeSNGen.gen(fromSource.name(), genContextItems);
        } else {
          sendCode = SerialRuleUtil.generateSendCode(
                  Long.parseLong(attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code)),
                  Long.parseLong(attributeKeyMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code)),
                  new Date()
                  ) ;
        }
        Profiler.registerInfoEnd(callerInfo);

        /* 持久化业务单号表和业务单号属性表 */
        Map<String,String> attributeParam = new HashMap<>(attributeKeyMap.size());
        for (Map.Entry<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEntity : attributeKeyMap.entrySet()) {
            attributeParam.put(attributeKeyEntity.getKey().name(), attributeKeyEntity.getValue());
        }
        boolean isSuccess = businessCodeManager.saveBusinessCodeAndAttribute(sendCode,BusinessCodeNodeTypeEnum.send_code,attributeParam,createUser, fromSource);
        if (!isSuccess) {
            logger.error("插入业务单号主表副表失败，创建批次号失败，批次号:{},批次属性值:{}", sendCode, JsonHelper.toJson(attributeKeyMap));
            return StringUtils.EMPTY;
        }

        return sendCode;
    }

    @Override
    public SendCodeDto queryByCode(String code) {
        if (!BusinessUtil.isSendCode(code)) {
            return null;
        }

        BusinessCodePo businessCodePo = businessCodeManager.queryBusinessCodeByCode(code,BusinessCodeNodeTypeEnum.send_code);
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
        Map<String,String> attributeMap = businessCodeManager.queryBusinessCodeAttributesByCode(code);
        SendCodeDto sendCodeDto = new SendCodeDto();
        sendCodeDto.setSendCode(code);
        sendCodeDto.setCreateSiteCode(Integer.valueOf(attributeMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code.name())));
        sendCodeDto.setReceiveSiteCode(Integer.valueOf(attributeMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code.name())));
        sendCodeDto.setFresh(Boolean.parseBoolean(attributeMap.get(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh.name())));

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
        String attributeValue = businessCodeManager.queryBusinessCodeAttributeByCodeAndKey(sendCode, BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh.name());
        return Boolean.parseBoolean(attributeValue);
    }

    /**
     * 批次号有效性校验
     * <ul>
         * <li>校验正则</li>
         * <li>校验批次号是否存在</li>
     * </ul>
     *
     * @param sendCode 批次号
     * @return code非200失败
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendCodeServiceImpl.validateSendCodeEffective", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Boolean> validateSendCodeEffective(String sendCode) {
        // 默认校验通过
        InvokeResult<Boolean> result = new InvokeResult<>();
        if (StringUtils.isBlank(sendCode)) {
            return result;
        }
        try {
            // 1. 校验批次号正则
            if (!BusinessHelper.isSendCode(sendCode)) {
                result.customMessage(SendCodeResponse.CODE_PARAMETER_ERROR, MessageFormat.format(SendCodeResponse.MESSAGE_PARAMETER_ERROR, sendCode));
                return result;
            }

            if (!switchIsOpen(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode))) {
                return result;
            }

            if (logger.isInfoEnabled()) {
                logger.info("启用批次有效性校验. site:{}", SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode));
            }

            // 2. 亚一批次号不校验存在性
            if (sendCode.toUpperCase().startsWith(DmsConstants.AO_BATCH_CODE_PREFIX)) {
                return result;
            }

            // 3. 校验批次号是否存在
            BusinessCodePo businessCodePo = businessCodeManager.queryBusinessCodeByCode(sendCode, BusinessCodeNodeTypeEnum.send_code);
            if (null == businessCodePo) {
                result.customMessage(SendCodeResponse.CODE_NOT_EXIST_ERROR, MessageFormat.format(SendCodeResponse.MESSAGE_NOT_EXIST_ERROR, sendCode));
                return result;
            }

        }
        catch (Exception ex) {
            logger.error("校验批次号有效性失败. {}", sendCode, ex);
            result.error("校验批次号服务异常! 请联系分拣小秘[咚咚:xnfjxm]");
        }

        return result;
    }

    /**
     * 批次号校验开关
     * @param createSiteCode 当前分拣中心
     * @return 开关状态 true：开启
     */
    private boolean switchIsOpen(Integer createSiteCode) {
        String configSites = dmsConfigManager.getPropertyConfig().getSendCodeEffectiveValidation();
        if (StringUtils.isBlank(configSites)) {
            return false;
        }
        // 全国开启
        if (Constants.STR_ALL.equalsIgnoreCase(configSites)) {
            return true;
        }
        if (null == createSiteCode) {
            return false;
        }

        List<String> enableSites = Arrays.asList(configSites.split(Constants.SEPARATOR_COMMA));
        return enableSites.contains(createSiteCode.toString());
    }

    /**
     * 批量校验批次号有效性
     * <ul>
     * <li>校验正则</li>
     * <li>校验批次号是否存在</li>
     * </ul>
     *
     * @param sendCodeList
     * @return
     */
    @Override
    public InvokeResult<Boolean> batchValidateSendCodeEffective(List<String> sendCodeList) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        if (CollectionUtils.isEmpty(sendCodeList)) {
            return result;
        }
        StringBuilder warnMsg = new StringBuilder();
        for (String sendCode : sendCodeList) {
            InvokeResult<Boolean> singleChkRet = this.validateSendCodeEffective(sendCode);
            if (!singleChkRet.codeSuccess()) {
                result.setCode(singleChkRet.getCode());
                warnMsg.append(singleChkRet.getMessage()).append("\r\n");
            }
        }
        if (!result.codeSuccess()) {
            result.setMessage(warnMsg.toString());
        }

        return result;
    }
}
