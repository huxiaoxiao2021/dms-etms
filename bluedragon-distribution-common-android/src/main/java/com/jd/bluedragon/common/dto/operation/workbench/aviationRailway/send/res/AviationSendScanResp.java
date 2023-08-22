package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.util.Date;

/**
 * @author liwenji
 * @description 发货扫描返回值
 * @date 2023-08-15 18:04
 */
public class AviationSendScanResp {

    /**
     * 本次扫描的包裹数
     */
    private Integer scanPackCount;

    /**
     * 本次扫描箱数
     */
    private Integer scanBoxCount;

    /**
     * 本次扫描拦截包裹数
     */
    private Integer interceptPackCount;

    /**
     * 本次扫描强制发货包裹数
     */
    private Integer forceSendPackCount;

    /**
     * 本次扫描目的地
     */
    private Long curScanDestId;

    /**
     * 本次扫描目的地
     */
    private String curScanDestName;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 明细任务首次扫描标识
     */
    private boolean firstScan;

    /**
     * 明细任务业务主键
     */
    private String sendDetailBizId;

    /**
     * 明细任务创建时间
     */
    private Date createTime;

    public Integer getScanPackCount() {
        return scanPackCount;
    }

    public void setScanPackCount(Integer scanPackCount) {
        this.scanPackCount = scanPackCount;
    }

    public Integer getScanBoxCount() {
        return scanBoxCount;
    }

    public void setScanBoxCount(Integer scanBoxCount) {
        this.scanBoxCount = scanBoxCount;
    }

    public Integer getInterceptPackCount() {
        return interceptPackCount;
    }

    public void setInterceptPackCount(Integer interceptPackCount) {
        this.interceptPackCount = interceptPackCount;
    }

    public Integer getForceSendPackCount() {
        return forceSendPackCount;
    }

    public void setForceSendPackCount(Integer forceSendPackCount) {
        this.forceSendPackCount = forceSendPackCount;
    }

    public Long getCurScanDestId() {
        return curScanDestId;
    }

    public void setCurScanDestId(Long curScanDestId) {
        this.curScanDestId = curScanDestId;
    }

    public String getCurScanDestName() {
        return curScanDestName;
    }

    public void setCurScanDestName(String curScanDestName) {
        this.curScanDestName = curScanDestName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public boolean isFirstScan() {
        return firstScan;
    }

    public void setFirstScan(boolean firstScan) {
        this.firstScan = firstScan;
    }

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
