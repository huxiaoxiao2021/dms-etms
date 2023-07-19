package com.jd.bluedragon.distribution.open.entity;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: BatchSendPageRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 15:42
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class BatchSendPageRequest extends BatchPageRequest{

    /**
     * 货物信息
     */
    private List<CargoOperateInfo> cargoNoList;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 批次号信息
     */
    private String batchCode;

    /**
     * 操作站点编码
     */
    private String operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 扫描开始时间
     */
    private Long scanBeginTime;

    /**
     * 扫描结束时间
     */
    private Long scanEndTime;

    public List<CargoOperateInfo> getCargoNoList() {
        return cargoNoList;
    }

    public void setCargoNoList(List<CargoOperateInfo> cargoNoList) {
        this.cargoNoList = cargoNoList;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Long getScanBeginTime() {
        return scanBeginTime;
    }

    public void setScanBeginTime(Long scanBeginTime) {
        this.scanBeginTime = scanBeginTime;
    }

    public Long getScanEndTime() {
        return scanEndTime;
    }

    public void setScanEndTime(Long scanEndTime) {
        this.scanEndTime = scanEndTime;
    }
}
