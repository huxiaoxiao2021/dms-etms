package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum;
import lombok.Data;

import java.util.List;

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
    //发货下游场地
    private List<Integer> receiveSiteIdList;
}
