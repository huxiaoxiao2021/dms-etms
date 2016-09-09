package com.jd.bluedragon.distribution.alpha;

import java.net.URL;

/**
 * Created by wuzuxiang on 2016/9/1.
 */
public class PrintDeviceResponse {

    /**
     * ISVID的id号
     */
    private String printDeviceId;

    /**
     * ISV对应的版本号
     */
    private String versionId;

    /**
     * 是否最新
     */
    private Integer yn;

    /**
     * 最新版本对应的下载地址
     * @return
     */
    private URL url;

    public String getPrintDeviceId() {
        return printDeviceId;
    }

    public void setPrintDeviceId(String printDeviceId) {
        this.printDeviceId = printDeviceId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
