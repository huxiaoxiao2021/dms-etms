package com.jd.bluedragon.common.dto.wastepackagestorage.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 提交完成已扫描的记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 18:03:14 周四
 */
public class SubmitDiscardedPackagePo extends DiscardedPackageBasePo implements Serializable {
    private static final long serialVersionUID = -3503018670538196586L;

    /**
     * 运单类型 1-包裹类 2-信件类
     */
    private Integer waybillType;

    /**
     * 强制提交
     */
    private Integer forceSubmit;

    public SubmitDiscardedPackagePo() {
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public SubmitDiscardedPackagePo setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }

    public Integer getForceSubmit() {
        return forceSubmit;
    }

    public SubmitDiscardedPackagePo setForceSubmit(Integer forceSubmit) {
        this.forceSubmit = forceSubmit;
        return this;
    }

    @Override
    public String toString() {
        return "SubmitDiscardedPackagePo{" +
                "waybillType=" + waybillType +
                ", forceSubmit=" + forceSubmit +
                '}';
    }
}
