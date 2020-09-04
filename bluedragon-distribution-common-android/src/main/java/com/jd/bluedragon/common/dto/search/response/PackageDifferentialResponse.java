package com.jd.bluedragon.common.dto.search.response;

import java.io.Serializable;

public class PackageDifferentialResponse implements Serializable {
    private static final long serialVersionUID = 5640679967099473566L;

    /** 箱号 */
    private String boxCode;

    /** 包裹号 */
    private String packageBarcode;

    /** 扫描状态 */
    private Integer scanStatus;

    /** 扫描状态说明 */
    private String scanStatusStr;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public Integer getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(Integer scanStatus) {
        this.scanStatus = scanStatus;
    }

    public String getScanStatusStr() {
        return scanStatusStr;
    }

    public void setScanStatusStr(String scanStatusStr) {
        this.scanStatusStr = scanStatusStr;
    }
}
