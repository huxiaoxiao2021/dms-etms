package com.jd.bluedragon.distribution.jy.service.comboard;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.BatchUpdateCancelReq;

import java.util.List;

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
    List<User> queryUserByStartSiteCode(JyComboardEntity startSiteId);

    /**
     * 根据板号获取运单号
     * @param entity
     * @return
     */
    String queryWayBillCodeByBoardCode(JyComboardEntity entity);
    int save(JyComboardEntity entity);

    JyComboardEntity queryIfScaned(JyComboardEntity condition);

    /**
     * 批量更新取消标识
     * @param req
     * @return
     */
    boolean batchUpdateCancelFlag(BatchUpdateCancelReq req);

    /**
     * 批量查询包裹统计信息
     * @param startSiteCode
     * @param boardCodeList
     * @return
     */
    long  countByBoardList(Long startSiteCode, List<String> boardCodeList);

}
