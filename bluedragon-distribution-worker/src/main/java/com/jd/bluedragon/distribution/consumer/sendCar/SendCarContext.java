package com.jd.bluedragon.distribution.consumer.sendCar;

import java.io.Serializable;

/**
 * 发车及取消发车MQ上下文
 * Created by wangtingwei on 2015/4/9.
 */
public class SendCarContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作时间
     */
    private String opeTime;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 1、批次号 2、箱号 3、包裹号
     */
    private String batchType;

    public String getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(String opeTime) {
        this.opeTime = opeTime;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }


}
