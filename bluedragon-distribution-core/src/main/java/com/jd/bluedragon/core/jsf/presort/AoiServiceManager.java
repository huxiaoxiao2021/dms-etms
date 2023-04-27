package com.jd.bluedragon.core.jsf.presort;

import java.util.List;

import com.jd.bluedragon.distribution.command.JdResult;

/**
 * 
 * @ClassName: AoiServiceManager
 * @Description: Aoi服务相关接口定义
 * @author: wuyoude
 * @date: 2023年2月21日 下午2:37:26
 *
 */
public interface AoiServiceManager {
    /**
     * 通过aoi编码获取站点路区绑定关系
     * https://joyspace.jd.com/pages/AgtcWFN2LyDyGldWtZwx
     * @param query
     * @return
     */
	JdResult<List<AoiBindRoadMappingData>> aoiBindRoadMappingExactSearch(AoiBindRoadMappingQuery query);
}
