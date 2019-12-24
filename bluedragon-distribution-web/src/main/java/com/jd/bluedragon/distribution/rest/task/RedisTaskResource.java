package com.jd.bluedragon.distribution.rest.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.redis.QueueKeyInfo;
import com.jd.bluedragon.core.redis.RedisTaskHelper;
import com.jd.bluedragon.core.redis.TaskMode;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tbschedule.dto.ScheduleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class RedisTaskResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisTaskHelper redisTaskHelper;

    @Autowired
    RedisManager redisManager;

    @Autowired
    BaseService baseService;

    @GET
    @Path("/task/redis/checkRedisTask")
    public TaskResponse checkRedisTask(@QueryParam("type") Integer type,
                                       @QueryParam("upLimit") Integer upLimit,
                                       @QueryParam("ownSign") String ownSign) {
        // 取得任务的key
        Task task = new Task();
        task.setType(type);
        task.setOwnSign(ownSign);
        task.setBody("Find task num");

        QueueKeyInfo queueKeyInfo = task.findQueueKey();
        Map<String, ScheduleQueue> redisQueueMap = redisTaskHelper.getQueueMap(queueKeyInfo);

        // 将此类型的所有队列中的数据数量加和
        long taskCount = 0;
        if (redisQueueMap != null) {
            Collection<ScheduleQueue> redisQueues = redisQueueMap.values();
            for (ScheduleQueue queue : redisQueues) {
                taskCount += redisTaskHelper.getRedisClient().lLen(
                        queue.getCacheKey());
            }
        }

        // 判断是否超限
        if (taskCount > upLimit) {
            return new TaskResponse(JdResponse.CODE_SEE_OTHER, "数量超出上限!");
        } else {
            return new TaskResponse(JdResponse.CODE_OK,
                    String.valueOf(taskCount));
        }
    }

    @GET
    @Path("/task/redis/moveTaskToDB")
    public TaskResponse moveTaskToDB(@QueryParam("taskType") String taskType,
                                     @QueryParam("ownSign") String ownSign) {
        TaskResponse response = new TaskResponse();

        QueueKeyInfo queueKeyInfo = new QueueKeyInfo();
        queueKeyInfo.setTaskType(taskType);
        queueKeyInfo.setOwnSign(ownSign);

        try {
            Long affectCount = redisManager.moveTaskToDB(queueKeyInfo);

            response.setCode(JdResponse.CODE_OK);
            response.setMessage(String.valueOf(affectCount));
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(String.valueOf(e.getMessage()));

            log.error(e.getMessage(), e);
        }

        return response;
    }

    @GET
    @Path("/task/redis/query")
    public TaskResponse redisQuery(@QueryParam("key") String key) {
        TaskResponse response = new TaskResponse();
        String value = JdResponse.MESSAGE_OK;
        try {
            value = redisManager.get(key);
        } catch (Exception e) {
            log.error("redisQuery failed", e);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(value);
        return response;
    }

    @GET
    @Path("/task/redis/del")
    public TaskResponse redisDel(@QueryParam("key") String key) {
        TaskResponse taskResponse = new TaskResponse();
        Long result = redisManager.del(key);
        taskResponse.setCode(JdResponse.CODE_OK);
        taskResponse.setMessage(result + "");
        return taskResponse;
    }

    @GET
    @Path("/task/redis/taskmode")
    public TaskResponse getTaskMode() {
        TaskResponse response = new TaskResponse();
        String value = JdResponse.MESSAGE_OK;
        TaskMode taskMode = null;
        try {
            taskMode = baseService.getTaskMode();
        } catch (Exception e) {
            log.error("redisQuery failed", e);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(e.getMessage());
        }
        if (taskMode != null) {
            if (taskMode.equals(TaskMode.DB)) {
                value = "DB";
            } else if (taskMode.equals(TaskMode.REDIS)) {
                value = "REDIS";
            } else {
                value = "OTHERS";
            }
        }
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(value);
        return response;
    }

    @GET
    @Path("/task/redis/test")
    public void testExpire() {
        try {
            SendM sendM = new SendM();
            sendM.setCreateSiteCode(120191);
            sendM.setBoxCode("9398467653-1-1-1");
            String cachedKey = CacheKeyConstants.REDIS_KEY_IS_DELIVERY
                    + sendM.getCreateSiteCode()
                    + sendM.getBoxCode();
            Boolean isExist = redisManager.exists(cachedKey);
            Long result = redisManager.lpush(cachedKey, JsonHelper.toJson(sendM));
            if (result <= 0) {
                log.warn("save to redis of key <{}> value <{}> fail",cachedKey,JsonHelper.toJson(sendM));

            } else {
                log.warn("save to redis of key <{}> value <{}> success",cachedKey,JsonHelper.toJson(sendM));
                if (!isExist) {
                    // 如果是列表key是第一次插入的，则设置整体的超时时间
                    Boolean expireResult = redisManager.expire(cachedKey, 5);
                    if (!expireResult) {
                        log.warn("set expire of key <{}> second <{}> fail, expireResult = {}",cachedKey,5,expireResult);
                    } else {
                        log.warn("set expire of key <{}> second <{}> success",cachedKey,5);
                        log.warn("get senm from redis of key <{}> value <{}> ",cachedKey,redisManager.lrange(cachedKey, 0, -1));
                        Thread.sleep(5 * 1000);
                        log.warn("get senm after 5 seconds from redis of key <{}> value <{}>"
                                ,cachedKey,redisManager.lrange(cachedKey, 0, -1));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("save to redis throws exceptions ", ex);
        }
    }
}
