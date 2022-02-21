package com.jd.bluedragon.common.dto.wastepackagestorage.request;

import java.io.Serializable;

/**
 * 查询已扫描未提交的记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 17:29:32 周四
 */
public class QueryUnSubmitDiscardedListPo extends DiscardedPackageBasePo implements Serializable {
    private static final long serialVersionUID = -3503018670538196586L;

    /**
     * 运单类型 1-包裹类 2-信件类
     */
    private Integer waybillType;

    private Integer pageNumber;

    private Integer pageSize;

    public QueryUnSubmitDiscardedListPo() {
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public QueryUnSubmitDiscardedListPo setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public QueryUnSubmitDiscardedListPo setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public QueryUnSubmitDiscardedListPo setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public String toString() {
        return "QueryUnSubmitDiscardedPackagePo{" +
                "waybillType=" + waybillType +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
