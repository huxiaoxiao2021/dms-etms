package com.jd.bluedragon.distribution.api.request.driver;

/**
 * 组板参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-15 21:00:42 周一
 */
public class DriverBoardCancelSendRequest extends BaseDriverInfoRequest {

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
     * 是否取消上次发货，false - 不取消（默认），true - 取消上次发货
     */
    private Boolean isCancelLastSend;

    /**
     * 是否取消整个板
     */
    private Integer cancelWholeBoard;

    public DriverBoardCancelSendRequest() {
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public DriverBoardCancelSendRequest setBusinessType(Integer businessType) {
        this.businessType = businessType;
        return this;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public DriverBoardCancelSendRequest setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public DriverBoardCancelSendRequest setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public DriverBoardCancelSendRequest setBatchCode(String batchCode) {
        this.batchCode = batchCode;
        return this;
    }

    public Boolean getCancelLastSend() {
        return isCancelLastSend;
    }

    public DriverBoardCancelSendRequest setCancelLastSend(Boolean cancelLastSend) {
        isCancelLastSend = cancelLastSend;
        return this;
    }

    public Integer getCancelWholeBoard() {
        return cancelWholeBoard;
    }

    public DriverBoardCancelSendRequest setCancelWholeBoard(Integer cancelWholeBoard) {
        this.cancelWholeBoard = cancelWholeBoard;
        return this;
    }
}
