package com.jd.bluedragon.distribution.inventory.dao;

import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

public interface InventoryTaskDao extends Dao<InventoryTask> {
    /**
     * 批量写入盘点任务
     * @param inventoryTaskList
     * @return
     */
    int addBatch(List<InventoryTask> inventoryTaskList);

    /**
     * 更新表里的update_time
     * @param inventoryTask
     * @return
     */
    boolean updateTime(InventoryTask inventoryTask);

    /**
     * 更新表里的status
     * @param inventoryTask
     * @return
     */
    boolean updateStatus(InventoryTask inventoryTask);
    /**
     * 根据盘点任务id获取盘点任务
     * @param inventoryTaskId
     * @return
     */
    List<InventoryTask> getInventoryTaskByTaskId(String inventoryTaskId);

    /**
     * 根据流向/盘点范围获取盘点任务
     * @param createSiteCode
     * @param directionCodeList
     * @param scope
     * @return
     */
    List<InventoryTask> getInventoryTaskByDirectionOrScope(Integer createSiteCode, List<Integer> directionCodeList, Integer scope);

    /**
     * 根据任务创建人编码获取盘点任务
     * @param createSiteCode
     * @param createUserCode
     * @return
     */
    List<InventoryTask> getInventoryTaskByCreateUser(Integer createSiteCode,Integer createUserCode);
}
