package com.jd.bluedragon.distribution.inventory.service;

import com.jd.bluedragon.distribution.api.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTaskCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

public interface InventoryTaskService extends Service<InventoryTask> {

    PagerResult<InventoryTask> queryByPagerCondition(InventoryTaskCondition condition);

    List<List<Object>> getExportData(InventoryTaskCondition condition);

    List<SiteEntity> getInventoryDirectionList(Integer createSiteCode);

    /**
     * 获取当前操作人正在进行的盘点任务
     * @param request
     * @return
     */
    JdResult<InventoryTaskResponse> getUserDoingInventoryTask(InventoryTaskRequest request);

    /**
     * 判断该流向/盘点范围是否有正在进行的任务
     * @param request
     * @return
     */
    JdResult<InventoryTaskResponse> directionVerify(InventoryTaskRequest request);

    /**
     * 生成盘点任务
     * @param request
     * @return
     */
    JdResult<InventoryTaskResponse> addInventoryTask(InventoryTaskRequest request);


}
