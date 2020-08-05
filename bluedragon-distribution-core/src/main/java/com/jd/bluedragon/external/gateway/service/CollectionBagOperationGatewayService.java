package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.material.collectionbag.CollectionBagOperationReq;

/**
 * @ClassName CollectionBagOperationGatewayService
 * @Description
 * @Author wyh
 * @Date 2020/7/8 18:34
 **/
public interface CollectionBagOperationGatewayService {

    JdCResponse<Void> receiveCollectionBag(CollectionBagOperationReq request);

    JdCResponse<Void> sendCollectionBag(CollectionBagOperationReq request);
}
