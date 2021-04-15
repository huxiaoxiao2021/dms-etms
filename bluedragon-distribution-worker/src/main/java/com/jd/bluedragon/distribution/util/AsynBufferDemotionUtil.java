package com.jd.bluedragon.distribution.util;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/4/12
 * @Description:
 */
@Service
public class AsynBufferDemotionUtil {

    private Logger log = LoggerFactory.getLogger(AsynBufferDemotionUtil.class);

    private String OFFLINE_TASK_MAX_E_KEY = "dms:offline:%s";

    @Autowired
    @Qualifier("redisClient")
    private Cluster redisClient;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 任务限流 按场地
     * @param task
     * @return
     */
    public boolean isDemotionOfSite(Task task){

        List<Integer> includeTaskTypes = Arrays.asList(Task.TASK_TYPE_RECEIVE,Task.TASK_TYPE_INSPECTION,
                Task.TASK_TYPE_SORTING,Task.TASK_TYPE_SEND_DELIVERY,Task.TASK_TYPE_ACARABILL_SEND_DELIVERY);
        Integer siteCode = task.getCreateSiteCode();
        String key = String.format(OFFLINE_TASK_MAX_E_KEY,siteCode);
        try{
            if(siteCode == null || siteCode <= 0){
                log.error("离线限流处理跳过，因未获取到当前离线任务归属场地信息 ，{}", JsonHelper.toJson(task));
                // 不处理降级
                return false;
            }
            OfflineLogRequest[] offlineLogRequests = JsonHelper.jsonToArray(task.getBody(), OfflineLogRequest[].class);
            if(!includeTaskTypes.contains(offlineLogRequests[0].getTaskType())){
                // 不处理降级
                return false;
            }
            Integer currentLimitingCount = uccPropertyConfiguration.getOfflineCurrentLimitingCount();
            // 限流数量为正整数时才会启用限流
            if(currentLimitingCount >= 0){
                Long nowTime = System.currentTimeMillis();
                //10S时间段
                Long windowTime = 10 * 1000L;
                Long lastBeginTime = nowTime - windowTime;
                if(redisClient.zCount(key,lastBeginTime,nowTime) >= currentLimitingCount){
                    //超过限流 降级
                    return true;
                }
                redisClient.zAdd(key,nowTime, UUID.randomUUID().toString());

                //清理限流ZSET 清理5个窗口周期前的数据
                redisClient.zRemRangeByScore(key,0,nowTime - (5 * windowTime));
            }

        }catch (Exception e){
            log.error("离线限流处理异常，{}",JsonHelper.toJson(task),e);
        }
        return false;
    }
}
