package com.jd.bluedragon.distribution.batchForward.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by hujiping on 2018/8/1.
 */
public class BatchForwardServiceImpl implements BatchForwardService {

    private final Logger logger = Logger.getLogger(BatchForwardServiceImpl.class);

    @Autowired
    private RedisManager redisManager;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SiteService siteService;
    @Autowired

    @Override
    public SendResult batchSend(BatchForwardRequest request) {

        //批次是否封车校验
        if(checkSendCodeIsSealed(request.getNewSendCode())){
            return new SendResult(SendResult.CODE_SENDED, "新批次号已操作封车，请换批次！");
        }
        //插入批次转发的任务
        insertBatchForwardTask(request);

        return null;
    }

    @Override
    public boolean dealBatchForwardTask(Task task) {
        this.logger.info("批次转发开始：" + JsonHelper.toJson(task));
        BatchForwardRequest request = JsonHelper.fromJson(task.getBody(), BatchForwardRequest.class);
        //旧批次号

        return true;
    }


    private void insertBatchForwardTask(BatchForwardRequest request) {

        Task task = new Task();

        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getNewSendCode());
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getNewSendCode());

        task.setCreateSiteCode(createSiteCode);
        task.setReceiveSiteCode(receiveSiteCode);
        task.setBusinessType(10);
        task.setType(Task.TASK_TYPE_SEND_BATCHFORWARD);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_BATCHFORWARD));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(createSiteCode.toString());
        task.setKeyword2(request.getNewSendCode());
        task.setOperateTime(DateHelper.parseDate(request.getOperateTime()));
        taskService.initFingerPrint(task);
        task.setOwnSign(BusinessHelper.getOwnSign());

        task.setBody(JsonHelper.toJson(request));
        taskService.add(task, true);
        logger.info("批次转发插入task_send" + JsonHelper.toJson(task));
    }

    /**
     * 校验批次号是否封车:默认返回false
     * @param sendCode
     * @return
     */
    private boolean checkSendCodeIsSealed(String sendCode) {
        boolean result = false;
        try {
            String isSeal = redisManager.getCache(Constants.CACHE_KEY_PRE_SEAL_SENDCODE+sendCode);
            logger.info("redis取封车批次号"+sendCode+"结果："+isSeal);
            if(StringUtils.isNotBlank(isSeal) && Constants.STRING_FLG_TRUE.equals(isSeal)){
                result = true;
            }
        }catch (Throwable e){
            logger.warn("redis取封车批次号失败："+e.getMessage());
        }
        return result;
    }



}
