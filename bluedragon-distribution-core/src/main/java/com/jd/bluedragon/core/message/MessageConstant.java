package com.jd.bluedragon.core.message;

public enum MessageConstant {
    
    ReverseSend("REVERSE_SEND_", "逆向发货回传仓储|售后|备件库系统"),
    ReversePop("REVERSE_POP_", "逆向收货回传POP系统"),
    OrderPacke("ORDER_PACKAGE_", "仓储包裹数据回传运单系统");
    
    private String name;
    
    private String desc;
    
    private MessageConstant() {
    }
    
    private MessageConstant(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public static final void main(String[] args) {
        System.out.println(MessageConstant.ReverseSend.name);
    }
}
