package com.jd.bluedragon.distribution.sortexception.service;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * Created by guoyongzhi on 2014/11/17.
 */
public interface SortExceptionLogService {
    /**
     * 分拣异常数据插入 sorting—EC表中
     * @param task
     * @return
     */
    public boolean addExpectionLog(Task task);

}
