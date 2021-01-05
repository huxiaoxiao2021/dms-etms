package com.jd.bluedragon.distribution.busineCode.sendCode.jsf;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.sdk.api.batch.IDMSSendCodeApi;
import com.jd.bluedragon.distribution.external.sdk.base.ServiceResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.external.sdk.constants.ServiceMessageEnum;
import com.jd.bluedragon.distribution.external.sdk.dto.batch.SendCodeReq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SendCodeExternalServiceImpl
 * @Description
 * @Author wyh
 * @Date 2021/1/4 15:57
 **/
@Service("sendCodeExternalService")
public class SendCodeExternalServiceImpl implements IDMSSendCodeApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendCodeExternalServiceImpl.class);

    @Autowired
    private SendCodeService sendCodeService;

    @Override
    @JProfiler(jKey = Constants.UMP_APP_NAME_DMSWEB + ".SendCodeExternalServiceImpl.genSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = { JProEnum.TP, JProEnum.FunctionError })
    public ServiceResult<String> genSendCode(SendCodeReq request) {
        ServiceResult<String> result = new ServiceResult<>();
        if (null == request
                || request.getBeginningSiteCode() == null
                || request.getDestSiteCode() == null
                || StringUtils.isBlank(request.getCreateUserErp())) {
            result.toParamError();
            return result;
        }

        try {
            Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyMap = new HashMap<>();
            attributeKeyMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(request.getBeginningSiteCode()));
            attributeKeyMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(request.getDestSiteCode()));
            BusinessCodeFromSourceEnum sourceType = BusinessCodeFromSourceEnum.getFromName(request.getSysSource() + "");

            String sendCode = sendCodeService.createSendCode(attributeKeyMap, sourceType, request.getCreateUserErp());
            if (StringUtils.isBlank(sendCode)) {
                result.customFail(ServiceMessageEnum.CODE_SEND_CODE_WARN.getCode(), ServiceMessageEnum.CODE_SEND_CODE_WARN.getMessage());
                return result;
            }

            result.setData(sendCode);
        }
        catch (Exception ex) {
            LOGGER.error("生成批次号失败. req:{}, source:{}", JsonHelper.toJson(request), request.getSysSource(), ex);
            result.toSystemError();
        }

        return result;
    }
}
