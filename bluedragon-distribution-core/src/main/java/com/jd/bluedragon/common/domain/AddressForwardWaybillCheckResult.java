package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * 改址转寄运单校验结果
 */
public class AddressForwardWaybillCheckResult implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 补打标识
     */
    private boolean rePrintFlag;

    /**
     * 换单打印标识
     */
    private boolean exchangePrintFlag;

    public boolean isRePrintFlag() {
        return rePrintFlag;
    }

    public void setRePrintFlag(boolean rePrintFlag) {
        this.rePrintFlag = rePrintFlag;
    }

    public boolean isExchangePrintFlag() {
        return exchangePrintFlag;
    }

    public void setExchangePrintFlag(boolean exchangePrintFlag) {
        this.exchangePrintFlag = exchangePrintFlag;
    }
}
