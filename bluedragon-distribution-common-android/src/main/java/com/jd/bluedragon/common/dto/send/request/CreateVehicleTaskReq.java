package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CreateVehicleTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -5531009617431175788L;

    /**
     * 车型
     */
    private Integer vehicleType;
    /**
     * 车型名称
     */
    private String vehicleTypeName;



}
