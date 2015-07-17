package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

 
public class VersionResponse extends JdResponse {

    private static final long serialVersionUID = -8433550306554078937L;

    public static final Integer CODE_Version_ERROR = 40000;
    public static final String MESSAGE_Version_ERROR = "无对应的版本配置信息";

    /** 分拣中心编号 */
    private String siteCode;
    
    /** 应用程序类型：11-卡西欧PDA,12-方正PDA, 20-Winform客户端 */
    private Integer programType;
    
    /** 版本号 */
    private String versionCode;
    
    /** 下载地址 */
    private String downloadUrl;

    public VersionResponse() {
    }

    public VersionResponse(Integer code, String message) {
        super(code, message);
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getProgramType() {
        return programType;
    }

    public void setProgramType(Integer programType) {
        this.programType = programType;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}
