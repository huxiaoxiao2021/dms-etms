package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;

/**
 * @ClassName: BaseSystemRequest
 * @Description: 客户端系统请求参数
 * @author: hujiping
 * @date: 2019/1/8 10:50
 */
public class BaseSystemRequest extends JdObject {

    /** 操作人编号_ERP帐号 */
    private String erpCode;

    /** 操作人姓名 */
    private String userName;

    /** 操作人所属站点编号 */
    private String siteCode;

    /** 操作人所属站点编号 */
    private String siteName;

    /** pda版本号 */
    private String pdaVersion;

    /** PDA操作时间 */
    private String operateTime;

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getPdaVersion() {
        return pdaVersion;
    }

    public void setPdaVersion(String pdaVersion) {
        this.pdaVersion = pdaVersion;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
