package com.jd.bluedragon.distribution.print.domain;


import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-06-06 16:05
 */
public class LogDto implements Serializable {
    
    private String request;
    
    private String response;
    
    private Integer status;
    
    private String log;
    
    private String method;

    private String exception;

    public LogDto(String log) {
        this.log = log;
        this.status = 1;
    }
    public LogDto(String log, String request) {
        this.log = log;
        this.request = request;
        this.status = 1;
    }
    public LogDto(String request, String response, Integer status, String log, String method, String exception) {
        this.request = request;
        this.response = response;
        this.status = status;
        this.log = log;
        this.method = method;
        this.exception = exception;
    }
    
    public LogDto(String log, String response,int status, String method) {
        this.status = status;
        this.response = response;
        this.log = log;
        this.method = method;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
