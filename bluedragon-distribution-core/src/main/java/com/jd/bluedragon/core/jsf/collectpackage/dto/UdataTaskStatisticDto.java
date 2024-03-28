package com.jd.bluedragon.core.jsf.collectpackage.dto;

import lombok.Data;

@Data
public class UdataTaskStatisticDto {
    private String bizId;
    private String boxCode;
    private Integer siteCode;
    private Integer endSiteId;
    private String endSiteName;
    private Integer interceptNum;
    private Integer forceNum;
    private Integer scannedNum;
    private Integer totalNum;
    private Integer interceptPackageNum;
    private Integer interceptBoxNum;
    private Integer forcePackageNum;
    private Integer forceBoxNum;
    private Integer scannedPackageNum;
    private Integer scannedBoxNum;


}
