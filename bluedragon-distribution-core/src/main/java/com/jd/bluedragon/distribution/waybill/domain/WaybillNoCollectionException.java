package com.jd.bluedragon.distribution.waybill.domain;

public class WaybillNoCollectionException extends Exception {

    //无参构造方法
    public WaybillNoCollectionException(){

        super();
    }

    //有参的构造方法
    public WaybillNoCollectionException(String message){
        super(message);

    }

    // 用指定的详细信息和原因构造一个新的异常
    public WaybillNoCollectionException(String message, Throwable cause){

        super(message,cause);
    }

    //用指定原因构造一个新的异常
    public WaybillNoCollectionException(Throwable cause) {

        super(cause);
    }
}
