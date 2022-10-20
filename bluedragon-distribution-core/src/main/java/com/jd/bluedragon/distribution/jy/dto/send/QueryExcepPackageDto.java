package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum;
import lombok.Data;

@Data
public class QueryExcepPackageDto {

    private Integer pageNo;
    private Integer pageSize;
    private ExcepScanTypeEnum excepScanTypeEnum;
    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;
    private String waybillCode;
    private Integer operateSiteId;
}
