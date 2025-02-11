package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.task.request.TaskPdaRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.external.gateway.service.TaskGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

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
    @BusinessLog(sourceSys = 1,bizType = 2006,operateType = 20061)
    @JProfiler(jKey = "DMSWEB.TaskGatewayServiceImpl.addTasksCommonly", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<String> addTasksCommonly(TaskPdaRequest pdaRequest) {
        JdCResponse<String> jdCResponse = new JdCResponse<>();
        if(pdaRequest == null){
            jdCResponse.toFail(JdResponse.MESSAGE_PARAM_ERROR);
            return jdCResponse;
        }
        if(pdaRequest.getReceiveSiteCode() == null || pdaRequest.getReceiveSiteCode() == 0){
            jdCResponse.toFail(JdResponse.MESSAGE_PARAM_ERROR_2);
            return jdCResponse;
        }
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

        if(Objects.equals(taskResponse.getCode(),TaskResponse.CODE_OK)){
            jdCResponse.toSucceed(taskResponse.getMessage());
        }else{
            jdCResponse.toError(taskResponse.getMessage());
        }
        return jdCResponse;
    }
}
