package com.jd.bluedragon;
/**
 * 
 * @ClassName: TextConstants
 * @Description: 字符串定义常量类
 * @author: wuyoude
 * @date: 2018年3月14日 下午12:35:24
 *
 */
public class TextConstants {
    /**
     * 运费-到付现结
     */
    public static final String FREIGHT_PAY_CASH = "到付现结";

    /**
     * 运费-寄付现结
     */
    public static final String FREIGHT_CONSIGER_CLEAR="寄付现结";
    /**
     * 货款-货到付款
     */
    public static final String GOODS_PAYMENT_COD = "货到付款";
    /**
     * 货款-在线支付
     */
    public static final String GOODS_PAYMENT_ONLINE = "在线支付";
    
    /**
     * 备注-【合并送】
     */
    public static final String REMARK_SEND_GATHER_TOGETHER ="【合并送】";

    /**
     * 已称标识
     */
    public static final String WEIGHT_FLAG_TRUE = "【已称】";

    /**
     * 配送方式 【送】/【提】
     */
    public static final String DELIVERY_METHOD_SEND="【送】";

    /**
     * waybill_sign36位=0 且waybill_sign40位=1 且 waybill_sign54位=2：冷链整车
     */
    public static final String B2B_FRESH_WHOLE_VEHICLE ="冷链专车";

    /**
     * waybill_sign36位=1 且waybill_sign40位=2 且 waybill_sign54位=2：快运冷链
     */
    public static final String B2B_FRESH_EXPRESS ="冷链卡班";

    /**
     * waybill_sign36位=1 且waybill_sign40位=3 且 waybill_sign54位=2：仓配冷链
     */
    public static final String B2B_FRESH_WAREHOUSE ="仓配冷链";

    /**
     * 当waybill_sign第62位等于1时，确定为B网营业厅运单:
     * 此时，waybill_sign第80位等于1时，面单打印“特惠运”
     */
    public static final String B2B_CHEAP_TRANSPORT ="特惠运";

    /**
     * 当waybill_sign第62位等于1时，确定为B网营业厅运单:
     * 此时，waybill_sign第80位等于2时，面单打标“特准运”
     */
    public static final String B2B_TIMELY_TRANSPORT ="特准运";
    /**
     * 京准达快递到车
     */
    public static final String	TEXT_TRANSPORT_KDDC="京准达\n快递到车";
    /**
     * 常用字符串:‘无’
     */
    public static final String COMMON_TEXT_NOTHING ="无";
}
