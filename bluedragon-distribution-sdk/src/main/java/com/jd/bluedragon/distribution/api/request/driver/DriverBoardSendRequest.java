package com.jd.bluedragon.distribution.api.request.driver;

import com.jd.bluedragon.distribution.api.request.base.BaseRequest;

/**
 * 组板参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-15 21:00:42 周一
 */
public class DriverBoardSendRequest extends BaseDriverInfoRequest {

    private static final long serialVersionUID = 2291305968739862890L;

    /** 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP */
    private Integer businessType;

    /**
     * 收货场地编号
     */
    private Integer receiveSiteCode;

    /**
     * 扫描主单据号
     */
    private String barCode;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 是否强制发货
     */
    private boolean isForceSend;

    /**
     * 是否取消上次发货，false - 不取消（默认），true - 取消上次发货
     */
    private Boolean isCancelLastSend;

    /**
     * 是否发送整板
     */
    private Integer sendForWholeBoard;

    public DriverBoardSendRequest() {
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public DriverBoardSendRequest setBusinessType(Integer businessType) {
        this.businessType = businessType;
        return this;
    }

    public boolean isForceSend() {
        return isForceSend;
    }

    public DriverBoardSendRequest setForceSend(boolean forceSend) {
        isForceSend = forceSend;
        return this;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public DriverBoardSendRequest setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public DriverBoardSendRequest setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public DriverBoardSendRequest setBatchCode(String batchCode) {
        this.batchCode = batchCode;
        return this;
    }

    public boolean getIsForceSend() {
        return isForceSend;
    }

    public DriverBoardSendRequest setIsForceSend(boolean forceSend) {
        isForceSend = forceSend;
        return this;
    }

    public Boolean getCancelLastSend() {
        return isCancelLastSend;
    }

    public DriverBoardSendRequest setCancelLastSend(Boolean cancelLastSend) {
        isCancelLastSend = cancelLastSend;
        return this;
    }

    public Integer getSendForWholeBoard() {
        return sendForWholeBoard;
    }

    public DriverBoardSendRequest setSendForWholeBoard(Integer sendForWholeBoard) {
        this.sendForWholeBoard = sendForWholeBoard;
        return this;
    }
}
