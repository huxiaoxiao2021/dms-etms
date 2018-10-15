package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author shipeilin
 * @Description: B网包装耗材，发送运单MQ消息体
 * @date 2018年08月22日 10时:31分
 */
public class WaybillConsumableDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 消息类型（1:终端揽收,2:分拣打包,3:外单计算）
     */
    private Integer messageType;

    /**
     * 分拣中心编码
     */
    private Integer dmsCode;

    /**
     * 操作人erp
     */
    private String operateUserErp;

    /**
     * 操作人名字
     */
    private String operateUserName;

    /**
     * 操作时间  yyyy-MM-dd HH:mm:ss
     */
    private String operateTime;

    /**
     * 耗材明细
     */
    private List<WaybillConsumableDetailDto> packingChargeList;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(Integer dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public List<WaybillConsumableDetailDto> getPackingChargeList() {
        return packingChargeList;
    }

    public void setPackingChargeList(List<WaybillConsumableDetailDto> packingChargeList) {
        this.packingChargeList = packingChargeList;
    }
}
