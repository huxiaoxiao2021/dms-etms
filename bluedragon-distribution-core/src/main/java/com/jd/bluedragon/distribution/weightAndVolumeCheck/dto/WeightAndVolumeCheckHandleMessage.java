package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

import java.io.Serializable;

/**
 * 称重抽检处理消息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-08-24 20:08:56 周一
 */
public class WeightAndVolumeCheckHandleMessage implements Serializable {

    // 发货
    public static final int SEND = 9;
    // 图片上传
    public static final int UPLOAD_IMG = 7;
    // 图片早于抽检数据
    public static final int IMG_BEFORE_DATA = 8;

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

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOpNode() {
        return opNode;
    }

    public void setOpNode(Integer opNode) {
        this.opNode = opNode;
    }
}
