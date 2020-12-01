package com.jd.bluedragon.distribution.reprintRecord.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 包裹补打记录
 *
 * @author fanggang7
 * @time 2020-11-01 21:56:41 周日
 */
public class ReprintRecordVo implements Serializable {

    private static final long serialVersionUID = -6098415572664118099L;

    /*
     * 主键
     */
    private Long id;

    /*
     * 条码号
     */
    private String barCode;

    /*
     * 站点编号
     */
    private Integer siteCode;

    /*
     * 站点名称
     */
    private String siteName;

    private Integer operatorCode;

    private String operatorErp;

    private String operatorName;

    private Date operateTime;

    private String operateTimeFormative;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateTimeFormative() {
        return operateTimeFormative;
    }

    public void setOperateTimeFormative(String operateTimeFormative) {
        this.operateTimeFormative = operateTimeFormative;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
