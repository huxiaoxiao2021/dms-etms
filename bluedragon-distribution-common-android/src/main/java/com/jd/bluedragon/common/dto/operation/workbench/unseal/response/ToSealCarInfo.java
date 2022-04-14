package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.Date;

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

    public Date getActualArriveTime() {
        return actualArriveTime;
    }

    public void setActualArriveTime(Date actualArriveTime) {
        this.actualArriveTime = actualArriveTime;
    }
}
