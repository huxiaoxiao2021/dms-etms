package com.jd.bluedragon.distribution.api.request.driver;

/**
 * 校验批次参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-15 21:00:42 周一
 */
public class DriverBoardSendCheckBatchCodeRequest extends BaseDriverInfoRequest {

    private static final long serialVersionUID = -4561964690651082189L;

    /**
     * 批次号
     */
    private String batchCode;

    public DriverBoardSendCheckBatchCodeRequest() {
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
}
