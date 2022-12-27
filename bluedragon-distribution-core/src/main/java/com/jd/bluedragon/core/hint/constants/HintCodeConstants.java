package com.jd.bluedragon.core.hint.constants;

/**
 * 提示语编码常量
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-15 15:27:25 周四
 */
public class HintCodeConstants {

    /**
     * 打印相关
     */
    // 补打重复
    public static String REPRINT_REPEAT = "00000001";

    // 包裹数超过限制
    public static String PRINT_P_OVER_SIZE = "00000002";

    // 取消拦截
    public static String PRINT_INTERCEPT_CANCEL = "00000003";

    /**
     * 称重量方相关
     */
    // 称重量方数据不合法
    public static String WEIGHT_AND_VOLUME_ILLEGAL_DATA = "00000004";

    // 称重量方数据不合法
    public static String WEIGHT_AND_VOLUME_BOX_NOT_EXIST = "00000005";


    /*##############################################验货提示语START#########################################################*/

    /**
     * 此运单号已绑定循环集包袋，请扫描包裹号操作!
     */
    public static String WAYBILL_BIND_RECYCLE_BAG = "10001";

    /**
     * 暂存集齐后发货
     */
    public static String JP_TEMP_STORE_TOGETHER = "10002";

    /**
     * 此单为企配仓运单，必须操作暂存上架
     */
    public static String QPC_TEMP_STORE = "10003";

    /**
     * 此运单需要进行包装，包装后请在电脑端确认
     */
    public static String PACKING_CONSUMABLE_CONFIRM = "10004";

    /**
     * 此运单需使用包装耗材，但不存在包装耗材任务
     * 此单包装服务暂无任务，请稍后再操作
     */
    public static String PACKING_CONSUMABLE_NOT_EXIST = "10005";

    /**
     * 运单需暂存，请操作暂存上架
     */
    public static String WAYBILL_NEED_TEMP_STORE = "10006";


    /*##############################################验货提示语END#########################################################*/


    /*##############################################拦截相关START#########################################################*/

    /**
     * 此弃件禁止操作，请按公司规定暂存
     * 弃件单禁止操作，请暂存
     */
    public static String WASTE_WAYBILL_TEMP_STORE = "20001";

    /**
     * 此单已拒收或妥投，请扫描正确面单或退商家打印正确面单或操作异常处理!
     */
    public static String WAYBILL_APPROPRIVATED = "20002";

    /**
     * 加盟商运单，请操作称重交接！
     */
    public static String ALLIANCE_NEED_WEIGHT = "20003";

    /**
     * 此运单有检疫证，若更换票号请录入
     */
    public static String JY_CARD_CHECK = "20004";

    /**
     * 文件标识运单请集包至WJ开头箱号
     * 文件类型需用WJ开头的箱号集包
     */
    public static String FILE_PACK_SORTING_WJ = "20005";

    /**
     * 文件包裹未集包禁止发货
     */
    public static String FILE_SEND_WITHOUT_BOX = "20006";

    /**
     * 运单为寄付营业厅运单，未操作揽收完成不允许发货/建箱!
     * 寄付运单，须有揽收动作，请异常处理
     */
    public static String BUSINESS_HALL_WAYBILL_NOT_RECV = "20007";

    /**
     * 此单为[理赔完成拦截订单]逆向订单,请暂存
     */
    public static String LP_TEMP_STORE = "20008";

    /**
     * 此单请先暂存，运单集齐后发货
     * 请暂存，待集齐后发货
     */
    public static String JP_TEMP_STORE = "20009";

    /**
     * 此单为[配送拒收报废],请联系线下处理
     */
    public static String SEVEN_FRESH_HINT = "20010";

    /**
     * 此单为[取消订单拦截],请退货
     * 此单需拦截,请操作退货换单！
     */
    public static String CANCEL_WAYBILL_INTERCEPT = "20011";

    /**
     * 此单为[拒收订单拦截],请退货
     */
    public static String REFUSE_RECEIVE_INTERCEPT = "20012";

