package com.jd.bluedragon.distribution.inventory.dao;

import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import java.util.List;
import java.util.Map;

public interface InventoryExceptionDao extends Dao<InventoryException> {

    int updateExpStatus(Map<String, Object> params);

    /**
     * 查询导出结果
     */
    List<InventoryException> getExportResultByCondition(InventoryExceptionCondition condition);

    List<InventoryException> getInventoryLossException();
}
