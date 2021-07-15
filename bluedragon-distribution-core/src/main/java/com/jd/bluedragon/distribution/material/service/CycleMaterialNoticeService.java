package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;

import java.util.List;

/**
 * @ClassName CycleMaterialNoticeService
 * @Description
 * @Author wyh
 * @Date 2020/8/24 10:43
 **/
public interface CycleMaterialNoticeService {

    /**
     * 发送发货物资消息
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

    /**
     * 批量发送发货物资消息
     * @param list
     * @return
     */
    JdResult<Boolean> batchSendDeliveryMessage(List<BoxMaterialRelationMQ> list);

}