    /**
     * 此单为[恶意订单拦截],请退货
     */
    public static String MALICIOUS_WAYBILL_INTERCEPT = "20013";

    /**
     * 此单为[白条强制拦截],请退货
     */
    public static String WHITE_BILL_FORCE_INTERCEPT = "20014";

    /**
     * 此单为[运营退货拦截],请退货
     * 此单需拦截,请操作退货换单！
     */
    public static String RETURN_GOODS_INTERCEPT = "20015";

    /**
     * 此单为预售未付全款，需要拦截暂存，等付全款后可继续操作！
     */
    public static String PRE_SELL_WITHOUT_FULL_PAY = "20016";

    /**
     * 此单未付尾款，异常处理选24预售原因，换单回仓！
     * 预收单未付尾款，请异常处理后回仓
     */
    public static String PRE_SELL_WITHOUT_FINAL_PAY = "20017";

    /**
     * 运单号:{0}已经转至C网,目前还有{1}共{2}个包裹未操作【包裹补打】进行换单，请操作【包裹补打】更换面单!
     * 运单号:{0}已转至C网,尚需{1}共{2}个包裹操作【包裹补打】更换面单
     */
    public static String PACKAGE_REPRINT_TO_TRANSFER = "20018";

    /**
     * 运单{0}配送方式或时间变化,{1}共{2}个包裹需换面单，请【包裹补打】换面单!
     */
    public static String WAYBILL_DISTRIBUTION_CHANGE_INTERCEPT = "20019";

    /**
     * 此运单为移动仓内配单，需单独建箱，禁止与其他类型运单混装
     */
    public static String MOVING_WAREHOUSE_WAYBILL_INTERCEPT = "20020";

    /**
     * 此箱号内为移动仓内配单，需单独建箱，其他类型运单禁止混装
     */
    public static String MOVING_WAREHOUSE_BOX_INTERCEPT = "20021";

    /**
     * 此运单为半退单，需单独建箱，禁止与其他类型运单混装
     */
    public static String PART_REVERSE_WAYBILL_INTERCEPT = "20022";

    /**
     * 此箱号内为半退单，需单独建箱，其他类型运单禁止混装
     */
    public static String PART_REVERSE_BOX_INTERCEPT = "20023";

    /**
     * 该订单没有重量或体积信息,不能装箱。请到【青龙分拣中心系统】称重量方
     * changeTo: 您操作的订单无重量，烦请称重量方以后再进行操作，谢谢。
     * 此单无重量/体积，请复重复量方，谢谢。
     */
    public static String WAYBILL_WITHOUT_WEIGHT_OR_VOLUME = "20024";

    /**
     * 此包裹无重量体积，请到转运工作台按包裹录入重量体积
     * changeTo: 您操作的订单无重量，烦请称重量方以后再进行操作，谢谢。
     * 此单无重量/体积，请复重复量方，谢谢。
     */
    public static String ZY_PACKAGE_WITHOUT_WEIGHT_OR_VOLUME = "20025";

    /**
     * 此单无称重重量,请称重后再操作
     * changeTo: 您操作的订单无重量，烦请称重量方以后再进行操作，谢谢。
     * 该订单没有重量或体积信息,不能装箱。请到【青龙分拣中心系统】称重量方
     * 此单无重量/体积，请复重复量方，谢谢。
     */
    public static String WAYBILL_WITHOUT_WEIGHT = "20026";

    /**
     * 该订单没有重量或体积信息，确定装箱？
     * changeTo: 您操作的订单无重量，烦请称重量方抽检，谢谢。
     * 此单无重量/体积，请复重复量方，谢谢。
     */
    public static String WAYBILL_WITHOUT_WEIGHT_WHEN_BOXING = "20027";

    /**
     * 运单无到付运费金额，不能分拣!
     */
    public static String WAYBILL_WITHOUT_RECEIVE_FREIGHT = "20028";

    /**
     * 运单无寄付运费金额，不能分拣!
     */
    public static String WAYBILL_WITHOUT_SEND_FREIGHT = "20029";

    /**
     * 此订单预分拣站已关闭,确定装箱
     */
    public static String PRE_SITE_CLOSE = "20030";

