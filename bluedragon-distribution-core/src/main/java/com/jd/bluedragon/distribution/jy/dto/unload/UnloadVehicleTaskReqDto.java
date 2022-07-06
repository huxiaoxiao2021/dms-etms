package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import lombok.Data;
import java.io.Serializable;

@Data
public class UnloadVehicleTaskReqDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 8483802798838866603L;

    private Integer pageNo;
    private Integer PageSize;
    private Integer vehicleStatus;
    private Integer groupId;

}
