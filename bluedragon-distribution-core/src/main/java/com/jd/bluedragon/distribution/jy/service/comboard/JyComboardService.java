package com.jd.bluedragon.distribution.jy.service.comboard;

/**
 * @author liwenji
 * @date 2022-11-23 21:17
 */
public interface JyComboardService {

    /**
     * 获取当前始发地的扫描人数
     * @param startSiteId
     * @return
     */
    int queryUserCountByStartSiteCode(Long startSiteId);
}
