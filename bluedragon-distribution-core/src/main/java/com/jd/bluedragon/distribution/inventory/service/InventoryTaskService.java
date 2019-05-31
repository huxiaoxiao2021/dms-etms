package com.jd.bluedragon.distribution.inventory.service;

import com.jd.bluedragon.distribution.api.request.inventory.InventoryTaskRequest;
import com.jd.bluedragon.distribution.api.response.inventory.InventoryTaskResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.ql.dms.common.web.mvc.api.Service;

public interface InventoryTaskService extends Service<InventoryTask> {
    /**
     * 生成盘点任务
     * @param request
     * @return
     */
    JdResult<InventoryTaskResponse> addInventoryTask(InventoryTaskRequest request);
}
