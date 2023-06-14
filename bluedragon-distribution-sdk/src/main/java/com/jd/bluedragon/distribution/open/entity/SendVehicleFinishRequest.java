package com.jd.bluedragon.distribution.open.entity;

import java.util.List;

/**
 * 装车发货完成
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-06-05 18:14:21 周一
 */
public class SendVehicleFinishRequest extends BaseRequest {

    private static final long serialVersionUID = -660082539944110885L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 批次号信息
     */
    private List<String> batchCodes;

    /**
     * 扫描开始时间
     */
    private Long scanBeginTime;

    /**
     * 扫描结束时间
     */
    private Long scanEndTime;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public Long getScanBeginTime() {
        return scanBeginTime;
    }

    public void setScanBeginTime(Long scanBeginTime) {
        this.scanBeginTime = scanBeginTime;
    }

    public Long getScanEndTime() {
        return scanEndTime;
    }

    public void setScanEndTime(Long scanEndTime) {
        this.scanEndTime = scanEndTime;
    }
}