    /**
     * 超区订单，请联系现场调度操作现场预分拣
     */
    public static String OVER_AREA_WAYBILL = "20031";

    /**
     * 此箱号已经分拣了其他的派车单
     */
    public static String BOX_BIND_TRANS_BILL = "20032";

    /**
     * 此[箱号]始发地与您所在的分拣中心不一致
     * 此[箱号]始发地与您所在的分拣中心不一致，请重打箱号后再操作
     */
    public static String BOX_BEGINNING_DIFFERENT_FROM_CURRENT_SITE = "20033";

    /**
     * 此[箱号]已发过货,不允许继续装箱
     */
    public static String BOX_HAS_SENT_GOODS = "20034";

    /**
     * 此[箱号]只能分拣逆向退货普通订单
     */
    public static String BOX_USE_FOR_COMMON_REVERSE = "20035";

    /**
     * 此[箱号]只能分拣逆向退货奢侈品订单
     */
    public static String BOX_USE_FOR_LUXURY_REVERSE = "20036";

    /**
     * 此[箱号]只能分拣逆向取件普通订单
     */
    public static String BOX_USE_FOR_COMMON_AFTER_SALE = "20037";

    /**
     * 此[箱号]只能分拣逆向取件奢侈品订单
     */
    public static String BOX_USE_FOR_LUXURY_AFTER_SALE= "20038";

    /**
     * 此单为[速递中心订单],确定装箱？
     */
    public static String FAST_STATION_WAYBILL = "20039";

    /**
     * 此单为[中转站订单],确定装箱？
     */
    public static String TRANSFER_STATION_WAYBILL = "20040";

    /**
     * 此单为[自助式自提柜订单],请分拣到自助式自提点
     */
    public static String ZITIGUI_WAYBILL = "20041";

    /**
     * 此单为[自助式自提柜订单],确定装箱？
     */
    public static String ZITIGUI_WAYBILL_BOXING = "20042";

    /**
     * 此[站点]只能分拣[自助式自提柜订单]
     */
    public static String SITE_FOR_ZITIGUI_WAYBILL = "20043";

    /**
     * 此单为[便民自提柜订单],请分拣到便民自提点
     */
    public static String BIANMIN_WAYBILL = "20044";

    /**
     * 此单为[合作自提柜订单],确定装箱？
     */
    public static String HEZUO_ZITIGUI_WAYBILL = "20045";

    /**
     * 此[站点]只能分拣[合作自提柜订单]
     */
    public static String SITE_FOR_HEZUO_ZITIGUI_WAYBILL = "20046";

    /**
     * 此单与分拣[站点]不一致,确定装箱？
     * 此单与分拣[站点]不一致,是否继续？
     */
    public static String SITE_NOT_EQUAL_RECEIVE_SITE = "20047";

    /**
     * 此单为[生鲜自提订单],请分拣到生鲜自提点
     */
    public static String FRESH_ZITI_WAYBILL = "20048";

    /**
     * 此单为[自提订单],请分拣到自提点
     */
    public static String ZITI_SITE_FOR_WAYBILL = "20049";

    /**
     * 此[站点]只能分拣[自提订单]
     */
    public static String SITE_FOR_ZITI_WAYBILL = "20050";

    /**
     * 此单为[上门换新订单],不能发第三方站点
     */
    public static String DOOR_REPLACE_WAYBILL = "20051";

    /**
     * 此单为[以旧换新订单]
     */
    public static String OLD_FOR_NEW_WAYBILL = "20052";

    /**
     * 此单为[合约订单]
     */
    public static String CONTRACT_WAYBILL = "20053";

    /**
     * 此单为[货到付款订单]
     */
    public static String COD_WAYBILL = "20054";

    /**
     * [包裹]与[箱号]的承运类型不一致,确定装箱？
     */
    public static String WAYBILL_BOX_TRANSPORT_DIFFERENCE = "20055";

    /**
     * 当前箱号为航空箱,非航空订单,确定装箱?
     */
    public static String AIR_BOX_FOR_AIR_WAYBILL = "20056";

