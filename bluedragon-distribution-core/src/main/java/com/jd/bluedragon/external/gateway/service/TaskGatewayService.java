package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.task.request.TaskPdaRequest;

/**
 * 上传任务接口 发布物流网关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/6
 */
public interface TaskGatewayService {

    /**
     * 通用的上传任务方法
     * @param pdaRequest
     * @return
     */
    JdCResponse<String> addTasksCommonly(TaskPdaRequest pdaRequest);
}
