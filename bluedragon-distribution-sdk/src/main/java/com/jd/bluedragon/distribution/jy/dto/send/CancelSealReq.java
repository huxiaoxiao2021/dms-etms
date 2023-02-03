package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/8
 */
public class CancelSealReq extends JyReqBaseDto implements Serializable {
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
