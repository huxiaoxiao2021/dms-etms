package com.jd.bluedragon.distribution.jy.service.collectNew.enums;

public enum JyCollectionMqBizSourceEnum {

    PRODUCE_NODE_PDA_SCAN("pda_scan", "实操扫描"),
    PRODUCE_NODE_MQ_WAYBILL_SPLIT("mq_waybill_split", "集齐消息消费时按运单拆分后发送包裹维度消息"),
    PRODUCE_NODE_MQ_BOX_SPLIT("mq_box_split", "集齐消息消费时按箱号拆分后发送包裹维度消息"),
    PRODUCE_NODE_MQ_BOARD_SPLIT("mq_board_split", "集齐消息消费时按板号拆分后发送包裹维度消息"),
    ;
    private String code;
    private String desc;

    JyCollectionMqBizSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
