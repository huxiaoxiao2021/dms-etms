package com.jd.bluedragon.core.jsf.address;

import com.jd.bluedragon.distribution.command.JdResult;

/**
 * 
 * @ClassName: GbDistrictJDDistrictMapServiceManager
 * @link https://lbsapi.jd.com/iframe.html?nav=2&childNav=1-7&childURL=/doc/guide/addressAnalyze/nationalStandardData/
 * @Description: 京标国标映射服务接口定义
 * @author: wuyoude
 * @date: 2022年12月1日 下午2:37:26
 *
 */
public interface GbDistrictJDDistrictMapServiceManager {
    /**
     * 调用eclp三方状态回传
     * @param list
     * @return
     */
	JdResult<DmsGbAddressLevelsResponse> getGBDistrictByJDCode(Integer jdCode);
}
