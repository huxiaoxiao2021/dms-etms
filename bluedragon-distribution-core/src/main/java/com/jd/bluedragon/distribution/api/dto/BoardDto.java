package com.jd.bluedragon.distribution.api.dto;

/**
 * @author lijie
 * @date 2019/10/31 13:55
 */
public class BoardDto {

    private String code;
    private String destination;
    private Integer status;
    private String date;
    private String time;
    private Integer destinationId;
    /**
     * 扫描数量
     */
    private Integer scanQuantity;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public Integer getScanQuantity() {
        return scanQuantity;
    }

    public void setScanQuantity(Integer scanQuantity) {
        this.scanQuantity = scanQuantity;
    }
}
