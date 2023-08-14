package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/14 20:54
 * @Description
 */
public class AviationSealListDto implements Serializable {

    private static final long serialVersionUID = -5005890642092421853L;

    private String bizId;
    /**
     * 订舱号
     */
    private String bookingCode;
    /**
     * 航班号
     */
    private String flightNumber;
    /**
     * 货物类型
     * CargoTypeEnum
     */
    private Integer cargoType;
    /**
     * 航空类型
     * AirTypeEnum
     */
    private Integer airType;
    /**
     * 目的分拣中心
     */
    private Integer nextSiteId;
    private String nextSiteName;

    private Double loadWeight;
    private Double loadVolume;
    private Double loadItemNum;
    /**
     * 标准发车时间
     */
    private Long departureTime;
    private String departureTimeStr;
    /**
     * 运力编码
     */
    private String transportCode;

}
