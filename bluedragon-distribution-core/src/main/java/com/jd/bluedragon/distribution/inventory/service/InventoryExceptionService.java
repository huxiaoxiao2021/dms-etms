package com.jd.bluedragon.distribution.inventory.service;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

public interface InventoryExceptionService extends Service<InventoryException> {

    PagerResult<InventoryException> queryByPagerCondition(InventoryExceptionCondition condition);

    List<List<Object>> getExportData(InventoryExceptionCondition condition);

    void generateInventoryException(InventoryBaseRequest inventoryBaseRequest);

    int handleException(List<Long> list, LoginUser loginUser);

    int syncInventoryExceptionWaybillTrace();
}
