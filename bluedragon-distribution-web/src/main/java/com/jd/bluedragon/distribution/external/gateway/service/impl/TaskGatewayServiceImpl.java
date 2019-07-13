package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.task.request.TaskPdaRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.external.gateway.service.TaskGatewayService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/7/6
 */
public class TaskGatewayServiceImpl implements TaskGatewayService {

    @Resource
    private TaskResource taskResource;

    @Override
    public JdCResponse<String> addTasksCommonly(TaskPdaRequest pdaRequest) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBoxCode(pdaRequest.getBoxCode());
        taskRequest.setReceiveSiteCode(pdaRequest.getReceiveSiteCode());
//        下面这些C# pda端没赋值
//        taskRequest.setTaskId(0L);
//        taskRequest.setKey("");
//        taskRequest.setUserCode(0);
//        taskRequest.setUserName("");
//        taskRequest.setSiteName("");
//        taskRequest.setBusinessType(0);
//        taskRequest.setId(0);
//        taskRequest.setOperateTime("");
        taskRequest.setType(pdaRequest.getType());
        taskRequest.setKeyword1(pdaRequest.getKeyword1());
        taskRequest.setKeyword2(pdaRequest.getKeyword2());
        taskRequest.setBody(pdaRequest.getBody());
        taskRequest.setSiteCode(pdaRequest.getSiteCode());

        TaskResponse taskResponse = taskResource.add(taskRequest);
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        if(Objects.equals(taskResponse.getCode(),TaskResponse.CODE_OK)){
            jdCResponse.toSucceed(taskResponse.getMessage());
        }else{
            jdCResponse.toError(taskResponse.getMessage());
        }
        return jdCResponse;
    }
}
