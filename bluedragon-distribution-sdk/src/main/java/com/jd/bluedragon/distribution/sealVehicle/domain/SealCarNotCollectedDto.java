package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time
 */
public class SealCarNotCollectedDto implements Serializable {

    private static final long serialVersionUID = -5424540721703078519L;

    /**
     * 分拣中心ID
     */
    private Long siteId;

    /**
     * 封车号
     */
    private String sealCarCode;

    /**
     * 封车车牌号
     */
    private String vehicleNumber;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 系统包裹总数
     */
    private Integer packageNumSys;

    /**
     * 封车包裹数
     */
    private Integer packageNumSeal;
}
