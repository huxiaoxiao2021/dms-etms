package com.jd.bluedragon.distribution.log;

public class OperateTypeConstants {

    public static final int TRIGGER_OUTER_WAYBILL_EXCHANGE = 1900002;//触发外单换单
    public static final int CONSUME_FAIL = 20062;//消费失败落库
    public static final int REDIS_TASK = 20063;//Redis任务数据处理
    public static final int PRE_SORTING_SITE_CHANGE = 60014;//预分拣站点变更
    public static final int REVERSE_SORTING_100SCORE = 60013;//回传退款100分逆向分拣
    public static final int BOARDCOMBINATION = 20053;//组板
    public static final int DEBOARDCOMBINATION = 20054;//取消组板
    public static final int VALIDWAYBILL = 1901003;//快运称重，当运单经校验存在时
    public static final int INVALIDWAYBILL = 1901004;//快运称重，当运单经校验不存在时
    public static final int OPERATEEXCEPTION = 1901001;//快运称重，记录操作人引起的异常
    public static final int WEIGHTINFOTOWAYBILL = 1901002;//快运称重，向运单回传包裹称重信息
    public static final int CHUGUAN = 20091;//推出管
    public static final int SENDREVERSE = 1008;//逆向发货
    public static final int SEAL = 1011;//封车
    public static final int FERRY_SEAL = 1014;//传摆封车
    public static final int OFFLINE_SEAL = 101104;//离线封车
    public static final int DE_SEAL = 1012;//解封车
    public static final int ONECARDELIVERY = 1001;//一车一单发货
    public static final int REFUND100 = 20073;//退款100分
    public static final int CAR_IN = 20091;//车辆进出记录，进
    public static final int CAR_OUT = 20092;//车辆进出记录，出
    public static final int DEPARTURE = 20093;//发车
    public static final int BOXCACHECLEAR = 60015;//缓存箱号清理
    public static final int REVERSE_DELIVERY = 1008;//逆向发货
    public static final int TRANSFER = 20101;//交接
    public static final int RECEIVE = 20081;//收货
    public static final int REVERSE_AMS_RECEIVE = 20085;//逆向售后收货
    public static final int REVERSE_WMS_RECEIVE = 20081;//逆向仓储收货
    public static final int INSPECT = 5001;//验货
    public static final int BACKUP_STORAGE_REVERSE_INSPECT = 5002;//逆向备件库驳回验货
    public static final int REVERSE_WMS_REJECT_INSPECT = 5003;//逆向仓储驳回验货
    public static final int PRINT = 20011;//打印
    public static final int SORTING = 60016;//分拣
    public static final int CANCEL_SORTING = 60017;//取消分拣
    public static final int DELIVERY = 1009;//发货
    public static final int CANCEL_DELIVERY = 1010;//取消发货
    public static final int DATA_EXCEPTIONS = 20111;//数据异常
    public static final int PACKAGE_LABEL_PRINT = 20112;//包裹标签打印
    public static final int REVERSE_RECEIPT = 20082;//逆向收货
    public static final int BACKUP_STORAGE_REVERSE_RECEIPT = 20083;//逆向备件库收货
    public static final int REVERSE_WAREHOUSING_REJECT = 20121;//逆向仓储驳回
    public static final int REVERSE_SPWMS_REJECT = 20092;//逆向仓储驳回
    public static final int SORTING_INTERCEPT = 60018;//分拣拦截
    public static final int SALE_REVERSE_INSPECT_REJECT = 5004;//逆向售后驳回验货
    public static final int CAN_GLOBAL = 20131;//取消预装载
    public static final int PARTNER_WAY_BILL = 1011;//运单号关联包裹信息
    public static final int FASTREFUND = 20074;//先货先款退款
    public static final int REVERSE_AFTERSALE_REJECT = 20141;//逆向售后驳回
    public static final int SORTING_CENTER_INSPECT = 50012;//分拣中心验货
    public static final int OFFLINE_SORTING = 60011;//离线分拣
    public static final int SORTING_SEAL = 60012;//分拣封箱
    public static final int OVER_AREA_RETURN = 20071;//三方超区退货
    public static final int POP_RECEIVE = 20086;//pop上门接货

}
