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
    private List<CargoOperateInfo> loadTaskDetail;

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

    public List<CargoOperateInfo> getLoadTaskDetail() {
        return loadTaskDetail;
    }

    public void setLoadTaskDetail(List<CargoOperateInfo> loadTaskDetail) {
        this.loadTaskDetail = loadTaskDetail;
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
}
