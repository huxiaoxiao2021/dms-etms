package com.jd.bluedragon.distribution.jy.manager;

import com.jd.wl.cs.abnormal.portal.api.dto.reason.AbnormalReasonDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/20 15:45
 * @Description:
 */
public interface AbnormalReasonOfZKManager {

    /**
     * 根据系统编码获取异常原因
     * @param
     * @return
     */
    List<AbnormalReasonDto> queryAbnormalReasonListBySystemCode();
}
