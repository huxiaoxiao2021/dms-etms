package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class SealVehicleInfoReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -548031614453404018L;
    //主任务编号
    private String sendVehicleBizId;
    //子任务编号
    private String sendVehicleDetailBizId;
    //车牌号
    private String vehicleNumber;
}
