package com.jd.bluedragon.distribution.jy.dto.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ExpefResultNotify {
    /**
     * 三无码
     */
    private String barCode;

    /**
     * 匹配单号
     */
    private String packageCode;

    /**
     * 三无上报站点
     */
    private Integer siteCode;

    /**
     * 通知类型 0、匹配成功 1、处理完成
     */
    private Integer notifyType;

    /**
     * 通知时间
     */
    private Date notifyTime;

    /**
     * 通知人
     */
    private String notifyErp;
}
