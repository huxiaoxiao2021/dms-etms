package com.jd.bluedragon.distribution.log;

public enum BizOperateTypeConstants {
    DELIVERY_DELIVERY(100, 1009),//发货，发货
    SORTING_SORTING(700, 60016),//分拣，分拣
    DELIVERY_CANCEL_DELIVERY(100, 1010),//发货，取消发货
    OUTER_WAYBILL_EXCHANGE_TRIGGER_OUTER_WAYBILL_EXCHANGE(1900, 1900002),//外单换单,触发外单换单
    TASK_CONSUME_FAIL(2006, 20062),//TASK,消费失败落库
    TASK_REDIS_TASK(2006, 20063),//TASK,Redis任务数据处理
    SORTING_PRE_SORTING_SITE_CHANGE(700, 60014),//分拣,预分拣站点变更
    SORTING_REVERSE_SORTING_100SCORE(700, 60013),//分拣,回传退款100分逆向分拣
    BOARDCOMBINATION_BOARDCOMBINATION(2005, 20053),//组板,组板
    BOARDCOMBINATION_DEBOARDCOMBINATION(2005, 20054),//组板,取消组板
    WEIGH_WAYBILL_VALIDWAYBILL(1901, 1901003),//快运称重，当运单经校验存在时
    WEIGH_WAYBILL_INVALIDWAYBILL(1901, 1901004),//快运称重，当运单经校验不存在时
    WEIGH_WAYBILL_OPERATEEXCEPTION(1901, 1901001),//快运称重，记录操作人引起的异常
    WEIGH_WAYBILL_WEIGHTINFOTOWAYBILL(1901, 1901002),//快运称重，向运单回传包裹称重信息
    BACKUP_STORAGE_CHUGUAN(2019, 20091),//备件库,推出管
    DELIVERY_SENDREVERSE(100, 1008),//发货,逆向发货
    SEAL_SEAL(1011, 1011),//封车，封车
    SEAL_FERRY_SEAL(1011, 1014),//封车，传摆封车
    SEAL_OFFLINE_SEAL(1011, 101104),//封车，离线封车
    DE_SEAL_DE_SEAL(1012, 1012),//解封车,解封车
    DELIVERY_ONECARDELIVERY(100, 1001),//发货,一车一单发货
    RETURNS_REFUND100(2007, 20073),//退货,退款100分
    CAR_CAR_IN(2009, 20091),//车辆，进出记录进
    CAR_CAR_OUT(2009, 20092),//车辆，进出记录出
    CAR_DEPARTURE(2009, 20093),//车辆,发车
    SORTING_BOXCACHECLEAR(700, 60015),//分拣，缓存箱号清理
    DELIVERY_REVERSE_DELIVERY(100, 1008),//发货，逆向发货
    TRANSFER_TRANSFER(2010, 20101),//交接,交接
    RECEIVE_RECEIVE(2008, 20081),//收货，收货
    RECEIVE_REVERSE_AMS_RECEIVE(2008, 20085),//收货,逆向售后收货
    RECEIVE_REVERSE_WMS_RECEIVE(2008, 20081),//收货,逆向仓储收货
    INSPECT_INSPECT(500, 5001),//验货,验货
    INSPECT_BACKUP_STORAGE_REVERSE_INSPECT(500, 5002),//验货，逆向备件库驳回验货
    INSPECT_REVERSE_WMS_REJECT_INSPECT(500, 5003),//验货,逆向仓储驳回验货
    PRINT_PRINT(2001, 20011),//打印，打印
    SORTING_CANCEL_SORTING(700, 60017),//分拣,取消分拣
    EXCEPTIONS_DATA_EXCEPTIONS(2011, 20111),//异常,数据异常
    PRINT_PACKAGE_LABEL_PRINT(2001, 20112),//打印,包裹标签打印
    RECEIVE_REVERSE_RECEIPT(2008, 20082),//收货,逆向收货
    RECEIVE_BACKUP_STORAGE_REVERSE_RECEIPT(2008, 20083),//收货,逆向备件库收货
    WAREHOUSING_REVERSE_WAREHOUSING_REJECT(2012, 20121),//仓储,逆向仓储驳回
    BACKUP_STORAGE_REVERSE_SPWMS_REJECT(2019, 20092),//仓储,逆向仓储驳回
    SORTING_SORTING_INTERCEPT(700, 60018),//分拣,分拣拦截
    INSPECT_SALE_REVERSE_INSPECT_REJECT(500, 5004),//验货,逆向售后驳回验货
    LOAD_CAN_GLOBAL(2013, 20131),//装载,取消预装载
    DELIVERY_PARTNER_WAY_BILL(100, 1011),//发货，运单号关联包裹信息
    RETURNS_FASTREFUND(2007, 20074),//退款,先货先款退款
    AFTERSALE_REVERSE_AFTERSALE_REJECT(2014, 20141),//售后,逆向售后驳回
    INSPECT_SORTING_CENTER_INSPECT(500, 50012),//验货,分拣中心验货
    SORTING_OFFLINE_SORTING(700,60011),//分拣,离线分拣
    SORTING_SORTING_SEAL(700,60012),//分拣，分拣封箱
    RETURNS_OVER_AREA_RETURN(2007,20071),//退货,三方超区退货
    RECEIVE_POP_RECEIVE(2008,20086);//收货,pop上门接货

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
