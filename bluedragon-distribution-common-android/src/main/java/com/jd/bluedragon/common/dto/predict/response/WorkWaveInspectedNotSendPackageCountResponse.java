package com.jd.bluedragon.common.dto.predict.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ProductTypeAgg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkWaveInspectedNotSendPackageCountResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 时间
     */
    private Date queryTime;
    /**
     * 当前场地
     */
    private Integer currentSiteCode;

    /**
     * 波次待扫描件数
     */
    private Long totalInspectedNotSendCount;

    /**
     * 产品类型的数量
     */
    private List<ProductTypeAgg> inspectedNotSendCountByProduct = new ArrayList<ProductTypeAgg>();

    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public Long getTotalInspectedNotSendCount() {
        return totalInspectedNotSendCount;
    }

    public void setTotalInspectedNotSendCount(Long totalInspectedNotSendCount) {
        this.totalInspectedNotSendCount = totalInspectedNotSendCount;
    }

    public List<ProductTypeAgg> getInspectedNotSendCountByProduct() {
        return inspectedNotSendCountByProduct;
    }

    public void setInspectedNotSendCountByProduct(List<ProductTypeAgg> inspectedNotSendCountByProduct) {
        this.inspectedNotSendCountByProduct = inspectedNotSendCountByProduct;
    }
}
