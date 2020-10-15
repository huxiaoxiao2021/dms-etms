package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.util.Date;

/**
 * 装货完成发货请求入参
 */
public class GoodsLoadingReq {
    private static final long serialVersionUID = 1L;

    /**
     * 业务来源
     */
    private Integer bizSource;

    /**
     * 任务号
     */
    private String taskId;

    /**
     * 操作人
     */
    private String createUser;

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 发货交接单类型(1 退货 2 转站 3 第三方)
     */
    private Integer sendType;

    /**
     * 发货单位编码
     */
    private Integer createSiteCode;

    /**
     * 发货交接单号-发货批次号
     */
    private String sendCode;

    /**
     * 收货单位编码
     */
    private Integer receiveSiteCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 是否删除 '0' 删除 '1' 使用
     */
    private Integer yn;
}
