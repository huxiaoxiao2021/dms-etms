package com.jd.bluedragon.core.exception;

/**
 * @ClassName SealVehicleTaskBusinessException
 * @Description
 * @Author wyh
 * @Date 2022/3/9 20:33
 **/
public class SealVehicleTaskBusinessException extends RuntimeException{

    private static final long serialVersionUID = -1736026953480217332L;

    public SealVehicleTaskBusinessException(String message) {
        super(message);
    }

    public SealVehicleTaskBusinessException() {
        super();
    }

    public SealVehicleTaskBusinessException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
