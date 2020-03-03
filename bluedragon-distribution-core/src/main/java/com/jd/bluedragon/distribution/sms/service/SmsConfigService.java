package com.jd.bluedragon.distribution.sms.service;

import com.jd.bluedragon.distribution.sms.domain.SMSDto;

/**
 * 短信配置接口
 *
 * @author: hujiping
 * @date: 2020/2/26 16:32
 */
public interface SmsConfigService {


    /**
     * 冷链卡班根据区域获取短信配置对象
     * @param orgId 区域ID
     * @return
     */
    SMSDto getSMSConstantsByOrgId(Integer orgId);
}
