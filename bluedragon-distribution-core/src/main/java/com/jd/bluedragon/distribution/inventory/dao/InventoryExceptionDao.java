package com.jd.bluedragon.distribution.inventory.dao;

import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionDto;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface InventoryExceptionDao extends Dao<InventoryException> {

    int updateExpStatus(Map<String, Object> params);

    /**
     * 查询导出结果
     */
    List<InventoryExceptionDto> getExportResultByCondition(InventoryExceptionCondition condition);
}
