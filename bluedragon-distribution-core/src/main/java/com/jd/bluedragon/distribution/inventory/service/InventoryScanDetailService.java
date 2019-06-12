package com.jd.bluedragon.distribution.inventory.service;

import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.domain.InventoryCustomException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.ql.dms.common.web.mvc.api.Service;

public interface InventoryScanDetailService extends Service<InventoryScanDetail> {

    int insert(InventoryScanDetail inventoryScanDetail) throws InventoryCustomException;

    InventoryScanDetail convert2InventoryBaseRequest(InventoryBaseRequest inventoryBaseRequest);
}
