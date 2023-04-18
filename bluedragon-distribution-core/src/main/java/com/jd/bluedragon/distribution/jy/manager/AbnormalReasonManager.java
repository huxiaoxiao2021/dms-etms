package com.jd.bluedragon.distribution.jy.manager;

import com.jd.wl.cs.abnormal.portal.api.dto.reason.AbnormalReasonDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/18 14:18
 * @Description: 质控获取异常原因Manager
 */
public interface AbnormalReasonManager {

    /**
     * 根据系统编码获取异常原因
     * @param systemCode
     * @return
     */
    List<AbnormalReasonDto> queryAbnormalReasonListBySystemCode();
}