    /**
     * 不允许转网，是否强制操作？
     */
    public static String WAYBILL_C_TO_B = "20057";

    /**
     * 当前场地到目的场地不存在混装箱规则！确定装箱？
     * 当前场地到目的场地不存在混装箱规则，请先在集包规则中维护规则后再装箱！
     */
    public static String MISSING_MIX_BOX_CONFIG = "20058";

    /**
     * 跨区校验：当前网点与目的网点不在同一区域！确定装箱？
     * 跨区校验：当前网点与目的网点不在同一区域！可能导致错发，确定装箱？
     */
    public static String CROSS_AREA_VALIDATION = "20059";

    /**
     * 包裹[目的地分拣中心]与扫描装箱箱号不一致,确定装箱？
     * 包裹[目的地分拣中心]与扫描装箱箱号不一致，可能导致错发，是否继续？
     */
    public static String RECEIVE_SITE_AND_DESTINATION_DIFFERENCE = "20060";

    /**
     * 订单信息变更,请补打包裹标签,是否继续分拣
     */
    public static String WAYBILL_INFO_CHANGE = "20061";

    /**
     * 订单信息变更,请补打包裹标签
     */
    public static String WAYBILL_INFO_CHANGE_FORCE = "20085";


    /**
     * 无此包裹或运单信息,确定装箱？
     */
    public static String WAYBILL_OR_PACKAGE_NOT_FOUND = "20062";

    /**
     * 请扫描板号/箱号/包裹号进行发货
     */
    public static String SCAN_BARCODE_LIMIT = "20063";

    /**
     * 支付类型为空，联系IT咚咚：org.wlxt2 处理
     */
    public static String WAYBILL_MISSING_PAYMENT_TYPE = "20064";

    /**
     * 特殊属性为空，联系IT咚咚：org.wlxt2 处理
     */
    public static String WAYBILL_MISSING_SEND_PAY = "20065";

    /**
     * 运单类型为空，联系IT咚咚：org.wlxt2 处理
     */
    public static String WAYBILL_TYPE_MISSING = "20066";

    /**
     * 此[箱号]只能分拣[奢侈品订单]
     */
    public static String BOX_USE_FOR_LUXURY_WAYBILL = "20067";

    /**
     * 此[箱号]只能分拣正向奢侈品订单
     */
    public static String BOX_USE_FOR_FORWARD_LUXURY_WAYBILL= "20068";

    /**
     * 此单为[奢侈品订单],请分拣到奢侈品箱号
     */
    public static String LUXURY_WAYBILL_CHOOSE_BOX = "20069";

    /**
     * 此单为[奢侈品订单]
     */
    public static String LUXURY_WAYBILL = "20070";

    /**
     * 此单为[奢侈品订单],请分拣到退货奢侈品箱号
     */
    public static String REVERSE_LUXURY_BOX_FOR_LUXURY_WAYBILL = "20071";

    /**
     * 此单为[奢侈品订单],请分拣到取件奢侈品箱号
     */
    public static String PICKUP_LUXURY_BOX_FOR_LUXURY_WAYBILL = "20072";

    /**
     * 此单为[取件订单]
     */
    public static String PICKUP_WAYBILL = "20073";

    /**
     * 此单为[退货订单]
     */
    public static String REVERSE_WAYBILL = "20074";

    /**
     * 此[站点]只能分拣[合作代收]
     */
    public static String HEZUODAISHOU_SITE = "20075";

    /**
     * 此订单预分拣站不存在或已关闭,确定装箱
     */
    public static String PRE_SITE_CLOSED_WHEN_SORTING = "20076";

    /**
     * 此[运单]为夺宝岛订单,禁止发往大库
     */
    public static String AUCTION_WAYBILL = "20077";

    /**
     * 此单为[合作代收],确定装箱？
     */
    public static String HEZUODAISHOU_WAYBILL = "20078";

    /**
     * 批次目的地与路由下一网点不一致，是否继续？路由下一站：{0}
     */
    public static String BATCH_DEST_AND_NEXT_ROUTER_DIFFERENCE = "20079";

