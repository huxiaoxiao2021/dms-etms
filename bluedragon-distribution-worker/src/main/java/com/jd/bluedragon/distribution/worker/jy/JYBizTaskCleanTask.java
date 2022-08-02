package com.jd.bluedragon.distribution.worker.jy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.service.task.JYBizTaskCleanService;
import com.jd.bluedragon.distribution.jy.service.task.JYBizUnloadTaskCleanServiceImpl;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.AbstractScheduleTask;
import com.jd.bluedragon.distribution.worker.AbstractScheduler;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jim.cli.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 拣运作业APP业务任务定时清理入口
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/7/14
 * @Description:
 *
 * 由淘宝调度控制单实例单线程执行
 * 任务配置SQL
 * INSERT INTO `bd_dms_worker`.`pamirs_schedule_tasktype`(`TASK_TYPE`, `DEAL_BEAN_NAME`, `HEARTBEAT_RATE`, `JUDGE_DEAD_INTERVAL`, `THREAD_NUMBER`, `EXECUTE_NUMBER`, `FETCH_NUMBER`, `SLEEP_TIME_NODATA`, `SLEEP_TIME_INTERVAL`, `PROCESSOR_TYPE`, `PERMIT_RUN_START_TIME`, `PERMIT_RUN_END_TIME`, `LAST_ASSIGN_TIME`, `LAST_ASSIGN_UUID`, `EXPIRE_OWN_SIGN_INTERVAL`, `GMT_CREATE`, `GMT_MODIFIED`) VALUES ( 'JYBizTaskCleanTask', 'jyBizTaskCleanTask', 30, 300, 1, 50, 100, 10, 5, 'NOTSLEEP', '0 0 2 ? * *', '0 0 9 ? * *', now(), '', NULL, now(), now());
 * INSERT INTO `bd_dms_worker`.`pamirs_schedule_queue`( `TASK_TYPE`, `QUEUE_ID`, `OWN_SIGN`, `BASE_TASK_TYPE`, `CUR_SERVER`, `REQ_SERVER`, `GMT_CREATE`, `GMT_MODIFIED`) VALUES ('JYBizTaskCleanTask', '0', 'DMS', NULL, NULL, NULL, now(), now());
 */
public class JYBizTaskCleanTask extends AbstractScheduler<Task> {

    private static final Logger logger = LoggerFactory.getLogger(JYBizTaskCleanTask.class);

    private static String JY_CLEAN_KEY = "jy:t:clean:%s";

    @Autowired
    @Qualifier("redisClient")
    private Cluster redisClient;

    @Autowired
    @Qualifier("jyBizUnloadTaskCleanService")
    private JYBizTaskCleanService jyBizUnloadTaskCleanService;

    @Autowired
    @Qualifier("jyBizSendTaskCleanService")
    private JYBizTaskCleanService jyBizSendTaskCleanService;

    @Override
    public boolean execute(Object[] objects, String s) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("JYBizTaskCleanTask execute start!");
        }


        Boolean flag = Boolean.TRUE;
        if(!jyBizUnloadTaskCleanService.clean()){
            flag = Boolean.FALSE;
        }
        if(!jyBizSendTaskCleanService.clean()){
            flag = Boolean.FALSE;
        }
        if(logger.isInfoEnabled()){
            logger.info("JYBizTaskCleanTask execute end! flag:{}",flag);
        }
        if(flag){
            redisClient.set(getExeKey(),"1",2, TimeUnit.DAYS,false);
        }else{
            redisClient.del(getExeKey());
        }
        return flag;
    }

    @Override
    public List<Task> selectTasks(String s, int i, List<String> list, int i1) throws Exception {
        //每天只调度一次
        if(canExecute()){
            //模拟任务
            List<Task> tasks = new ArrayList<>();
            tasks.add(new Task());
            return tasks;
        }
        //今天执行过直接返回空任务集合不调用execute
        logger.info("JYBizTaskCleanTask today executed !");
        return new ArrayList<>();
    }


    private boolean canExecute(){
        //每日只调度一次
        return !redisClient.exists(getExeKey());
    }

    private String getExeKey(){
        return String.format(JY_CLEAN_KEY,DateHelper.formatDate(new Date(), Constants.DATE_FORMAT2));
    }

    @Override
    public Comparator<Task> getComparator() {
        return null;
    }
}
