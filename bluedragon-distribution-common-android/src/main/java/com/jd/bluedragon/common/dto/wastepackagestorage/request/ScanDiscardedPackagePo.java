package com.jd.bluedragon.common.dto.wastepackagestorage.request;

import java.io.Serializable;

/**
 * 弃件扫描请求参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 20:28:35 周四
 */
public class ScanDiscardedPackagePo extends DiscardedPackageBasePo implements Serializable {

    private static final long serialVersionUID = -8729643477002920596L;

    /**
     * 状态 0 弃件暂存 1 弃件出库 2 已认领
     */
    private Integer status;

    /**
     *  扫描单据号
     */
    private String barCode;

    /**
     * 操作类型-1-弃件暂存 2-弃件废弃
     */
    private Integer operateType;

    /**
     * 运单类型 1-包裹类 2-信件类
     */
    private Integer waybillType;

    public ScanDiscardedPackagePo() {
    }

    public Integer getStatus() {
        return status;
    }

    public ScanDiscardedPackagePo setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public ScanDiscardedPackagePo setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public ScanDiscardedPackagePo setOperateType(Integer operateType) {
        this.operateType = operateType;
        return this;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public ScanDiscardedPackagePo setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }
}