    /**
     * // 无重量拦截弱校验提示
     * 您操作的订单无重量，烦请称重量方抽检，谢谢。
     */
    public static String WAYBILL_WITHOUT_WEIGHT_WEAK_INTERCEPT = "20080";
    
    /**
     * 全量接单失败拦截
     * 此单为[全量接单失败拦截],请退货。
     */
    public static String FULL_ORDER_FAIL_INTERCEPT = "20081";
    /**
     * 此订单目的分拣中心已关闭,确定装箱。
     */
    public static String EMD_DMSSITE_CLOSE="20082";
    /**
     * 此单末端非直发包站点，禁止装箱
     */
    public static String CODE_DIRECT_SEND_SITE_ERROR = "20083";
    /**
     * 未配置发出地[始发分拣名称]至目的地[目的分拣名称]的医药直发包规则，请先创建医药直发规则
     */
    public static String CODE_COLD_CHAIN_SITE_NO_ROUTE = "20084";
    /**
     * 不允许按直发医药箱号进行发货，请拆箱后再进行发货
     */
    public static String CODE_COLD_CHAIN_SEND_BOX_ERROR = "20086";

    /*##############################################拦截相关END#########################################################*/



    /*##############################################发货相关START#########################################################*/

    /**
     * 该发货批次号已操作封车，无法重复操作！
     * 该发货批次号已操作封车，请勿重复操作！
     */
    public static String SEND_CODE_SEALED = "30001";

    /**
     * 请输入正确的批次号！
     */
    public static String SEND_CODE_ILLEGAL = "30002";

    /**
     * 该箱号已经发货
     */
    public static String BOX_SENT_ALREADY = "30003";

    /**
     * 当前众邮箱号无分拣复重重量或复重体积，请复重量方后再发货
     * 此单无重量/体积，请复重复量方，谢谢。
     */
    public static String ZY_BOX_MISSING_WEIGHT_OR_VOLUME = "30004";

    /**
     * 箱号与目的地不一致，是否发货？
     */
    public static String BOX_DESTINATION_AND_RECEVIE_SITE_DIFFERENCE = "30005";

    /**
     * 无该箱号发货的记录
     */
    public static String BOX_SENDM_MISSING = "30006";

    /**
     * 该箱号还没有完验
     */
    public static String THIRD_BOX_INSPECTION_EXCEPTION = "30007";

    /**
     * 此单为[妥投状态]，请先核实异常，在PDA上提交配送异常后再进行逆向操作!
     * 此单已妥投，请异常处理
     */
    public static String WAYBILL_DELIVERED_WHILE_REVERSE = "30008";

    /**
     * 包裹号不正确，请检查包裹号对应的滑道号或重打面单！
     */
    public static String PACKAGE_CODE_ILLEGAL = "30009";

    /**
     * 运单号[{0}]发货验证失败。该运单中无包裹信息
     */
    public static String SEND_VALIDATE_WAYBILL_HAS_PACKAGE = "30010";

    /**
     * 批次号已操作封车，请换批次！
     */
    public static String SEND_CODE_SEALED_TIPS_SECOND = "30011";

    /**
     * 该箱号/包裹已发货，是否取消上次发货并操作本次发货？
     */
    public static String CANCEL_LAST_SEND = "30012";

    /**
     * 请先在电脑上确认此运单包装服务是否完成!
     * 此单有包装服务，请在【青龙分拣系统】完成确认
     */
    public static String PACKING_CONSUMABLE_CONFIRM_TIPS_SECOND = "30013";

    /**
     * 运单无总重量：{0}
     */
    public static String WAYBILL_MISSING_TOTAL_WEIGHT = "30014";

    /**
     * 运单无总体积：{0}
     */
    public static String WAYBILL_MISSING_TOTAL_VOLUME = "30015";

    /**
     * 运单无到付运费金额：{0}
     */
    public static String WAYBILL_MISSING_RECEIVE_FREIGHT = "30016";

    /**
     * 运单无寄付运费金额：{0}
     */
    public static String WAYBILL_MISSING_SEND_FREIGHT = "30017";

