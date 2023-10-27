package com.jd.bluedragon.core.jsf.collectpackage.dto;

import lombok.Data;

@Data
public class UdataTaskFlowDetailDto {
    private String bizId;
    private String boxCode;
    private Integer siteCode;
    private Integer endSiteId;
    private String endSiteName;
    private String packageCode;
}
