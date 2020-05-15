package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.sysConfig.response.SysConfigDto;

import java.util.List;

public interface SysConfigManageGateWayService {
    /**
     * 根据关键字获取配置信息
     * @param key
     * @return
     */
    JdCResponse<List<SysConfigDto>> getConfigByKey(String key);
}
