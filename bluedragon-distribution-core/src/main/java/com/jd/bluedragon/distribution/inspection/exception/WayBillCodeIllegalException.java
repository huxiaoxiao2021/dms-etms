package com.jd.bluedragon.distribution.inspection.exception;

/**
 * 运单号不符合正则规则异常
 * Created by shipeilin on 2017/8/7.
 */
public class WayBillCodeIllegalException extends RuntimeException{
    private static final long serialVersionUID = -1086891229148586224L;

    public WayBillCodeIllegalException(String message) {
        super(message);
    }
}
