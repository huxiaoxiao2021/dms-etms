package com.jd.bluedragon.distribution.jy.dto.send;

/**
 * @ClassName SendVehicleD
 * @Description
 * @Author wyh
 * @Date 2022/5/18 18:22
 **/
public class SendVehicleDetail extends BaseSendVehicleDetail {

    private static final long serialVersionUID = -800277887131111L;

    /**
     * 状态
     */
    private Integer itemStatus;

    /**
     * 状态描述
     */
    private String itemStatusDesc;

    public Integer getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Integer itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemStatusDesc() {
        return itemStatusDesc;
    }

    public void setItemStatusDesc(String itemStatusDesc) {
        this.itemStatusDesc = itemStatusDesc;
    }
}
