package com.jd.bluedragon.core.jsf.boxlimit;

import com.jdl.basic.api.domain.boxFlow.CollectBoxFlowDirectionConf;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/2 14:28
 * @Description: 集箱包裹配置
 */
public interface BoxLimitConfigManager {

    /**
     * 获取集箱配置信息
     * @param createSiteCode
     * @param type
     * @return
     */
    Integer getLimitNums( Integer createSiteCode, String type);

    List<CollectBoxFlowDirectionConf> listCollectBoxFlowDirection(CollectBoxFlowDirectionConf collectBoxFlowDirectionConf);
}
