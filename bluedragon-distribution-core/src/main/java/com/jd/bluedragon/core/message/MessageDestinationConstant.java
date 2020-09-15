package com.jd.bluedragon.core.message;


public enum MessageDestinationConstant {

	ReverseReceive("bd_dms_reverse_receive", "逆向收货消息"), 
	PopAbnormal("dms_pop_abnormal_order", "POP差异包裹审核结果处理分发地址"),
	LossOrder("ldms_to_bluedragon", "逆向报损订单"),
	FxmAbnormal("FXMDmsAoFB","配送外呼回传"),
	PickWare("sph_reverse_1","备件库取件单交接拆包"),
	DmsRouter("dms_router","DMS同步外部系统"),
	PopPickup("dms_pop_pickup","PDA推送POP收货消息"),
    ReceiveToArteryMQ("receive_artery_info", "收货发送给财务计费系统运输信息"),
    QualityControlMQ("bd_exception_to_qc","异常页面、备件库分拣发送给质控MQ"),
    QualityControlFXMMQ("bd_dms_abnormal_order_to_qc","配送外呼发送给质控MQ"),
	//FIXME:已经失效
	@Deprecated
    SendDetailMQ("dmsWorkSendDetail", "发货明细消息"),
	//根据source区分类型，未来可能代替dmsWorkSendDetail
    NewSendDetailMQ("dms_send_detail", "新发货明细消息");
    private String name;

    private String desc;

    private MessageDestinationConstant() {
    }

    private MessageDestinationConstant(String name, String desc) {
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
        System.out.println(MessageDestinationConstant.ReverseReceive.name);
        System.out.println(MessageDestinationConstant.ReverseReceive.name());
        System.out.println(MessageDestinationConstant.ReverseReceive.getName());
        System.out.println(MessageDestinationConstant.QualityControlMQ.getName());
        System.out.println(MessageDestinationConstant.ReceiveToArteryMQ.getName());
        System.out.println(MessageDestinationConstant.QualityControlFXMMQ.getName());
    }
}
