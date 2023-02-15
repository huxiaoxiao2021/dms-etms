package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

public class CreateSendTask extends SendDBSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(CreateSendTask.class);
    @Autowired
    protected UccPropertyConfiguration uccConfig;
    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;
    @Autowired
    DeliveryService deliveryService;


    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        log.info("=========CreateSendTask start executeSingleTask======task:{}", JsonHelper.toJson(task));
        //判断任务完成度+任务创建时间
        final SendMWrapper sendMWrapper = JsonHelper.fromJson(task.getBody(), SendMWrapper.class);
        SendM sendM =sendMWrapper.getSendM();
        Date createTime = task.getCreateTime();
        String uiqueId =sendMWrapper.getBatchUniqKey();

        String initialCountKey = String.format(CacheKeyConstants.INITIAL_SEND_COUNT_KEY, uiqueId);
        String compeletedCountKey = String.format(CacheKeyConstants.COMPELETE_SEND_COUNT_KEY, uiqueId);
        int initialCount = 0,compeletedCount=0;

        if (StringUtils.isNotEmpty(redisClientCache.get(initialCountKey))){
            initialCount =Integer.valueOf(redisClientCache.get(initialCountKey));
        }
        if (StringUtils.isNotEmpty(redisClientCache.get(compeletedCountKey))){
            compeletedCount = Integer.valueOf(redisClientCache.get(compeletedCountKey));
        }

        if (initialCount>0 && compeletedCount>0 && compeletedCount >= initialCount) {
            log.info("批次 {} 任务执行完毕，开始调用deliveryService.addTaskSend...",uiqueId);
            deliveryService.addTaskSend(sendM);
            deleteRedisCountKey(initialCountKey,compeletedCountKey);
            return true;
        } else {
            Date now = new Date();
            int passedTime = DateHelper.getMiniDiff(createTime, now);
            if (passedTime > uccConfig.getCreateSendTasktimeOut()) {
                log.error("批次 {} 任务未执行完毕，但已超过时间阈值，调用deliveryService.addTaskSend...",uiqueId);
                deliveryService.addTaskSend(sendM);
                deleteRedisCountKey(initialCountKey,compeletedCountKey);
                return true;
            }
        }
        log.info("批次 {} 任务不满足条件，下次重新执行",uiqueId);
        return false;
    }

    private void deleteRedisCountKey(String initialCountKey, String compeletedCountKey) {
        redisClientCache.del(initialCountKey);
        redisClientCache.del(compeletedCountKey);
        log.info("=============清除批次任务计数器===============");
    }

    @Override
    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }
        List<String> ownSignList = new ArrayList<>();
        if (StringUtils.isNotBlank(ownSigns)) {
            ownSignList = Arrays.asList(ownSigns.split(","));
        }
        List<Task> tasks = new ArrayList<Task>();
        try {

            if (queryCondition.size() != queueNum) {
                fetchNum = fetchNum * queueNum / queryCondition.size();
            }

            List<Task> Tasks = taskService.findTasksUnderOptimizeSendTask(this.type, fetchNum, this.keyType, queryCondition, ownSign, ownSignList, uccConfig.getCreateSendTaskExecuteCount());
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}", e.getMessage(), e);
        }
        return tasks;
    }
}
