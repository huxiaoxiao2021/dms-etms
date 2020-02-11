package com.jd.bluedragon.distribution.log;

public enum BizOperateTypeConstants {
    DELIVERY_DELIVERY(100,1009),//发货，发货
    SORTING_SORTING(700,60016),//分拣，分拣
    DELIVERY_CANCEL_DELIVERY(100,1010),//发货，取消发货
    OUTER_WAYBILL_EXCHANGE_TRIGGER_OUTER_WAYBILL_EXCHANGE(1900,1900002),//外单换单,触发外单换单
    TASK_CONSUME_FAIL(2006,20062),//TASK,消费失败落库
    TASK_REDIS_TASK(2006,20063),//TASK,Redis任务数据处理
    SORTING_PRE_SORTING_SITE_CHANGE(700,60014)//分拣,预分拣站点变更

    ;

    private int BizTypeCode;
    private int OperateTypeCode;

    BizOperateTypeConstants(int bizTypeCode, int operateTypeCode) {
        BizTypeCode = bizTypeCode;
        OperateTypeCode = operateTypeCode;
    }

    public int getBizTypeCode() {
        return BizTypeCode;
    }

    public void setBizTypeCode(int bizTypeCode) {
        BizTypeCode = bizTypeCode;
    }

    public int getOperateTypeCode() {
        return OperateTypeCode;
    }

    public void setOperateTypeCode(int operateTypeCode) {
        OperateTypeCode = operateTypeCode;
    }
}
