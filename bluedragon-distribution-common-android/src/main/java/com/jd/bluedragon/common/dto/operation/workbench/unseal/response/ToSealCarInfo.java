package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ProductTypeAgg;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ToSealCarInfo
 * @Description
 * @Author wyh
 * @Date 2022/3/2 20:04
 **/
public class ToSealCarInfo extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = 1425385913367353087L;

    /**
     * 实际到达时间
     */
    private Date actualArriveTime;

    /**
     * 产品类型聚合列表
     */
    private List<ProductTypeAgg> productTypeAggList;

    public Date getActualArriveTime() {
        return actualArriveTime;
    }

    public void setActualArriveTime(Date actualArriveTime) {
        this.actualArriveTime = actualArriveTime;
    }

    private Boolean _active;

    public Boolean get_active() {
        return _active;
    }

    public void set_active(Boolean _active) {
        this._active = _active;
    }

    public List<ProductTypeAgg> getProductTypeAggList() {
        return productTypeAggList;
    }

    public void setProductTypeAggList(List<ProductTypeAgg> productTypeAggList) {
        this.productTypeAggList = productTypeAggList;
    }
}
