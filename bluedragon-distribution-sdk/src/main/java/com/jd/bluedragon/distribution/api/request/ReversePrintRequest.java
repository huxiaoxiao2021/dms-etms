package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;

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
     * 员工ERP
     */
    private String staffErpCode;
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

    /**
     * pop商家ID
     */
    private Integer popSupId;

    /**
     * pop商家名称
     */
    private String popSupName;

    /**
     * B商家ID
     */
    private Integer busiId;

    /**
     * B商家名称
     */
    private String busiName;

    /**
     * 运单类型
     */
    private Integer waybillType;

    /**
     * 称重量方信息
     */
    private WeightOperFlow weightOperFlow;

    /**
     * 是否获取称重信息（temp）0不称重 1称重
     */
    private Integer packOpeFlowFlg;

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

    public String getStaffErpCode() {
        return staffErpCode;
    }

    public void setStaffErpCode(String staffErpCode) {
        this.staffErpCode = staffErpCode;
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

    public Integer getPopSupId() {
        return popSupId;
    }

    public void setPopSupId(Integer popSupId) {
        this.popSupId = popSupId;
    }

    public String getPopSupName() {
        return popSupName;
    }

    public void setPopSupName(String popSupName) {
        this.popSupName = popSupName;
    }

    public Integer getBusiId() {
        return busiId;
    }

    public void setBusiId(Integer busiId) {
        this.busiId = busiId;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }

    public WeightOperFlow getWeightOperFlow() {
        return weightOperFlow;
    }

    public void setWeightOperFlow(WeightOperFlow weightOperFlow) {
        this.weightOperFlow = weightOperFlow;
    }

    public Integer getPackOpeFlowFlg() {
        return packOpeFlowFlg;
    }

    public void setPackOpeFlowFlg(Integer packOpeFlowFlg) {
        this.packOpeFlowFlg = packOpeFlowFlg;
    }

    @Override
    public String toString() {
        return "ReversePrintRequest{" +
                "oldCode='" + oldCode + '\'' +
                ", newCode='" + newCode + '\'' +
                ", newPackageCode='" + newPackageCode + '\'' +
                ", staffId=" + staffId +
                ", staffRealName='" + staffRealName + '\'' +
                ", staffErpCode='" + staffErpCode + '\'' +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", operateUnixTime=" + operateUnixTime +
                ", dmsDisCode=" + dmsDisCode +
                ", popSupId=" + popSupId +
                ", popSupName='" + popSupName + '\'' +
                ", busiId=" + busiId +
                ", busiName='" + busiName + '\'' +
                ", weightOperFlow=" + weightOperFlow +
                ", packOpeFlowFlg=" + packOpeFlowFlg +
                "} " + super.toString();
    }
}
