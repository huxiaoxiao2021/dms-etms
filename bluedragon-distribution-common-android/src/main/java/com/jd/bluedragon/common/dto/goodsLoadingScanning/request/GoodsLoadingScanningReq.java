package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

public class GoodsLoadingScanningReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 包裹号转板号标识updateUnloadCarTaskHelpers
     */
    private Integer transfer;

    /**
     * 包裹明细
     */
    private List<GoodsDetailReq> packageDetails;

    /**
     * 是否多扫的标识：1标识多扫 0标识不是多扫
     */
    private Integer flowDisaccord;

    /**
     * 当前操作用户
     */
    private User user;

    /**
     * 当前网点ID
     */
    private Integer createSiteCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getWayBillCode() {
        return wayBillCode;
    }

    public void setWayBillCode(String wayBillCode) {
        this.wayBillCode = wayBillCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public List<GoodsDetailReq> getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(List<GoodsDetailReq> packageDetails) {
        this.packageDetails = packageDetails;
    }

    public Integer getTransfer() {
        return transfer;
    }

    public void setTransfer(Integer transfer) {
        this.transfer = transfer;
    }

    public Integer getFlowDisaccord() {
        return flowDisaccord;
    }

    public void setFlowDisaccord(Integer flowDisaccord) {
        this.flowDisaccord = flowDisaccord;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
}
