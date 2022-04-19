package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName DrivingCarInfo
 * @Description
 * @Author wyh
 * @Date 2022/3/2 20:41
 **/
public class DrivingCarInfo extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -836859441242814975L;

    /**
     * 预计到达时间
     */
    private Date predictionArriveTime;

    public Date getPredictionArriveTime() {
        return predictionArriveTime;
    }

    public void setPredictionArriveTime(Date predictionArriveTime) {
        this.predictionArriveTime = predictionArriveTime;
    }

    private Boolean _active;

    public Boolean get_active() {
        return _active;
    }

    public void set_active(Boolean _active) {
        this._active = _active;
    }
}
