package com.jd.bluedragon.distribution.external.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.task.MiniStoreSyncProcessDataTask;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.external.service.DmsTaskService;
import com.jd.bluedragon.distribution.ministore.dto.PSReceivingDto;
import com.jd.bluedragon.distribution.ministore.enums.BizDirectionEnum;
import com.jd.bluedragon.distribution.ministore.enums.ProcessTypeEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Autowired
    MiniStoreService miniStoreService;
    @Autowired
    @Qualifier("taskExecutor")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    SortingService sortingService;
    @Autowired
    @Qualifier("miniStoreSortProcessProducer")
    private DefaultJMQProducer miniStoreSortProcessProducer;


    @Override
    @JProfiler(jKey = "DMSWEB.DmsTaskServiceImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public TaskResponse add(TaskRequest request) {
        TaskResponse response = new TaskResponse(JdResponse.CODE_OK,
                JdResponse.MESSAGE_OK);
        if (request == null) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        if (request.getReceiveSiteCode() == null || request.getReceiveSiteCode() == 0) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR_2);
            return response;
        }
        if (request != null && Objects.equals(Task.TASK_TYPE_INSPECTION, request.getType())) {
            log.warn("DmsInternalServiceImpl验货任务keyword2[{}]siteCode[{}]request[{}]", request.getKeyword2(), request.getSiteCode(), JsonHelper.toJson(request));
        }
        response = taskResource.add(request);
        if (response != null && JdResponse.CODE_OK.equals(response.getCode())) {
            log.info("验货add task成功");
            if (Task.TASK_TYPE_RECEIVE.equals(request.getType()) && request.getBody() != null) {
                log.info("验货生成异步任务:MiniStoreSyncProcessDataTask...");
                List<PSReceivingDto> psReceivingDtoList = JSON.parseArray(request.getBody(), PSReceivingDto.class);
                PSReceivingDto psReceivingDto = psReceivingDtoList.get(0);
                ProcessTypeEnum processTypeEnum = BizDirectionEnum.FROWARD.getCode().equals(request.getBusinessType()) ? ProcessTypeEnum.INSPECTION_SORT_CENTER : ProcessTypeEnum.BACK_INSPECTION_SORT_CENTER;
                Runnable task = new MiniStoreSyncProcessDataTask(processTypeEnum, request.getBoxCode(), psReceivingDto.getUserName(), Long.valueOf(psReceivingDto.getUserCode()), miniStoreService, miniStoreSortProcessProducer);
                taskExecutor.execute(task);
            }
        }
        return response;
    }
}
