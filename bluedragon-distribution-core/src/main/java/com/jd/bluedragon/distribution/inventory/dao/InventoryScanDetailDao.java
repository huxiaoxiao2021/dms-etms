package com.jd.bluedragon.distribution.inventory.dao;

import com.jd.bluedragon.distribution.inventory.domain.InventoryScanDetail;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

public interface InventoryScanDetailDao extends Dao<InventoryScanDetail> {

    List<InventoryScanDetail> getScanDetailByParam(String inventoryTaskId);
    List<InventoryScanDetail> getScanDetailByParam(String inventoryTaskId, String waybillCode);
    List<String> getScanPackageCodeByParam(String inventoryTaskId);
    List<String> getScanPackageCodeByParam(String inventoryTaskId, String waybillCode);
    int getScanCountByParam(String inventoryTaskId, String packageCode);
    boolean saveOrUpdate(InventoryScanDetail inventoryScanDetail);
}
