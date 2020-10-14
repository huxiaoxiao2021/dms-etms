package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;

/**
 * @ClassName CycleMaterialNoticeService
 * @Description
 * @Author wyh
 * @Date 2020/8/24 10:43
 **/
public interface CycleMaterialNoticeService {

    /**
     *
     * @param relationMQ
     * @return
     */
    JdResult<Boolean> deliverySendGoodsMessage(BoxMaterialRelationMQ relationMQ);

    /**
     * 分拣环节发送集包袋消息
     * @param relationMQ
     * @return
     */
    JdResult<Boolean> sendSortingMaterialMessage(BoxMaterialRelationMQ relationMQ);

}
