package com.jd.bluedragon.common.dto.seal.request;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/8
 */
public class CancelSealRequest  implements Serializable {
    private static final long serialVersionUID = -1L;
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

    /**
     * 条码：包裹号、箱号、批次号等
     */
    private String barCode;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

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
