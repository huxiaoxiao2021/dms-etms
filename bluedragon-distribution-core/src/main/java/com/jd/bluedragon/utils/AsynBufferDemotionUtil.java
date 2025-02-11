package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
    private DmsConfigManager dmsConfigManager;

    /**
     * 任务限流 按场地
     * @param siteCode
     * @param taskBody
     * @return
     */
    @JProfiler(jKey = "DMS.AsynBufferDemotionUtil.isDemotionOfSite", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public boolean isDemotionOfSite(Integer siteCode,String taskBody){

        List<Integer> includeTaskTypes = Arrays.asList(Task.TASK_TYPE_RECEIVE,Task.TASK_TYPE_INSPECTION,
                Task.TASK_TYPE_SORTING,Task.TASK_TYPE_SEND_DELIVERY,Task.TASK_TYPE_ACARABILL_SEND_DELIVERY);
        try{
            if(siteCode == null || siteCode <= 0){
                //尝试从body解析
                OfflineLogRequest[] offlineLogRequests = JsonHelper.jsonToArray(taskBody, OfflineLogRequest[].class);
                if(offlineLogRequests[0] == null || offlineLogRequests[0].getSiteCode() == null || offlineLogRequests[0].getSiteCode() <= 0){
                    log.error("离线限流处理跳过，因未获取到当前离线任务归属场地信息 ，{}",taskBody);
                    return false;
                }else{
                    //重新赋值siteCode
                    siteCode = offlineLogRequests[0].getSiteCode();
                }
            }

            String key = String.format(OFFLINE_TASK_MAX_E_KEY,siteCode);

            Integer currentLimitingCount = dmsConfigManager.getPropertyConfig().getOfflineCurrentLimitingCount();
            // 限流数量为正整数时才会启用限流
            if(currentLimitingCount > 0){

                OfflineLogRequest[] offlineLogRequests = JsonHelper.jsonToArray(taskBody, OfflineLogRequest[].class);
                if(offlineLogRequests[0] != null && !includeTaskTypes.contains(offlineLogRequests[0].getTaskType())){
                    // 不处理降级
                    return false;
                }

                Long nowTime = System.currentTimeMillis();
                //10S时间段
                Long windowTime = 10 * 1000L;
                Long lastBeginTime = nowTime - windowTime;
                if(redisClient.zCount(key,lastBeginTime,nowTime) >= currentLimitingCount){
                    //超过限流 降级
                    if(log.isWarnEnabled()){
                        log.warn("超过限流降级 {}",taskBody);
                    }
                    return true;
                }
                redisClient.zAdd(key,nowTime, UUID.randomUUID().toString());

                //清理限流ZSET 清理5个窗口周期前的数据
                redisClient.zRemRangeByScore(key,0,nowTime - (5 * windowTime));
            }

        }catch (Exception e){
            log.error("离线限流处理异常，{}",taskBody,e);
        }
        return false;
    }
}
