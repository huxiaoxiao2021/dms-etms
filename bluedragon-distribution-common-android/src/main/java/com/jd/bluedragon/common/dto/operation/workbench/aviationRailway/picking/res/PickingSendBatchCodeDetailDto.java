package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2024/4/2 14:25
 * @Description
 */
public class PickingSendBatchCodeDetailDto implements Serializable {
    private static final long serialVersionUID = 4997976038143915037L;
    //批次号
    private String sendCode;
    //首次扫描时间
    private Date firstScanTime;
    //批次完成时间
    private Date completeTime;
    //扫描数量
    private Integer scanItemNum;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Date getFirstScanTime() {
        return firstScanTime;
    }

    public void setFirstScanTime(Date firstScanTime) {
        this.firstScanTime = firstScanTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Integer getScanItemNum() {
        return scanItemNum;
    }

    public void setScanItemNum(Integer scanItemNum) {
        this.scanItemNum = scanItemNum;
    }
}
