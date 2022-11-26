package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;

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

    /**
     * 根据板号获取运单号
     * @param boardCode
     * @return
     */
    String queryWayBillCodeByBoardCode(String boardCode);
    int save(JyComboardEntity entity);

    JyComboardEntity queryIfScaned(JyComboardEntity condition);
}
