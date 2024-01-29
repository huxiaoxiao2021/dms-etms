package com.jd.bluedragon.distribution.api.request.base;

import java.io.Serializable;

/**
 * 请求者信息入参
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public class RequestProfile implements Serializable {

    private static final long serialVersionUID = -2999072617785752698L;

    /**
     * 调用系统来源
     */
    private String systemCode;

    /**
     * 调用key
     */
    private String secretKey;

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

    public RequestProfile() {
    }

    public String getSystemCode() {
        return systemCode;
    }

    public RequestProfile setSystemCode(String systemCode) {
        this.systemCode = systemCode;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public RequestProfile setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public RequestProfile setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public RequestProfile setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public RequestProfile setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
