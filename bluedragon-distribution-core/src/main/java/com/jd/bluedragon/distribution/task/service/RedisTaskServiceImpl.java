package com.jd.bluedragon.distribution.task.service;

import java.util.Date;

import com.jd.bluedragon.distribution.base.service.SysConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.redis.QueueKeyInfo;
import com.jd.bluedragon.core.redis.RedisTaskHelper;
import com.jd.bluedragon.core.redis.TaskModeAware;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.ump.profiler.proxy.Profiler;

@Service("redisTaskService")
public class RedisTaskServiceImpl implements RedisTaskService {
	
	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	RedisTaskHelper redisTaskHelper;

	@Autowired
	RedisManager redisManager;

    @Autowired
    private RedisTaskQueueService redisTaskQueueService;

    @Autowired
    private SysConfigService sysConfigService;

    private static final String REDIS_ERROR_UMP_ALERT_KEY = "Bluedragon_dms_center.redis.write.QueueNotExist";
    
	@Profiled(tag = "RedisTaskService.add")
	public boolean add(TaskModeAware task) {
		
		try {
			QueueKeyInfo qkInfo = task.findQueueKey();
			if(qkInfo==null){//校验队列键信息是否为空，为空返回false
				return false;
			}
			
			String queueKey = qkInfo.getQueueKey();
			//验证队列key是否可用
			if(redisTaskHelper.validateQueueKey(qkInfo)){
				//如果插入成功，则返回true
                if(redisTaskQueueService.getQueueLength(queueKey)<sysConfigService.getMaxRedisQueueSize()) {
                    Long result = redisManager.rpush(queueKey, JsonUtil.getInstance()
                            .object2Json(task));
                    if (result > 0) {
                        return true;
                    }
                }
			} else {
				// redis list name找不到的时候报警
				String warnStr = "Redis List名" + queueKey + "不存在";
				logger.warn(warnStr);
				//进行报警
				pushAlert(REDIS_ERROR_UMP_ALERT_KEY, warnStr);
			}
			
		} catch (Exception e) {
			logger.error("保存任务到redis失败：", e);
		}
		return false;
	}

	private void pushAlert(String key, String msg) {
		logger.info("RedisTaskService.pushAlert ");
		Profiler.businessAlarm(key, new Date().getTime(), msg);
	}
}
