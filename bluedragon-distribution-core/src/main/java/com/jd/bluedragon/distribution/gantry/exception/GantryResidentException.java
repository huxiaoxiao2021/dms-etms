package com.jd.bluedragon.distribution.gantry.exception;

/**
 * 龙门架驻厂自定义异常
 *
 * @author: hujiping
 * @date: 2020/10/23 14:21
 */
public class GantryResidentException extends Exception{

    public static final String WAYBILL_NOT_EXIST = "运单【%s】没有数据，重试3次后没有则放弃处理!";

    public GantryResidentException(String message) {
        super(message);
    }
}
