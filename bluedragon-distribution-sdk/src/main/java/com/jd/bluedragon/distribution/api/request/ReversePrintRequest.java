package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;

import java.security.Timestamp;
import java.util.Date;

/**
 * 逆向换单打印对象
 * Created by wangtingwei on 14-8-7.
 */
public class ReversePrintRequest extends JdObject {

    /**
     * 原单号
     */
    private String oldCode;

    /**
     * 新单号
     */
    private String newCode;

    /**
     * 新包裹号
     */
    private String newPackageCode ;

    /**
     * 员工ID
     */
    private int staffId;

    /**
     * 员工真实姓名
     */
    private String staffRealName;

    /**
     * 操作人站点ID
     */
    private int siteCode;

    /**
     * 操作人站点名称
     */
    private String siteName;

    /**
     * 操作时间
     */
    private long operateUnixTime;

    /**
     * 分拣中心id
     * */
    private Integer dmsDisCode;

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffRealName() {
        return staffRealName;
    }

    public void setStaffRealName(String staffRealName) {
        this.staffRealName = staffRealName;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public long getOperateUnixTime() {
        return operateUnixTime;
    }

    public void setOperateUnixTime(long operateUnixTime) {
        this.operateUnixTime = operateUnixTime;
    }

    public Integer getDmsDisCode() {
        return dmsDisCode;
    }

    public void setDmsDisCode(Integer dmsDisCode) {
        this.dmsDisCode = dmsDisCode;
    }

    public String getNewPackageCode() {
        return newPackageCode;
    }

    public void setNewPackageCode(String newPackageCode) {
        this.newPackageCode = newPackageCode;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("oldCode:");
        stringBuilder.append(this.oldCode);
        stringBuilder.append(";newCode:");
        stringBuilder.append(this.newCode);
        stringBuilder.append(";newPackageCode:");
        stringBuilder.append(this.newPackageCode);
        return stringBuilder.toString();
    }
}
