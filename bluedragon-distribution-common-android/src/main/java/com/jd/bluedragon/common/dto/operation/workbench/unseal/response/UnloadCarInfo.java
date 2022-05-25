package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName UnloadCarInfo
 * @Description
 * @Author wyh
 * @Date 2022/3/2 20:12
 **/
public class UnloadCarInfo extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = 5105812931046178518L;
    /**
     * 总件数
     */
    private Long totalCount;

    /**
     * 已卸件数
     */
    private Long unloadCount;

    /**
     * 卸车进度
     */
    private BigDecimal unloadProgress;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getUnloadCount() {
        return unloadCount;
    }

    public void setUnloadCount(Long unloadCount) {
        this.unloadCount = unloadCount;
    }

    public BigDecimal getUnloadProgress() {
        return unloadProgress;
    }

    public void setUnloadProgress(BigDecimal unloadProgress) {
        this.unloadProgress = unloadProgress;
    }

    private Boolean _active;

    public Boolean get_active() {
        return _active;
    }

    public void set_active(Boolean _active) {
        this._active = _active;
    }
}
