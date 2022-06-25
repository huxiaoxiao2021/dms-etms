package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class CancelSendTaskResp implements Serializable {
    private static final long serialVersionUID = 3813878446640603317L;
    //流向目的站点名称
    private String endSiteName;
    //取消的包裹数量
    private Integer canclePackageCount;

    /**
     * 取消的容器维度编号 按包裹 就是包裹号,按运单 就是运单号 ,按箱就是箱号，按板就是板号
     */
    private String cancelCode;

    public String getCancelCode() {
        return cancelCode;
    }

    public void setCancelCode(String cancelCode) {
        this.cancelCode = cancelCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Integer getCanclePackageCount() {
        return canclePackageCount;
    }

    public void setCanclePackageCount(Integer canclePackageCount) {
        this.canclePackageCount = canclePackageCount;
    }

    @Override
    public String toString() {
        return "CancelSendTaskResp{" +
                "endSiteName='" + endSiteName + '\'' +
                ", canclePackageCount=" + canclePackageCount +
                ", cancelCode='" + cancelCode + '\'' +
                '}';
    }
}
