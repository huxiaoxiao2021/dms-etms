package com.jd.bluedragon.core.jsf.collectpackage.dto;

import lombok.Data;

@Data
public class UdataTaskFlowStatisticDto {
    private String bizId;
    private String boxCode;
    private Integer siteCode;
    private Integer endSiteId;
    private String endSiteName;
    private Integer interceptNum;
    private Integer forceNum;
    private Integer scannedNum;
    private Integer totalNum;
}
