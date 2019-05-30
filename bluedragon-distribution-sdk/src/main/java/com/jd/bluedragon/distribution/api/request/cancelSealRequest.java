package com.jd.bluedragon.distribution.api.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * create by biyubo 2019-05-13
 * 取消封车入参对象
 */
public class cancelSealRequest {
    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 操作类型 1.单个批次号取消封车 2.封车同批次下取消封车
     */
    private Integer operateType;

    /**
     * 操作人
     */
    private String operateUserCode;

    /**
     * 操作时间
     */
    private  String operateTime;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(String operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateTime() {return operateTime;}

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
