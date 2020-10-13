package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

public class GoodsDetailReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 包裹号集合（运单下面总包裹数，默认10个包裹的包裹号）
     */
    private String packageCodes;

    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 总包裹数量
     */
    private Integer packageAmount;

    /**
     * 库存数量
     */
    private Integer goodsAmount;

    /**
     * 已装车数量
     */
    private Integer loadAmount;

    /**
     * 未装车数量
     */
    private Integer unloadAmount;

    /**
     * 强制下发数量
     */
    private Integer forceAmount;


}