    /**
     * 未查询到包裹/箱号配置的路由信息，是否继续操作？
     */
    public static String MISSING_ROUTER = "30018";

    /**
     * 包裹/箱号对应路由下一网点与所选目的地不一致，是否继续操作?
     */
    public static String NEXT_ROUTER_AND_DESTINATION_DIFFERENCE = "30019";

    /**
     * 有不全运单，请处理后再发货
     * 此单未集齐，待集齐后再发货
     */
    public static String WAYBILL_SEND_DIFFERENCE = "30020";

    /**
     * 包裹不齐是否强制发货？
     */
    public static String WAYBILL_SEND_NOT_COMPLETE = "30021";

    /**
     * 该运单正在处理中，请等待处理完成
     * 该单号下的所有包裹正在按运单发货处理中，请等待处理完成
     */
    public static String WAYBILL_SEND_IS_PROCESSING = "30022";

    /**
     * 部分发货成功，存在[{0}]个单号正在发货处理中，请等待处理完成后再查看或操作
     */
    public static String CURRENT_SEND_EXIST_PROCESSING = "30023";

    /**
     * 该批次的发货操作已提交，请勿重复操作
     */
    public static String BATCH_SEND_PROCESSING = "30024";

    /**
     * 当前批次始发ID与操作人所属单位ID不一致!
     */
    public static String BATCH_ORIGIN_AND_OPERATOR_ORIGIN_DIFFERENCE = "30025";

    /**
     * 该单号下的所有包裹正在按运单发货处理中，请等待处理完成
     */
    public static String SEND_BY_WAYBILL_PROCESSING = "30026";

    /**
     * 箱子已经在该批次中发货，请勿重复操作
     */
    public static String BOX_SENT_BY_THIS_BATCH = "30027";

    /**
     * 箱子已经在批次[{0}]中发货
     * 箱子已经在批次[{0}]中发货，请勿重复操作！
     */
    public static String BOX_SENT_BY_CONCRETE_BATCH = "30028";

    /**
     * 该包裹已发货，是否取消上次发货并重新发货？
     */
    public static String CANCEL_LAST_SEND_TIPS_SECOND = "30029";

    /**
     * 当前批次为生鲜批次专用，而此单是非生鲜运单，请换普通批次
     * 发货批次为生鲜批次，严禁发非生鲜的运单
     */
    public static String SPECIAL_FRESH_BATCH = "30030";

    /**
     * 已经操作过按板发货.
     */
    public static String BOARD_SENT_ALREADY = "30031";

    /**
     * 获取板号目的地失败
     */
    public static String FAIL_TO_GET_BOARD_DEST = "30032";

    /**
     * 获取批次号目的地失败
     */
    public static String FAIL_TO_GET_BATCH_DEST= "30033";

    /**
     * 板号目的地与批次号目的地不一致，是否强制操作发货？
     */
    public static String BOARD_AND_BATCH_DEST_DIFFERENCE = "30034";

    /**
     * 组板发货板号校验失败
     * 板发货板号校验失败，重新操作发
     */
    public static String BOARD_SEND_FAIL = "30035";

    /**
     * 已经装载不允许取消
     */
    public static String PRE_LOAD_CANNOT_CANCEL = "30036";

    /**
     * 无该运单的发货记录
     */
    public static String WAYBILL_SENDM_MISSING = "30037";

    /**
     * 无该包裹的发货记录
     */
    public static String PACKAGE_SENDM_MISSING = "30038";

    /**
     * 下游已解封车，不允许取消发货
     */
    public static String CANCEL_DELIVERY_CHECK_UNSEAL = "30039";

    /**
     * 操作封车已超过一小时，不允许取消发货。请先操作 取消封车
     */
    public static String ABORT_CANCEL_EXCEED_ONE_HOUR = "30040";

    /**
     * 根据批次号获取封车状态时异常
     */
    public static String GET_BATCH_SEAL_STATUS_ERROR = "30041";

    /**
     * 取消发货校验封车异常
     */
    public static String ABORT_DELIVERY_CHECK_SEAL_ERROR = "30042";

