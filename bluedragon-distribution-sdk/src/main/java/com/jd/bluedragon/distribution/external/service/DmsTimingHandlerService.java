package com.jd.bluedragon.distribution.external.service;

/**
 * 定时处理任务接口
 *
 * @author hujiping
 * @date 2023/3/9 5:19 PM
 */
public interface DmsTimingHandlerService {

    /**
     * 定时处理超时的异常任务
     */
    void timingHandlerOverTimeException();

    /**
     * 定时处理生鲜报废已领取任务并咚咚通知
     */
    void timingHandlerFreshScrapNotice();
    
}
