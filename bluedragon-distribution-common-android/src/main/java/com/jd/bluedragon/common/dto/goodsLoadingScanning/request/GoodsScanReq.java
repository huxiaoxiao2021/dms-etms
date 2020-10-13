package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

public class GoodsScanReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 板号
     */
    private String boardCode;

}
