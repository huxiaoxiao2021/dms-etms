package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.base.request.User;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:20
 * @Description
 */
public interface JyPickingSendDestinationService {

    /**
     * 获取该流向未完成的批次号,查询不到生成新的批次号
     * @param curSiteId
     * @param nextSiteId
     * @return
     */
    String findOrGenerateBatchCode(Long curSiteId, Long nextSiteId, User user);

    /**
     * 校验发货流向是否存在
     * @param curSiteId
     * @param nextSiteId
     * @return
     */
    boolean existSendNextSite(Long curSiteId, Long nextSiteId);
}
