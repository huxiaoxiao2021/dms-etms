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
     * 运费 - 运费到付
     */
    public static final String FREIGHT_PAY = "运费到付";

    /**
     * B网运费-寄付
     */
    public static final String FREIGHT_SEND = "寄付";

    /**
     * B网运费-月结
     */
    public static final String FREIGHT_MONTH = "月结";

    /**
     * B网运费-临时欠款
     */
    public static final String FREIGHT_TEMPORARY_ARREARS="临欠";
    /**
     * COD-货到付款
     */
    public static final String GOODS_PAYMENT_COD_FLAG = "COD";
    /**
     * 货款-货到付款
     */
    public static final String GOODS_PAYMENT_COD = "货到付款￥";
    /**
     * 货款-在线支付
     */
    public static final String GOODS_PAYMENT_ONLINE = "在线支付";

    /**
     * 货款-代收货款
     */
    public static final String GOODS_PAYMENT_NEED_PAY = "代收货款";
    
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
     * 冷链整车 -> 冷链专车 -> 冷链整车
     */
    public static final String B2B_FRESH_WHOLE_VEHICLE ="冷链整车";

    /**
     * waybill_sign36位=1 且waybill_sign40位=2 且 waybill_sign54位=2：快运冷链
     * 快运冷链下新增 原逻辑待业务确认是否变更
     * {
     *     1. Waybill_sign54=2生鲜行业 且Waybill_sign40=2 纯配快运零担 且Waybill_sign80=6 且118=2 城配整车，即为：“冷链城配整车”
     *     2. Waybill_sign54=2生鲜行业 且 Waybill_sign40=2纯配快运零担 且Waybill_sign80=6 且 118= 1或者0或者空 城配共配，即为：“冷链城配共配”
     *     3. Waybill_sign54=2 生鲜行业 且Waybill_sign40=2 纯配快运零担且Waybill_sign80=8 专车，代表：“冷链整车”
     * }
     */
    public static final String B2B_FRESH_EXPRESS ="冷链卡班";

    /**
     * waybill_sign36位=1 且waybill_sign40位=3 且 waybill_sign54位=2：仓配冷链
     */
    public static final String B2B_FRESH_WAREHOUSE ="仓配冷链";

    /**
     * Waybill_sign54=2生鲜行业 且Waybill_sign40=2 纯配快运零担 且Waybill_sign80=6 且118=2：  城配整车
     */
    public static final String B2B_FRESH_URBAN_WHOLE_VEHICLE = "城配整车";

    /**
     * Waybill_sign54=2 生鲜行业 且Waybill_sign40=2 纯配快运零担 且Waybill_sign80=6 且 118=1或者0或者空：  城配共配
     */
    public static final String B2B_FRESH_URBAN_TOGETHER = "城配共配";

    /**
     * 当waybill_sign第62位等于1时，确定为B网营业厅运单:
     * 此时，waybill_sign第80位等于1时，面单打印“特惠运”
     */
    public static final String B2B_CHEAP_TRANSPORT ="";

    /**
     * 当waybill_sign第62位等于1时，确定为B网营业厅运单:
     * 此时，waybill_sign第80位等于2时，面单打标“特准运”
     */
    public static final String B2B_TIMELY_TRANSPORT ="【准】";
    /**
     * 京准达快递到车
     */
    public static final String	TEXT_TRANSPORT_KDDC="京准达 快递到车";
    /**
     * 常用字符串:‘无’
     */
    public static final String COMMON_TEXT_NOTHING ="无";
    /**
     * 订单号前缀-订单号
     */
    public static final String PRINT_TEXT_ORDER_CODE_PREFIX ="订单号";

    /**
     * waybill_sign第54位等于4 且 第40位等于2或3
     */
    public static final String COMMON_TEXT_MEDICINE_SCATTERED ="医药零担";
    /**
     * 京准达-标识-‘准’
     */
    public static final String	TEXT_JZD_SPECIAL_MARK="准";
    /**
     * 京准达字符-‘京准达’
     */
    public static final String TEXT_JZD="京准达";
    /**
     * 预计送达时间展示，格式化
     */
    public static final String PROMISE_TEXT_FORMAT="配送时间：%s";
    /**
     * KA标识
     */
    public static final String KA_FLAG = "KA";
    /**
     * 特准包裹-标识-‘快’
     */
    public static final String	PECIAL_TIMELY_MARK="快";
    /**
     * 纯配B2B运单、纯配C转B添加水印“B”
     */
    public static final String	PECIAL_B_MARK="B";
    /**
     * 纯配B2B运单、纯配C转B添加水印 B-【运单号】后4位
     */
    public static final String	PECIAL_B_MARK1="B-";    
    /**
     * 无接触标识‘代’
     */
    public static final String NO_TOUCH_FLAG = "代";
    /**
     * 海运标识‘H’
     */
    public static final String TRANSPORT_SEA_FLAG = "H";
    /**
     * 预售标识‘预’
     */
    public static final String PRE_SELL_FLAG = "预";
    /**
     * 运输模式‘特快送 次晨’
     */
    public static final String EXPRESS_DELIVERY_NEXT_MORNING = "特快送 次晨";
    /**
     * 运输模式‘特快送’
     */
    public static final String EXPRESS_DELIVERY = "特快送";
    /**
     * 运输模式‘特快送 同城’
     */
    public static final String EXPRESS_DELIVERY_SAME_CITY = "特快送 同城";
    /**
     * 无人车配送标识-车
     */
    public static final String WRCPS_FLAG = "车";
    /**
     * 尊
     */
    public static final String SPECIAL_MARK_SENIOR ="尊";
    /**
     * 合约机 需激活
     */
    public static final String REMARK_CONTRACT_PHONE ="合约机 需激活";
}
