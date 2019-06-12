package com.jd.bluedragon.distribution.inventory.service;

import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.ql.dms.common.web.mvc.api.Service;

public interface InventoryExceptionService extends Service<InventoryException> {

    void generateInventoryException(InventoryBaseRequest inventoryBaseRequest);
}
