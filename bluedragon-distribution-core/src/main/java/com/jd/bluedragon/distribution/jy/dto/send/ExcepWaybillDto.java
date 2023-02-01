package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.util.List;
@Data
public class ExcepWaybillDto {
    private List<SendWaybillDto> sendWaybillDtoList;
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
}
