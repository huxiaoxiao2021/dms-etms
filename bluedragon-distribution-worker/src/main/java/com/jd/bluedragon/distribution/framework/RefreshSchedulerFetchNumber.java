package com.jd.bluedragon.distribution.framework;

import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.TBScheduleManager;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 动态刷新抓取任务数量
 * 数据来源为调服务器
 * Created by wangtingwei on 2015/9/29.
 */
public class RefreshSchedulerFetchNumber {

    private static final Log LOGGER= LogFactory.getLog(RefreshSchedulerFetchNumber.class);
    /**
     * 刷新定时器
     */
    private Timer refreshTimer;

    /**
     * 刷新间隔【分钟】
     */
    private int   interval;

    /**
     * 首次执行在XX【分钟】之后
     */
    private int   firstInterval;

    public int getFirstInterval() {
        return firstInterval;
    }

    public void setFirstInterval(int firstInterval) {
        this.firstInterval = firstInterval;
    }

    /**
     * 当前调度器
     */
    @Autowired
    @Qualifier("managerFactory")
    private TBScheduleManagerFactory currentSchedule;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public TBScheduleManagerFactory getCurrentSchedule() {
        return currentSchedule;
    }

    public void setCurrentSchedule(TBScheduleManagerFactory currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    public RefreshSchedulerFetchNumber(){
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("构建刷新【任务数量】任务");
        }
        refreshTimer=new Timer("RefreshSchedulerFetchNumber");

    }

    /**
     * 启动任务
     */
    public void start(){
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("开始启动刷新【任务数量】任务");
        }
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        },new Date(System.currentTimeMillis()+firstInterval*60000),interval*60000);
    }

    /**
     * 刷新任务配置
     */
    private final void refresh(){
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("刷新获取任务数量");
        }
        List<TBScheduleManager> list= this.currentSchedule.getScheduleManagerList();
        for (TBScheduleManager item:list){
            if(null==item.getTaskTypeInfo()){
                continue;
            }
            refreshTaskType(item.getTaskTypeInfo());
        }
    }

    /**
     * 刷新单个任务
     * @param taskType
     */
    private final void refreshTaskType(ScheduleTaskType taskType){
        if(LOGGER.isInfoEnabled()){
            LOGGER.info(taskType.getBaseTaskType()+"获取任务数量为"+taskType.getFetchDataNumber());
        }
        ScheduleTaskType newTaskType=null;
        try {
            newTaskType = TBScheduleManagerFactory.getScheduleConfigCenter().loadTaskTypeBaseInfo(taskType.getBaseTaskType());
        }catch (Exception ex){
            LOGGER.error("读取TASKTYPE异常",ex);
        }
        if(null!=newTaskType){
            taskType.setFetchDataNumber(newTaskType.getFetchDataNumber());
            if(LOGGER.isInfoEnabled()){
                LOGGER.info("重新设置"+taskType.getBaseTaskType()+"任务获取数量值为"+newTaskType.getFetchDataNumber());
            }
        }
    }


    public void close(){
        refreshTimer.cancel();

    }

}
