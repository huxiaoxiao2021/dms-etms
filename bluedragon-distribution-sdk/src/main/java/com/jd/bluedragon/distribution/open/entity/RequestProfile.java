package com.jd.bluedragon.distribution.open.entity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: RequestProfile
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 10:01
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class RequestProfile {

    /**
     * 系统来源
     */
    private String sysSource;

    /**
     * 调用链ID
     */
    private String traceId;

    /**
     * 时区，GMT+8
     */
    private String timeZone;

    /**
     * 毫秒时间戳
     */
    private Long timestamp;

    public String getSysSource() {
        return sysSource;
    }

    public void setSysSource(String sysSource) {
        this.sysSource = sysSource;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
