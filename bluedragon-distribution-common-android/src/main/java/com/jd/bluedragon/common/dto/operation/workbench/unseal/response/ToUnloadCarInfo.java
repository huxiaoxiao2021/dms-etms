package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ToUnloadCarInfo
 * @Description
 * @Author wyh
 * @Date 2022/3/2 20:08
 **/
public class ToUnloadCarInfo extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -5677936458990632239L;

    /**
     * 解封车时间
     */
    private Date deSealCarTime;

    public Date getDeSealCarTime() {
        return deSealCarTime;
    }

    public void setDeSealCarTime(Date deSealCarTime) {
        this.deSealCarTime = deSealCarTime;
    }

    private Boolean _active;

    public Boolean get_active() {
        return _active;
    }

    public void set_active(Boolean _active) {
        this._active = _active;
    }
}
