package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;
import java.util.Date;


public class Board implements Serializable {

    private static final long serialVersionUID = 818613838435071234L;
    private Long id;

    /**
     * 板号
     */
    private String code;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 板状态
     */
    private Integer status;

    /**
     * 开板时间
     */
    private Date createTime;

    /** 目的地编号 **/
    private Integer destinationId;

    private String sendCode;

    public Long getId() {
        return id;
    }

    public Board setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }
}
