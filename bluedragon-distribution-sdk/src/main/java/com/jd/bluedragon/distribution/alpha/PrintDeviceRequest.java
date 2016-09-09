package com.jd.bluedragon.distribution.alpha;

/**
 * Created by wuzuxiang on 2016/8/26.
 */
public class PrintDeviceRequest {

    private static final long serialVersionUID = 5799267676878153722L;

    /**
     * b版本编号
     */
    private String versionId;

    /**
     * ISVID
     */
    private String printDeviceId;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getPrintDeviceId() {
        return printDeviceId;
    }

    public void setPrintDeviceId(String printDeviceId) {
        this.printDeviceId = printDeviceId;
    }
}
