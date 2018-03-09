package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class DeliveryResponse extends JdResponse {
    
    private static final long serialVersionUID = 6917841719620008189L;
    public static final Integer CODE_Delivery_SEND_SUCCESS = 1;
    public static final Integer CODE_Delivery_SEND_FAIL = 2;
    public static final Integer CODE_Delivery_SEND_CONFIRM = 4;

    public static final Integer CODE_VER_CHECK_EXCEPTION = 304;
    public static final String MESSAGE_VER_CHECK_EXCEPTION = "验证服务失败，请重试";

    public static final Integer CODE_Delivery_ERROR = 40001;
    public static final String MESSAGE_Delivery_ERROR = "发货处理异常";
    
    public static final Integer CODE_Delivery_NO_CHECK = 30001;
    public static final String MESSAGE_Delivery_NO_CHECK = "该箱号还没有验货，是否发货？";
    
    public static final Integer CODE_Delivery_ALL_CHECK = 40002;
    public static final String MESSAGE_Delivery_ALL_CHECK = "该箱号还没有完验";

    public static final Integer CODE_CITY_BILL_CHECK = 39002;
    public static final String MESSAGE_CITY_BILL_CHECK = "，是否强制发货？";

    public static final Integer CODE_Delivery_NO_MESAGE = 40003;
    public static final String MESSAGE_Delivery_NO_MESAGE = "无该箱号发货的记录";
    public static final String MESSAGE_Delivery_IS_MESAGE = "发货处理中请稍后再试";
    public static final String MESSAGE_Delivery_NO_PACKAGE= "无该包裹的记录";
    public static final String MESSAGE_Delivery_NO_REQUEST= "输入参数错误";
    public static final String MESSAGE_Delivery_NO_BATCH = "无该波次的发货明细";
    
    
    public static final Integer CODE_Delivery_NO_DEPART = 40004;
    public static final String MESSAGE_Delivery_NO_DEPART = "该箱号已经发车";
    
    public static final Integer CODE_Delivery_IS_SEND = 40005;
    public static final String MESSAGE_Delivery_IS_SEND = "该箱号已经发货";
    
    public static final Integer CODE_Delivery_IS_SITE = 40006;
    public static final String MESSAGE_Delivery_IS_SITE = "箱号与目的地不一致";
    
    public static final Integer CODE_Delivery_NO_SORTING = 30002;
    public static final String MESSAGE_Delivery_NO_SORTING = "包裹没有分拣记录，是否发货？";
    
    public static final Integer CODE_Delivery_THREE_SORTING = 30003;
    public static final String MESSAGE_Delivery_THREE_SORTING = "有不全运单，请处理后再发货";
    
    public static final Integer CODE_Delivery_LACK_ORDER = 30004;
    public static final String MESSAGE_Delivery_LACK_ORDER = "该箱有跨箱订单，不能取消发货";
    
    public static final Integer CODE_Delivery_SORTING_DIFF = 30005;
    public static final String MESSAGE_Delivery_SORTING_DIFF = "该箱或者站点一单多件包裹不全";
    
    public static final Integer CODE_Delivery_TRANSIT = 30006;
    public static final String MESSAGE_Delivery_TRANSIT = "箱号与目的地不一致，是否发货？";

    public static final Integer CODE_SCHEDULE_INCOMPLETE = 30007;
    public static final String MESSAGE_SCHEDULE_PACKAGE_INCOMPLETE = "包裹不齐是否强制发货？";
    public static final String MESSAGE_SCHEDULE_WAYBILL_INCOMPLETE = "运单不齐是否强制发货？";
    /**
     * 快运-拦截标识
     */
    public static final Integer CODE_INTERCEPT_FOR_B2B = 40007;
    public DeliveryResponse() {
        super();
    }
    
    public DeliveryResponse(Integer code, String message) {
        super(code, message);
    }
}
