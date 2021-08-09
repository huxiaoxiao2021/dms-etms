package com.jd.bluedragon.common.dto.stock;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存盘点扫描
 *
 * @author hujiping
 * @date 2021/6/4 2:12 下午
 */
public class StockInventoryScanDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 波次编码
     */
    private String waveCode;

    /**
     * 波次开始时间
     */
    private Date waveBeginTime;

    /**
     * 波次结束时间
     */
    private Date waveEndTime;

    /**
     * 操作站点
     */
    private Integer operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人名称
     */
    private String operateUserName;

    /**
     * 扫描时间
     */
    private Date scanTime;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public Date getWaveBeginTime() {
        return waveBeginTime;
    }

    public void setWaveBeginTime(Date waveBeginTime) {
        this.waveBeginTime = waveBeginTime;
    }

    public Date getWaveEndTime() {
        return waveEndTime;
    }

    public void setWaveEndTime(Date waveEndTime) {
        this.waveEndTime = waveEndTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Date getScanTime() {
        return scanTime;
    }

    public void setScanTime(Date scanTime) {
        this.scanTime = scanTime;
    }
}
