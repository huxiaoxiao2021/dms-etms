package com.jd.bluedragon.distribution.worker.sorting;

import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.TimeUnit;

public class SortingTask extends DBSingleScheduler {
    
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String SPLIT_CHAR="$";

    private final static String TASK_SORTING_FINGERPRINT_1200_5S = "TASK_1200_FP_5S_"; //5前缀

    @Autowired
    private SortingService sortingService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {


        String fingerPrintKey = TASK_SORTING_FINGERPRINT_1200_5S + task.getCreateSiteCode() + task.getReceiveSiteCode() + task.getKeyword2();
        try{
            //判断是否重复分拣, 5秒内如果同操作场地、同目的地、同扫描号码即可判断为重复操作。立刻置失败，转到下一次执行。

            Long incrResult = redisClientCache.incr(fingerPrintKey);
            redisClientCache.expire(fingerPrintKey, 5, TimeUnit.SECONDS);
            if(incrResult>1){//说明有重复任务
               return false;
            }
        }catch(Error e){
            this.logger.error("获得1200分拣任务指纹失败"+task.getBody(), e);
        }


        boolean result = false;
        try {
            this.logger.info("task id is " + task.getId());
            result = this.sortingService.doSorting(task);
        } catch (Exception e) {
            StringBuilder builder=new StringBuilder("task id is");
            builder.append(task.getId());
            builder.append(SPLIT_CHAR).append(task.getBoxCode());
            builder.append(SPLIT_CHAR).append(task.getKeyword1());
            builder.append(SPLIT_CHAR).append(task.getKeyword2());
            this.logger.error(builder.toString());
            this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
            result = Boolean.FALSE;
        }

        redisClientCache.del(fingerPrintKey);
		return result;
	}



}
