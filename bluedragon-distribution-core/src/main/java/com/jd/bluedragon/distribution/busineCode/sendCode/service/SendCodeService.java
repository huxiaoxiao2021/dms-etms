package com.jd.bluedragon.distribution.busineCode.sendCode.service;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.busineCode.sendCode.domain.SendCodeDto;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 *     批次号业务单号的service服务接口
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public interface SendCodeService {

    /**
     * 创建批次号 （采用实时时间生成）生成不重复的批次号
     * @param fromSource 创建来源
     * @param attributeKeyMap 批次属性 必须包含始发地和目的地
     * @param createUser 创建人
     * @see BusinessCodeAttributeKey.SendCodeAttributeKeyEnum 批次号属性
     * @return 批次号（生成的批次号最多可以看到小时纬度）
     */
    String createSendCode(Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyMap, BusinessCodeFromSourceEnum fromSource, String createUser);

    /**
     * 查询批次号的对象
     * @param code 批次号
     * @return
     */
    SendCodeDto queryByCode(String code);

    /**
     * 校验批次号是否具有生鲜属性
     * @param sendCode 批次号
     * @return
     */
    Boolean isFreshSendCode(String sendCode);
}
