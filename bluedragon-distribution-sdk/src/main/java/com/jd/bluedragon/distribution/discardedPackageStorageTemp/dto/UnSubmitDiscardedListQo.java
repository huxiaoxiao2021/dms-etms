package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;

/**
 * 查询已扫描未提交的记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 17:29:32 周四
 */
public class UnSubmitDiscardedListQo extends BasePagerCondition implements Serializable {
    private static final long serialVersionUID = -3503018670538196586L;

    /**
     * 操作人erp
     */
    private String operatorErp;

    /**
     * 运单类型 1-包裹类 2-信件类
     */
    private Integer waybillType;

    /**
     * 提交状态
     */
    private Integer unSubmitStatus;

    private Integer pageSize;

    public UnSubmitDiscardedListQo() {
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public UnSubmitDiscardedListQo setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
        return this;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public UnSubmitDiscardedListQo setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }

    public Integer getUnSubmitStatus() {
        return unSubmitStatus;
    }

    public UnSubmitDiscardedListQo setUnSubmitStatus(Integer unSubmitStatus) {
        this.unSubmitStatus = unSubmitStatus;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }
}
