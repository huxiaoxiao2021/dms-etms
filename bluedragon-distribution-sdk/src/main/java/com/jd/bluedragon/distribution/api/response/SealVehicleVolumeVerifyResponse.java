package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author lijie
 * @date 2020/1/9 10:54
 */
public class SealVehicleVolumeVerifyResponse<T> extends JdResponse{

    private static final long serialVersionUID = -9096768337471427500L;
    
    public static final Integer CODE_OK = 1;
    public static final String MESSAGE_OK = "成功";

    public static final Integer CODE_FAIL = -1;
    public static final String MESSAGE_FAIL = "失败";

    public static final Integer CODE_SERVICE_ERROR = 0;
    public static final String MESSAGE_SERVICE_ERROR = "异常";

    public static final Integer CODE_WARNING = 0;
    public static final String MESSAGE_WARNING = "警告";

    public SealVehicleVolumeVerifyResponse(){

    }

    public SealVehicleVolumeVerifyResponse(Integer code, String message){
        super(code, message);
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