    /**
     * 无该板号的发货记录
     */
    public static String BOARD_SENDM_MISSING = "30043";

    /**
     * 当前板号是封车批次中的唯一板号，不允许取消发货。请先操作 取消封车
     */
    public static String UNIQUE_BOARD_IN_THE_BATCH = "30044";

    /**
     * 当前包裹是封车批次中的唯一包裹，不允许取消发货。请先操作 取消封车
     */
    public static String UNIQUE_PACKAGE_IN_THE_BATCH = "30045";

    /**
     * 当前运单是封车批次中的唯一运单，不允许取消发货。请先操作 取消封车
     */
    public static String UNIQUE_WAYBILL_IN_THE_BATCH = "30046";

    /**
     * 当前箱号是封车批次中的唯一箱号，不允许取消发货。请先操作 取消封车
     */
    public static String UNIQUE_BOX_IN_THE_BATCH = "30047";

    /**
     * 该单号下的所有包裹正在按运单发货处理中，请等待处理完成
     */
    public static String DELIVERY_BY_WAYBILL_PROCESSING = "30048";

    /**
     * 该箱号已经发车
     */
    public static String BOX_SENT_ALREADY_TIPS_SECOND = "30049";

    /**
     * 发货处理中请稍后再试
     */
    public static String DELIVERY_PROCESSING = "30050";

    /**
     * 板号已操作发车，不能取消发货
     */
    public static String ABORT_CANCEL_AFTER_BOARD_SENT = "30051";

    /**
     * 按板发货正在处理，请稍后再操作取消发货
     */
    public static String BOARD_DELIVERY_PROCESSING = "30052";

    /**
     * 无该批次号的发货记录
     */
    public static String QUERY_DELIVERY_BY_BATCH_MISSING = "30053";


    /*##############################################发货相关END#########################################################*/

    /**
     * 打印客户端-站点人员无菜单权限提示
     */
    public static String PRINT_CLINET_SMS_SITE_MENU_NOAUTH = "60001";
    /*##############################################发货相关END#########################################################*/

    /**
     * 没有对应站点
     */
    public static String SITE_NOT_EXIST = "40001";

    /**
     * 单号不正确，未通过京东单号规则校验
     * 单号不符合运单规则校验
     */
    public static String WAYBILL_RULE_ILLEGAL = "40002";

    /**
     * 运单{0}信息为空，请联系 配送系统运营
     */
    public static String WAYBILL_MISSING_OLD_SITE = "40003";

    /**
     * 调用运单接口异常
     */
    public static String GET_WAYBILL_CHOICE_ERROR = "40004";

    /**
     * 调用路由接口异常
     */
    public static String GET_ROUTER_BY_WAYBILL_ERROR = "40005";

    /**
     * 发货配置接口异常
     */
    public static String GET_AREA_DEST_ERROR = "40006";

    /**
     * 包裹号或运单号不能为空
     */
    public static String WAYBILL_OR_PACKAGE_IS_NULL = "40007";

    /**
     * 包裹或运单信息正在处理，请稍候
     */
    public static String CANCEL_SORTING_IS_PROCESSING = "40008";

    /**
     * 包裹/运单/箱子正在取消分拣，请稍后
     */
    public static String CANCEL_SORTING_PROCESSING = "40009";

    /**
     * 无包裹/运单/箱子分拣记录
     */
    public static String NO_SORTING_RECORD = "40010";

    /**
     * 包裹/运单/箱子已经发货或未分拣，不能取消分拣
     */
    public static String FAIL_CANCEL_SORTING_AFTER_SENDING = "40011";

    /**
     * 包裹已经验货，不能取消分拣
     */
    public static String FAIL_CANCEL_SORTING_AFTER_INSPECTING = "40012";

    /**
     * 运单/箱子的包裹数大于2万，请联系IT人员报备此操作
     */
    public static String PACKAGE_NUM_GTE_TWENTY_THOUSAND = "40013";

    /**
     * 无箱号信息
     */
    public static String BOX_NOT_EXIST = "40014";

    /**
     * 箱号无打印
     */
    public static String BOX_NO_USE = "40015";

