package com.jd.bluedragon.distribution.jy.dto.unload.trust;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2024/3/6 17:52
 * @Description
 */
public class MqRetryBaseDto implements Serializable {
    static final long serialVersionUID = 1L;

    /**
     *  首次消费时间: 重试队列字段
     */
    private Long firstConsumerTime;
    /**
     * 异常重试间隔分钟数：firstConsumerTime + retryIntervalMinutes 分钟后可再次重试
     */
    private Integer retryIntervalMinutes;
    /**
     * 重试标识：区分是原始消息还是重试消息
     * 原始消息：false
     * 重试消息：true
     */
    private Boolean retryFlag;


    public Long getFirstConsumerTime() {
        return firstConsumerTime;
    }

    public void setFirstConsumerTime(Long firstConsumerTime) {
        this.firstConsumerTime = firstConsumerTime;
    }

    public Integer getRetryIntervalMinutes() {
        return retryIntervalMinutes;
    }

    public void setRetryIntervalMinutes(Integer retryIntervalMinutes) {
        this.retryIntervalMinutes = retryIntervalMinutes;
    }

    public Boolean getRetryFlag() {
        return retryFlag;
    }

    public void setRetryFlag(Boolean retryFlag) {
        this.retryFlag = retryFlag;
    }
}
