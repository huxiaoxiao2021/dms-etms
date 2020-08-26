package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;

/**
 * @ClassName DeliveryGoodsNoticeService
 * @Description
 * @Author wyh
 * @Date 2020/8/24 10:43
 **/
public interface DeliveryGoodsNoticeService {

    /**
     *
     * @param relationMQ
     * @return
     */
    JdResult<Boolean> deliverySendGoodsMessage(BoxMaterialRelationMQ relationMQ);
}