    /**
     * 此箱号已经发货
     */
    public static String BOX_SENT = "40016";

    /**
     * 此箱号未绑定循环集包袋
     * 此箱号未绑定循环集包袋，请绑定集包袋！
     */
    public static String BOX_UNBIND_RECYCLE_BAG = "40017";

    /**
     * 你所在场地未开通此功能
     */
    public static String YOUR_SITE_CAN_NOT_USE_FUNC = "50001";
    
    
    /**
     * 异常上报-改地址拦截:改址订单，请补打操作 
     */
    public static String EX_REPORT_CHECK_CHANGE_ADDRESS = "50003";    
    
    /**
     * 非航空单无法操作航空转陆运
     */
    public static String AIR_TO_ROAD_NOT_ALLOWD="50002";
    /**
     * 存在非航空单操作，提示非航空单数量
     */
    public static String AIR_TO_ROAD_TIP_MSG="50004";
    /**
     * 非一单一件单错发到德邦虚拟分拣中心
     */
    public static String NOT_ONE_PACK_WAYBILL_WRONG_SEND_MSG="50005";

    /**
     * 非单错发到德邦虚拟分拣中心
     */
    public static String NOT_DP_WAYBILL_WRONG_SEND_MSG="50006";
    public static String NOT_FOUND_TRANSFER_TASK_DATA_MSG="50008";
    public static String NOT_FOUND_BINDING_TASK_DATA_MSG="50009";

    /*** 拣运降级提示语 start **/
    // 拣运降级全局提示语：大促降级，部分数据展示不全，请稍后重新查看!
    public static String JY_DEMOTION_MSG_SEAL_GLOBAL = "70000";
    // 封车任务详情：大促降级导致未查询到封车任务明细，请稍后查看!
    public static String JY_DEMOTION_MSG_SEAL_DETAIL = "70001";
    // 多扫和待扫包裹明细：大促降级导致未查询到多扫和待扫包裹明细，请稍后查看!
    public static String JY_DEMOTION_MSG_UNLOAD_MORE_AND_TOSCAN = "70002";
    // 多扫单号明细：大促降级导致未查询到多扫包裹明细，请稍后查看!
    public static String JY_DEMOTION_MSG_UNLOAD_MORE_SCAN = "70003";
    // 拦截单号明细：大促降级导致未查询到拦截包裹明细，请稍后查看!
    public static String JY_DEMOTION_MSG_UNLOAD_INTERCEPT = "70004";
    // 卸车待扫包裹：大促降级导致未查询到待扫包裹明细，请稍后查看!
    public static String JY_DEMOTION_MSG_UNLOAD_TOSCAN = "70005";
    // 发货进度不准：大促降级导致发货进度不准，请稍后查看!
    public static String JY_DEMOTION_MSG_SEND_PROCESS_NOT_ACCURATE = "70006";
    // 卸车进度不准：大促降级导致卸车进度不准，请稍后查看!
    public static String JY_DEMOTION_MSG_UNLOAD_PROCESS_NOT_ACCURATE = "70007";
    // 根据包裹号从ES未获得封车编码：因大促降级导致根据包裹未查询到封车编码，请录入车牌号查询!
    public static String JY_DEMOTION_MSG_NO_SEAL_CAR_CODE = "70008";
    // 发货异常：大促降级导致未查询到发货异常明细，请稍后查看!
    public static String JY_DEMOTION_MSG_SEND_ABNORMAL = "70009";
    // 强制发货包裹明细：大促降级导致未查询到强制发货包裹明细，请稍后查看!
    public static String JY_DEMOTION_MSG_SEND_FORCE = "70010";
    // 发货拦截：大促降级导致未查询到发货拦截包裹明细，请稍后查看!
    public static String JY_DEMOTION_MSG_SEND_INTERCEPT = "70011";
    /*** 拣运降级提示语 start **/

    // 设备抽检任务已关闭并且未超过2小时，是否强制创建？
    public static String JY_MACHINE_CALIBRATE_TASK_CLOSED_AND_NOT_OVER_2_HINT = "80001";
}
