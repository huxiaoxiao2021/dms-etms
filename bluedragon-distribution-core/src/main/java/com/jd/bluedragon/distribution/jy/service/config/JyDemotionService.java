package com.jd.bluedragon.distribution.jy.service.config;

import com.jd.bluedragon.sdk.modules.client.dto.JyDemotionConfigInfo;

import java.util.List;

/**
 * 拣运降级接口
 *
 * @author hujiping
 * @date 2022/10/10 4:16 PM
 */
public interface JyDemotionService {

    /**
     * 校验某功能是否降级
     *
     * @param key
     * @return
     */
    boolean checkIsDemotion(String key);

    /**
     * 获取拣运降级配置
     *
     * @return
     */
    List<JyDemotionConfigInfo> obtainJyDemotionConfig();
}
