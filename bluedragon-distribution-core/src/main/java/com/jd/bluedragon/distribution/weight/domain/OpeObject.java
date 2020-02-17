package com.jd.bluedragon.distribution.weight.domain;

/**
 * Created by yanghongqiang on 2015/5/10.
 */
public class OpeObject {
    /// <summary>
    /// 包裹号
    /// </summary>
    private String packageCode;

    /// <summary>
    /// 包裹重量	单位（KG）
    /// </summary>
    private Float pWeight;
    /// <summary>
    /// 包裹长	单位（CM）
    /// </summary>
    private Float pLength;
    /// <summary>
    /// 包裹宽	单位（CM）
    /// </summary>
    private Float pWidth;
    /// <summary>
    /// 包裹高	单位（CM）
    /// </summary>
    private Float pHigh;
    /// <summary>
    /// 操作人ID
    /// </summary>
    private int opeUserId;
    /// <summary>
    /// 操作人姓名
    /// </summary>
    private String opeUserName;
    /// <summary>
    /// 操作站点ID
    /// </summary>
    private int opeSiteId;
    /// <summary>
    /// 操作站点名称
    /// </summary>
    private String opeSiteName;
    /// <summary>
    /// 操作时间	格式：yyyy-MM-dd HH:mm:ss
    /// </summary>
    private String opeTime;
    /// <summary>
    /// 长包裹 0:普通包裹 1:长包裹
    /// </summary>
    private Integer longPackage;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Float getpWeight() {
        return pWeight;
    }

    public void setpWeight(Float pWeight) {
        this.pWeight = pWeight;
    }

    public Float getpLength() {
        return pLength;
    }

    public void setpLength(Float pLength) {
        this.pLength = pLength;
    }

    public Float getpWidth() {
        return pWidth;
    }

    public void setpWidth(Float pWidth) {
        this.pWidth = pWidth;
    }

    public Float getpHigh() {
        return pHigh;
    }

    public void setpHigh(Float pHigh) {
        this.pHigh = pHigh;
    }

    public int getOpeUserId() {
        return opeUserId;
    }

    public void setOpeUserId(int opeUserId) {
        this.opeUserId = opeUserId;
    }

    public String getOpeUserName() {
        return opeUserName;
    }

    public void setOpeUserName(String opeUserName) {
        this.opeUserName = opeUserName;
    }

    public int getOpeSiteId() {
        return opeSiteId;
    }

    public void setOpeSiteId(int opeSiteId) {
        this.opeSiteId = opeSiteId;
    }

    public String getOpeSiteName() {
        return opeSiteName;
    }

    public void setOpeSiteName(String opeSiteName) {
        this.opeSiteName = opeSiteName;
    }

    public String getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(String opeTime) {
        this.opeTime = opeTime;
    }

    public Integer getLongPackage() {
        return longPackage;
    }

    public void setLongPackage(Integer longPackage) {
        this.longPackage = longPackage;
    }
}
