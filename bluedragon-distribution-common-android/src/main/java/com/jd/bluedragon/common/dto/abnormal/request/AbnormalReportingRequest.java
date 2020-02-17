package com.jd.bluedragon.common.dto.abnormal.request;

import com.jd.bluedragon.common.dto.abnormal.DmsAbnormalReasonDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AbnormalReportingRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * 条码集合
     * */
    private List<String> barCodes;

    /*
     * 异常原因对象
     * */
    private DmsAbnormalReasonDto dmsAbnormalReasonDto;

    /*
     * 上传图片地址
     * */
    private List<String> imgUrls;

    /*
     * 处理部门编号
     * */
    private String dealDeptCode;

    /*
     * 处理部门名称
     * */
    private String dealDeptName;

    /*
     * 站点编号
     * */
    private Integer siteCode;

    /*
     * 站点名称
     * */
    private String siteName;

    /*
     * 用户编号
     * */
    private Integer userCode;

    /*
     * 用户ERP
     * */
    private String userErp;

    /*
     * 用户ERP
     * */
    private String userName;

    /*
     * 备注说明
     * */
    private String remark;

    /*
     * 操作时间
     * */
    private Date operateTime;

    /*
     * 站点类型
     * */
    private Integer dealDeptType;

    public List<String> getBarCodes() {
        return barCodes;
    }

    public void setBarCodes(List<String> barCodes) {
        this.barCodes = barCodes;
    }

    public DmsAbnormalReasonDto getDmsAbnormalReasonDto() {
        return dmsAbnormalReasonDto;
    }

    public void setDmsAbnormalReasonDto(DmsAbnormalReasonDto dmsAbnormalReasonDto) {
        this.dmsAbnormalReasonDto = dmsAbnormalReasonDto;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public String getDealDeptCode() {
        return dealDeptCode;
    }

    public void setDealDeptCode(String dealDeptCode) {
        this.dealDeptCode = dealDeptCode;
    }

    public String getDealDeptName() {
        return dealDeptName;
    }

    public void setDealDeptName(String dealDeptName) {
        this.dealDeptName = dealDeptName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDealDeptType() {
        return dealDeptType;
    }

    public void setDealDeptType(Integer dealDeptType) {
        this.dealDeptType = dealDeptType;
    }
}
