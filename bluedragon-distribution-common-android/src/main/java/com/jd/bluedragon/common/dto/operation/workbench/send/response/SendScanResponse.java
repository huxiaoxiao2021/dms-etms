package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;

/**
 * @ClassName SendScanResponse
 * @Description
 * @Author wyh
 * @Date 2022/6/4 21:49
 **/
public class SendScanResponse implements Serializable {

    private static final long serialVersionUID = -1410653337122346470L;

    /**
     * 本次扫描的包裹数
     */
    private Integer scanPackCount;

    /**
     * 本次扫描目的地
     */
    private Long curScanDestId;

    /**
     * 本次扫描目的地
     */
    private String curScanDestName;

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
}
