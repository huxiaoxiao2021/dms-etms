package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/**
 * 区域批次目的地请求
 * <p>
 * Created by lixin39 on 2016/12/9.
 */
public class AreaDestRequest extends JdRequest {

    private static final long serialVersionUID = 8954054127159816536L;

    /**
     * 方案编号
     */
    private Integer planId;

    /**
     * 线路类型
     */
    private Integer routeType;

    /**
     * 始发分拣中心名称
     */
    private String createSiteName;

    /**
     * 始发分拣中心编号
     */
    private Integer createSiteCode;

    /**
     * 中转分拣中心编号
     */
    private Integer transferSiteCode;

    /**
     * 中转分拣中心名称
     */
    private String transferSiteName;

    /**
     * 批次目的地编号
     */
    private Integer receiveSiteCode;

    /**
     * 批次目的地名称
     */
    private String receiveSiteName;

    /**
     * 批次目的地的编号和名称
     */
    private List<String> receiveSiteCodeName;

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getTransferSiteCode() {
        return transferSiteCode;
    }

    public void setTransferSiteCode(Integer transferSiteCode) {
        this.transferSiteCode = transferSiteCode;
    }

    public String getTransferSiteName() {
        return transferSiteName;
    }

    public void setTransferSiteName(String transferSiteName) {
        this.transferSiteName = transferSiteName;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public List<String> getReceiveSiteCodeName() {
        return receiveSiteCodeName;
    }

    public void setReceiveSiteCodeName(List<String> receiveSiteCodeName) {
        this.receiveSiteCodeName = receiveSiteCodeName;
    }

    @Override
    public String toString() {
        return "AreaDestRequest{" +
                "planId=" + planId +
                ", routeType=" + routeType +
                ", createSiteName='" + createSiteName + '\'' +
                ", createSiteCode=" + createSiteCode +
                ", transferSiteCode=" + transferSiteCode +
                ", transferSiteName='" + transferSiteName + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                ", receiveSiteCodeName=" + receiveSiteCodeName +
                '}';
    }
}
