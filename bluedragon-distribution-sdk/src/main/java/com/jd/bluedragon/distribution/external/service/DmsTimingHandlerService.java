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

    /**
     *处理超48小时客服未反馈破损任务状态
     */
    void dealDamageExpTaskOverTwoDags();
    

    /**
     * 定时清理混扫任务信息
     */
    void timingHandlerDeleteCTTGroupData();

}
