package com.jd.bluedragon.distribution.printOnline.domain;

import java.io.Serializable;
import java.util.List;

public class PrintOnlineModalDTO implements Serializable {

    private static final long serialVersionUID = 5826017370697724251L;

    //操作地名称
    private String createSiteName;
    //批次号
    private String sendCode;
    //目的地名称
    private String receiveSiteName;
    //发货时间
    private String sendTime;
    //发货箱列表
    private List<PrintOnlineBoxDTO> boxes;
    //发货运单维度列表
    private List<PrintOnlineWaybillDTO> waybills;
    //备注描述
    private String remark;

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public List<PrintOnlineBoxDTO> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<PrintOnlineBoxDTO> boxes) {
        this.boxes = boxes;
    }

    public List<PrintOnlineWaybillDTO> getWaybills() {
        return waybills;
    }

    public void setWaybills(List<PrintOnlineWaybillDTO> waybills) {
        this.waybills = waybills;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
