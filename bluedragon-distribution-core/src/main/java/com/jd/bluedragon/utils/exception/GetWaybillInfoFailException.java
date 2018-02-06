package com.jd.bluedragon.utils.exception;

/**
 * 自定义获取运单信息失败异常
 *
 * <p>
 * Created by lixin39 on 2018/1/31.
 */
public class GetWaybillInfoFailException extends RuntimeException {
    public GetWaybillInfoFailException(String message) {
        super(message);
    }
}
