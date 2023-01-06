package com.jd.bluedragon.distribution.open.entity;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: BatchInspectionRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 14:41
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class BatchInspectionPageRequest extends BatchPageRequest {

    /**
     * 货物信息
     */
    private List<CargoOperateInfo> unloadDetailCargoList;

    /**
     * 批次信息
     */
    private String batchCode;

    /**
     * 操作站点
     */
    private String operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    public List<CargoOperateInfo> getUnloadDetailCargoList() {
        return unloadDetailCargoList;
    }

    public void setUnloadDetailCargoList(List<CargoOperateInfo> unloadDetailCargoList) {
        this.unloadDetailCargoList = unloadDetailCargoList;
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
