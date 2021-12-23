package com.jd.bluedragon.common.dto.wastepackagestorage.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 查询未扫描的记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 17:29:32 周四
 */
public class QueryUnScanDiscardedPackagePo extends DiscardedPackageBasePo implements Serializable {
    private static final long serialVersionUID = -3503018670538196586L;

    /**
     * 运单类型 1-包裹类 2-信件类
     */
    private Integer waybillType;

    /**
     * 搜索单据
     */
    private String barCode;

    public QueryUnScanDiscardedPackagePo() {
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public QueryUnScanDiscardedPackagePo setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public QueryUnScanDiscardedPackagePo setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    @Override
    public String toString() {
        return "QueryUnScanDiscardedPackagePo{" +
                "waybillType=" + waybillType +
                ", barCode='" + barCode + '\'' +
                '}';
    }
}
