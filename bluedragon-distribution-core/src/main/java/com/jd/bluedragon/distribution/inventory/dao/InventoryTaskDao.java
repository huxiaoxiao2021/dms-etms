package com.jd.bluedragon.distribution.inventory.dao;

import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

public interface InventoryTaskDao extends Dao<InventoryTask> {
    /**
     * 根据站点和操作人获取正在进行的任务列表
     * @param createSiteCode
     * @param createUserCode
     * @return
     */
    List<InventoryTask> getInventoryTaskBySiteAndUser(Integer createSiteCode, Integer createUserCode);

    /**
     * 批量写入盘点任务
     * @param inventoryTaskList
     * @return
     */
    int addBatch(List<InventoryTask> inventoryTaskList);
}
