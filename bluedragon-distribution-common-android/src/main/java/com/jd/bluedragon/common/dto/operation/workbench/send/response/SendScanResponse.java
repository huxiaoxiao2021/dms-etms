package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SendScanResponse
 * @Description
 * @Author wyh
 * @Date 2022/6/4 21:49
 **/
public class SendScanResponse implements Serializable {

    private static final long serialVersionUID = -1410653337122346470L;

    /**
     * 确认发货目的地
     */
    public static final int CODE_CONFIRM_DEST = 2001;

    /**
     * 确认绑定集包袋
     */
    public static final int CODE_CONFIRM_MATERIAL = 2002;

    /**
     * 无任务发货场景，扫描第一单确认目的地
     */
    public static final int CODE_NO_TASK_CONFIRM_DEST = 2003;

    /**
     * 装载率超标
     */
    public static final int CODE_LOAD_RATE_EXCEED = 2004;

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
    /**
     * 本次扫描的运单下包裹总数
     */
    private Integer scanWaybillPackSum;

    //=========================

    public Integer getScanPackCount() {
        return scanPackCount;
    }

    public void setScanPackCount(Integer scanPackCount) {
        this.scanPackCount = scanPackCount;
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

    public Integer getScanWaybillPackSum() {
        return scanWaybillPackSum;
    }

    public void setScanWaybillPackSum(Integer scanWaybillPackSum) {
        this.scanWaybillPackSum = scanWaybillPackSum;
    }
}
