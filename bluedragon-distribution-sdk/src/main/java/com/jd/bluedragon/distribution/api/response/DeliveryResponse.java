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

    //
    public static final Integer CODE_CROUTER_ERROR =40007;
    public static final String MESSAGE_CROUTER_ERROR="批次目的地与运单路由不一致，是否继续？";

    public static final Integer CODE_NO_BOARDSEND_DETAIL_ERROR =40008;
    public static final String MESSAGE_NO_BOARDSEND_DETAIL_ERROR="无该板号的发货记录";

    public static final Integer CODE_BOARD_SEND_NOT_FINISH_ERROR =40009;
    public static final String MESSAGE_BOARD_SEND_NOT_FINISH_ERROR="按板发货正在处理，请稍后再操作取消发货";

    public static final Integer CODE_SEND_SITE_NOTMATCH__ERROR =40010;
    public static final String MESSAGE_SEND_SITE_NOTMATCH_ERROR="批次号始发ID与操作人所属单位ID不一致";

    public static final Integer CODE_BOARD_SENDED_ERROR =40011;
    public static final String MESSAGE_BOARD_SENDED_ERROR="板号已操作发车，不能取消发货";

    public static final Integer CODE_SEND_CODE_ERROR =40012;
    public static final String MESSAGE_SEND_CODE_ERROR="批次号已操作封车，请换批次!";

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

    public static final Integer CODE_Delivery_SAVE = 30008;
    public static final String MESSAGE_Delivery_SAVE = "此单请先暂存，运单集齐后发货";

    public static final Integer CODE_SCHEDULE_INCOMPLETE = 30007;
    public static final String MESSAGE_SCHEDULE_PACKAGE_INCOMPLETE = "包裹不齐是否强制发货？";
    public static final String MESSAGE_SCHEDULE_WAYBILL_INCOMPLETE = "运单不齐是否强制发货？";
    public static final String MESSAGE_ROUTER_ERROR = "包裹/箱号对应路由下一网点与所选目的地不一致，是否继续操作?";
    public static final String MESSAGE_ROUTER_MISS_ERROR = "未查询到包裹/箱号配置的路由信息，是否继续操作？";
    public static final String MESSAGE_ROUTER_SITE_ERROR = "无法获取此包裹对应路由的末级分拣中心，是否继续操作？";

    /**
     * 快运-拦截标识
     */
    public static final Integer CODE_INTERCEPT_FOR_B2B = 40007;

    /**
     * B网包装耗材服务确认标识，与Ver提示保持一致
     */
    public static final Integer CODE_29120 = 29120;

    public static final String MESSAGE_29120 = "请先在电脑上确认此运单包装服务是否完成!";

    public DeliveryResponse() {
        super();
    }
    
    public DeliveryResponse(Integer code, String message) {
        super(code, message);
    }
}
