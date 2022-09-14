package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

@Data
public class SendWaybillDto {
    private String waybillCode;
    private Integer packageCount;
    private Integer totalCount;
}
