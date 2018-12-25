package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.external.service.DmsTaskService;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsTaskService")
public class DmsTaskServiceImpl implements DmsTaskService {

    @Autowired
    @Qualifier("taskResource")
    private TaskResource taskResource;

    @Override
    public TaskResponse add(TaskRequest request) {
        return taskResource.add(request);
    }
}
