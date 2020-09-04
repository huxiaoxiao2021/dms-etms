package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 称重抽检处理消息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-08-24 20:08:56 周一
 */
@Data
public class WeightAndVolumeCheckHandleMessage implements Serializable {

    public static final int SEND = 9;

    public static final int UPLOAD_IMG = 7;

    private static final long serialVersionUID = -3133640093526743930L;

    /**
     * 分拣中心
     */
    private Integer siteCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 操作环节
     */
    private Integer opNode;
}
