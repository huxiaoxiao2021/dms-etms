package com.jd.bluedragon.distribution.sendCode;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendCode.domain.HugeSendCodeEntity;

import java.util.Map;

/**
 * <p>
 *     分拣批次号得相关得服务
 *
 * @author zoothon
 * @since 2020/2/18
 **/
public interface DMSSendCodeJSFService {

    /**
     * 查询批次号得相关信息，非明细信息
     * @param sendCode 批次号
     * @return
     */
    InvokeResult<HugeSendCodeEntity> queryBigInfoBySendCode(String sendCode);

    /**
     * 创建批次号
     * @param attributeKeyEnumObjectMap 批次号属性
     * @param fromSourceEnum 创建来源
     * @param createUser 创建人
     * @return 返回创建出来的批次
     */
    InvokeResult<String> createSendCode(Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum,Object> attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum fromSourceEnum, String createUser);

}
