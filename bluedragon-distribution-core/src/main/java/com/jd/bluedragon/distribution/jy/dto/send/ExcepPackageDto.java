package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.util.List;
@Data
public class ExcepPackageDto {
    private List<SendPackageDto> sendPackageDtoList;
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
}
