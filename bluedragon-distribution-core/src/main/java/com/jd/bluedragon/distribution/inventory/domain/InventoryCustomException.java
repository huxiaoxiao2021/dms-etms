package com.jd.bluedragon.distribution.inventory.domain;

public class InventoryCustomException extends Exception {

    //无参构造方法
    public InventoryCustomException(){

        super();
    }

    //有参的构造方法
    public InventoryCustomException(String message){
        super(message);

    }

    // 用指定的详细信息和原因构造一个新的异常
    public InventoryCustomException(String message, Throwable cause){

        super(message,cause);
    }

    //用指定原因构造一个新的异常
    public InventoryCustomException(Throwable cause) {

        super(cause);
    }
}
