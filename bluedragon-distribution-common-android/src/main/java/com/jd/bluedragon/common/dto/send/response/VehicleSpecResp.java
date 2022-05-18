package com.jd.bluedragon.common.dto.send.response;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class VehicleSpecResp implements Serializable {
    private static final long serialVersionUID = -7846057809543529300L;
    /**
     * 规格信息
     */
    private String spec;
    /**
     * 某规格下的车辆类型列表信息
     */
    private List<VehicleTypeDto> vehicleTypeDtoList;
}
