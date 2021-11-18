package com.jd.bluedragon.distribution.api.request.driver;

/**
 * 获取批次信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-17 15:34:04 周三
 */
public class DriverBoardCancelSendGetBatchInfoRequest extends BaseDriverInfoRequest {

    private static final long serialVersionUID = 563094193503142670L;

    /**
     * 批次号
     */
    private String batchCode;

    public DriverBoardCancelSendGetBatchInfoRequest() {
    }

    public String getBatchCode() {
        return batchCode;
    }

    public DriverBoardCancelSendGetBatchInfoRequest setBatchCode(String batchCode) {
        this.batchCode = batchCode;
        return this;
    }
}
