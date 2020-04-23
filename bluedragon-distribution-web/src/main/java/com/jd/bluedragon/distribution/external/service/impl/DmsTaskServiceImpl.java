package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.external.service.DmsTaskService;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsTaskService")
public class DmsTaskServiceImpl implements DmsTaskService {
    private static final Logger log = LoggerFactory.getLogger(DmsTaskServiceImpl.class);
    @Autowired
    @Qualifier("taskResource")
    private TaskResource taskResource;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsTaskServiceImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public TaskResponse add(TaskRequest request) {
        TaskResponse response = new TaskResponse(JdResponse.CODE_OK,
                JdResponse.MESSAGE_OK);
        if(request == null){
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        if(request.getReceiveSiteCode() == null || request.getReceiveSiteCode() == 0){
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR_2);
            return response;
        }
        if(request != null && Objects.equals(Task.TASK_TYPE_INSPECTION,request.getType())){
            log.warn("DmsInternalServiceImpl验货任务keyword2[{}]siteCode[{}]request[{}]", request.getKeyword2(),request.getSiteCode(), JsonHelper.toJson(request));
        }
        response = taskResource.add(request);
        return response;
    }
}
