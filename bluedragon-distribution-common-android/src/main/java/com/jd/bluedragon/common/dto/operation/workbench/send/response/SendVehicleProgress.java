package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName SendVehicleProgress
 * @Description
 * @Author wyh
 * @Date 2022/5/19 16:16
 **/
public class SendVehicleProgress implements Serializable {

    private static final long serialVersionUID = -7310020588075926827L;

    /**
     * 装载率
     */
    private BigDecimal loadRate;

    /**
     * 车型体积
     */
    private BigDecimal volume;

    /**
     * 车型重量
     */
    private BigDecimal weight;

    /**
     * 装载体积
     */
    private BigDecimal loadVolume;

    /**
     * 装载重量
     */
    private BigDecimal loadWeight;

    /**
     * 待扫包裹数
     */
    private Long toScanCount;

    /**
     * 已扫包裹数
     */
    private Long scannedPackCount;

    /**
     * 已扫箱数
     */
    private Long scannedBoxCount;

    /**
     * 拦截包裹数
     */
    private Long interceptedPackCount;

    /**
     * 强制发包裹数
     */
    private Long forceSendPackCount;
}
