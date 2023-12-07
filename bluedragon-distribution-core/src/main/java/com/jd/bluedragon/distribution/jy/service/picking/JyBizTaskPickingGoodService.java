package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:13
 * @Description
 */
public interface JyBizTaskPickingGoodService {
    /**
     * 根据bizId待提货任务
     * @param bizId
     * @param ignoreYn  true: 忽略yn   false：只查yn=1
     * @return
     */
    JyBizTaskPickingGoodEntity findByBizIdWithYn(String bizId, boolean ignoreYn);
}
