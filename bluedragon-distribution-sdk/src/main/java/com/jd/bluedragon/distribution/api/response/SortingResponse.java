package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SortingResponse extends JdResponse {

    private static final long serialVersionUID = 5954178829551983145L;

    public static final Integer CODE_PARAM_IS_NULL = 22001;
    public static final String MESSAGE_PARAM_IS_NULL = "参数不能为空";

    public static final Integer CODE_PACKAGE_CODE_OR_WAYBILL_CODE_IS_NULL = 22002;
    public static final String MESSAGE_PACKAGE_CODE_OR_WAYBILL_CODE_IS_NULL = "包裹号或运单号不能为空";

    public static final Integer CODE_SORTING_WAITING_PROCESS = 22003;
    public static final String MESSAGE_SORTING_WAITING_PROCESS = "包裹或运单信息正在处理，请稍候";
    
    public static final Integer CODE_SORTING_SENDED = 22004;
    public static final String MESSAGE_SORTING_SENDED = "包裹/运单/箱子已经发货或未分拣，不能取消分拣";

    public static final Integer CODE_SORTING_RECORD_NOT_FOUND = 22005;
    public static final String MESSAGE_SORTING_RECORD_NOT_FOUND = "无包裹/运单/箱子分拣记录";
    
    public static final Integer CODE_SORTING_INSPECTED = 22006;
    public static final String MESSAGE_SORTING_INSPECTED = "包裹已经验货，不能取消分拣";

    public static final Integer CODE_SORTING_CANCEL_PROCESS = 22007;
    public static final String MESSAGE_SORTING_CANCEL_PROCESS = "包裹/运单/箱子正在取消分拣，请稍后";

    public static final Integer CODE_PACKAGE_NUM_LIMIT = 22008;
    public static final String MESSAGE_PACKAGE_NUM_LIMIT = "运单/箱子的包裹数大于2万，请联系IT人员报备此操作";

    public static final Integer CODE_29212 = 29212;

    public static final Integer CODE_29300 = 29300;
    public static final String MESSAGE_29300 = "此[包裹]或[运单]已经[锁定]或[取消]，请联系客服或退货";
    
    public static final Integer CODE_293000 = 293000;
    public static final String MESSAGE_293000 = "此[包裹]或[运单]已经[锁定]或[取消]，未出库，请联系客服或退货";
    
    public static final Integer CODE_29301 = 29301;
    public static final String MESSAGE_29301 = "此[包裹]或[运单]为[锁定订单]，请联系客服";
    
    public static final Integer CODE_293010 = 293010;
    public static final String MESSAGE_293010 = "此[包裹]或[运单]为[锁定订单]，未出库，请联系客服";
    
    public static final Integer CODE_29302 = 29302;
    public static final String MESSAGE_29302 = "此[包裹]或[运单]为[取消订单]，请退货";
    
    public static final Integer CODE_293020 = 293020;
    public static final String MESSAGE_293020 = "此[包裹]或[运单]为[取消订单]，未出库，请退货";
    
    public static final Integer CODE_39006 = 39006;
    public static final String MESSAGE_39006 = "此单为取消订单，请逆向退回，确定强制继续？";
    
    public static final Integer CODE_293040 = 293040;
    public static final String MESSAGE_293040 = "此[包裹]或[运单]为[未出库]，请联系客服";
    
    public static final Integer CODE_29303 = 29303;
    public static final String MESSAGE_29303 = "此[包裹]或[运单]为[退款100分订单]，请退货";
    //病单拦截提示信息
    public static final Integer CODE_29307 = 29307;
    public static final String MESSAGE_29307 = "此单为[病单],请退货";
    
    public static final Integer CODE_29121 = 29121;
    public static final String MESSAGE_29121 = "此单为[妥投状态]，请先核实异常，在PDA上提交配送异常后再进行逆向操作!";

    public static final Integer CODE_29122 = 29122;
    public static final String MESSAGE_29122 = "此单获取运单状态异常!";

    public static final Integer CODE_31121 = 31121;
    public static final String MESSAGE_31121 = "此单仓储已收货，是否继续退货";

    public static final Integer CODE_31122 = 31122;
    public static final String MESSAGE_31122 = "此单已报丢报损，是否继续退货";

    public static final Integer CODE_EXE_ERROR = 22011;
    public static final String MESSAGE_EXE_ERROR = "取消分拣异常";

    public static final Integer CODE_31123 = 31123;
    public static final String MESSAGE_31123 = "验货未集齐是否继续操作";

    /**************************************配送拦截新逻辑START****************************/

    public static final Integer CODE_29311 = 29311;
    public static final String MESSAGE_29311 = "此单为[取消订单拦截],请退货";

    public static final Integer CODE_29312 = 29312;
    public static final String MESSAGE_29312 = "此单为[拒收订单拦截],请退货";

    public static final Integer CODE_29313= 29313;
    public static final String MESSAGE_29313 = "此单为[恶意订单拦截],请退货";

    public static final Integer CODE_29316 = 29316;
    public static final String MESSAGE_29316 = "此单为[白条强制拦截],请退货";

    /**************************************配送拦截新逻辑END******************************/


    /** 箱号 */
    private String boxCode;

    /** 运单号 */
    private String waybillCode;

    /** 包裹号 */
    private String packageCode;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;

    /** 最后操作人编号 */
    private Integer updateUserCode;

    /** 最后操作人 */
    private String updateUser;

    /** 最后修改时间 */
    private Date updateTime;
    
    /**取消分拣处理结果*/
    private Map<String,Integer> cancelResult;

    public static final Integer CODE_29000 = 29000;
    public static final String MESSAGE_29000 = "箱号不能为空";

    public static final Integer CODE_29001 = 29001;
    public static final String MESSAGE_29001 = "无此箱号信息";

    /**
     * @deprecated
     */
    public static final Integer CODE_29002 = 29002;
    /**
     * @deprecated
     */
    public static final String MESSAGE_29002 = "此[箱号]对应[站点]与分拣[站点]不一致";

    public static final Integer CODE_29003 = 37000;
    public static final String MESSAGE_29003 = "此[箱号]只能分拣正向普通订单";

    public static final Integer CODE_29004 = 29004;
    public static final String MESSAGE_29004 = "此[箱号]只能分拣逆向退货普通订单";

    public static final Integer CODE_29005 = 29005;
    public static final String MESSAGE_29005 = "此[箱号]只能分拣逆向取件普通订单";

    public static final Integer CODE_29006 = 29006;
    public static final String MESSAGE_29006 = "此[箱号]只能分拣[奢侈品订单]";

    public static final Integer CODE_29007 = 29007;
    public static final String MESSAGE_29007 = "此[箱号]只能分拣正向奢侈品订单";

    public static final Integer CODE_29008 = 29008;
    public static final String MESSAGE_29008 = "此[箱号]只能分拣逆向退货奢侈品订单";

    public static final Integer CODE_29009 = 29009;
    public static final String MESSAGE_29009 = "此[箱号]只能分拣逆向取件奢侈品订单";

    //为校验操作人的分拣中心与箱号始发地是否一致。
    public static final Integer CODE_29010 = 29010;
    public static final String MESSAGE_29010 = "此[箱号]始发地与您所在的分拣中心不一致";

    //已发货的箱号不允许继续装箱校验
    public static final Integer CODE_29011 = 29011;
    public static final String MESSAGE_29011 = "此[箱号]已发过货,不允许继续装箱";

    public static final Integer CODE_29100 = 29100;
    public static final String MESSAGE_29100 = "包裹或运单号不能为空";

    public static final Integer CODE_29102 = 29102;
    public static final String MESSAGE_29102 = "此单为[货到付款订单]";

    public static final Integer CODE_29103 = 29103;
    public static final String MESSAGE_29103 = "此单为[上门换新订单],不能发第三方站点";

    public static final Integer CODE_29104 = 29104;
    public static final String MESSAGE_29104 = "此单为[以旧换新订单]";

    public static final Integer CODE_29105 = 29105;
    public static final String MESSAGE_29105 = "此单为[合约订单]";

    public static final Integer CODE_29106 = 29106;
    public static final String MESSAGE_29106 = "此单为[地铁自提订单],请分拣到地铁自提点";

    public static final Integer CODE_29107 = 29107;
    public static final String MESSAGE_29107 = "此单为[奢侈品订单],请分拣到奢侈品箱号";

    public static final Integer CODE_29108 = 29108;
    public static final String MESSAGE_29108 = "此单为[自提订单],请分拣到自提点";

    public static final Integer CODE_2910801 = 2910801;
    public static final String MESSAGE_2910801 = "此单为[生鲜自提订单],请分拣到生鲜自提点";

    public static final Integer CODE_29109 = 29109;
    public static final String MESSAGE_29109 = "此单为[奢侈品订单]";

    public static final Integer CODE_29110 = 29110;
    public static final String MESSAGE_29110 = "此单为[奢侈品订单],请分拣到退货奢侈品箱号";

    public static final Integer CODE_29111 = 29111;
    public static final String MESSAGE_29111 = "此单为[奢侈品订单],请分拣到取件奢侈品箱号";

    public static final Integer CODE_29112 = 29112;
    public static final String MESSAGE_29112 = "此单为[取件订单]";

    public static final Integer CODE_29113 = 29113;
    public static final String MESSAGE_29113 = "此单为[退货订单]";

    public static final Integer CODE_29114 = 29114;
    public static final String MESSAGE_29114 = "此单为[好邻居便利自提点订单],请分拣到好邻居便利自提点";

    public static final Integer CODE_29115 = 29115;
    public static final String MESSAGE_29115 = "此单为[社区合作自提点订单],请分拣到社区合作自提点";

    public static final Integer CODE_29116 = 29116;
    public static final String MESSAGE_29116 = "此单为[自助式自提柜订单],请分拣到自助式自提点";

    public static final Integer CODE_29117 = 29117;
    public static final String MESSAGE_29117 = "此单为[中转站订单]";

    public static final Integer CODE_29118 = 29118;
    public static final String MESSAGE_29118 = "此单为[便民自提柜订单],请分拣到便民自提点";

    public static final Integer CODE_29119 = 29119;
    public static final String MESSAGE_29119 = "此单为[配送拒收报废],请联系线下处理";

    public static final Integer CODE_29120 = 29120;
    public static final String MESSAGE_29120 = "请先在电脑上确认此运单包装服务是否完成!";

    public static final Integer CODE_29123 = 29123;
    public static final String MESSAGE_29123 = "该包裹支持半退，禁止装箱，请原包分拣!";

    //1
    public static final Integer CODE_29200 = 29200;
    public static final String MESSAGE_29200 = "分拣中心不能为空";

    public static final Integer CODE_29201 = 29201;
    public static final String MESSAGE_29201 = "站点不能为空";

    public static final Integer CODE_29202 = 29202;
    public static final String MESSAGE_29202 = "无此站点信息或站点已关闭";

    public static final Integer CODE_29203 = 29203;
    public static final String MESSAGE_29203 = "此[站点]只能分拣[地铁自提订单]";

    public static final Integer CODE_29204 = 29204;
    public static final String MESSAGE_29204 = "此[站点]只能分拣[自提订单]";

    public static final Integer CODE_29205 = 29205;
    public static final String MESSAGE_29205 = "[支付方式]错误,不能分拣到自提点";

    public static final Integer CODE_29206 = 29206;
    public static final String MESSAGE_29206 = "此[站点]只能分拣[B商家订单]";

    public static final Integer CODE_29207 = 29207;
    public static final String MESSAGE_29207 = "此[站点]只能分拣[好邻居便利自提订单]";

    public static final Integer CODE_29208 = 29208;
    public static final String MESSAGE_29208 = "此[站点]只能分拣[社区合作自提订单]";

    public static final Integer CODE_29209 = 29209;
    public static final String MESSAGE_29209 = "此[站点]只能分拣[自助式自提柜订单]";

    public static final Integer CODE_29210 = 29210;
    public static final String MESSAGE_29210 = "此[站点]只能分拣[合作自提柜订单]";

    public static final Integer CODE_29211 = 29211;
    public static final String MESSAGE_29211 = "此[站点]只能分拣[合作代收]";

    public static final Integer CODE_29552 = 29552;
    public static final String MESSAGE_29552 = "加盟商运单，请操作称重交接！";


    public static final Integer CODE_29304 = 29304;
    public static final String MESSAGE_29304 = "此[运单]为夺宝岛订单,禁止发往大库";

    public static final Integer CODE_29305 = 29305;
    public static final String MESSAGE_29305 = "此单为[拦截订单],请退货";

    public static final Integer CODE_29306 = 29306;
    public static final String MESSAGE_29306 = "此单为[商家拦截订单],请退货";

    //病单拦截提示信息
    public static final Integer CODE_29308 = 29308;
    public static final String MESSAGE_29308 = "此单为[理赔完成拦截订单],请退就近分拣逆向处置组";

    //病单拦截提示信息
    public static final Integer CODE_29309 = 29309;
    public static final String MESSAGE_29309 = "此单为[理赔完成拦截订单]逆向订单,请暂存";

    //金鹏订单拦截信息
    public static final Integer CODE_29310 = 29310;
    public static final String MESSAGE_29310 = "此单请先暂存，运单集齐后发货";


    public static final Integer CODE_29401 = 29401;
    public static final String MESSAGE_29401 = "此单的[预分拣站点]为空";

    public static final Integer CODE_29402 = 29402;
    public static final String MESSAGE_29402 = "此[分拣中心]的[分拣规则]没有配置";

    public static final Integer CODE_29403 = 29403;
    public static final String MESSAGE_29403 = "该订单没有重量或体积信息,不能装箱";

    public static final Integer CODE_29404 = 29404;
    public static final String MESSAGE_29404 = "请扫描板号/箱号/包裹号进行发货";

    public static final Integer CODE_29405 = 29405;
    public static final String MESSAGE_29405 = "运单无到付运费金额，不能分拣!";

    public static final Integer CODE_29406 = 29406;
    public static final String MESSAGE_29406 = "运单无寄付运费金额，不能分拣!";

    public static final Integer CODE_29407 = 29407;
    public static final String MESSAGE_29407 = "此箱号内为移动仓内配单，需单独建箱，其他类型运单禁止混装";

    public static final Integer CODE_29408 = 29408;
    public static final String MESSAGE_29408 = "此运单为移动仓内配单，需单独建箱，禁止与其他类型运单混装";

    public static final Integer CODE_29409 = 29409;
    public static final String MESSAGE_29409 = "【1】.包裹号录入错误请检查重新录入。【2】.包裹号不存在请使用运单号操作包裹补打，覆盖粘贴原面单。";

    public static final Integer CODE_29412 = 29412;
    public static final String MESSAGE_29412 = "运单中无包裹数据，请确认扫描内容是否正确。联系IT处理";

    public static final Integer CODE_29410 = 29410;
    public static final String MESSAGE_29410 = "运单号:{0}已经转至C网,目前还有{1}共{2}个包裹未操作【包裹补打】进行换单，请操作【包裹补打】更换面单!";

    public static final Integer CODE_29414 = 29414;
    public static final String MESSAGE_29414 = "运单为寄付营业厅运单，未操作揽收完成不允许发货/建箱!";

    public static final Integer CODE_29411 = 29411;
    public static final String MESSAGE_29411 = "运单{0}配送方式或时间变化,共{2}个包裹需换面单，请【包裹补打】换面单!";

    public static final Integer CODE_29415 = 29415;
    public static final String MESSAGE_29415 = "此箱号内为半退单，需单独建箱，其他类型运单禁止混装";

    public static final Integer CODE_29416 = 29416;
    public static final String MESSAGE_29416 = "此运单为半退单，需单独建箱，禁止与其他类型运单混装";

    public static final Integer CODE_29417 = 29417;
    public static final String MESSAGE_29417 = "此箱包裹数量超限，请更换箱号!";
    public static final String MESSAGE_29417_WAYBILL = "集包数量超过上限{0}个，请按包裹操作建箱!";
    public static final String MESSAGE_29417_BOXLIMIT = "集包数量超过上限{0}个，请更换箱号后建箱";

    public static final Integer CODE_29418 = 29418;
    public static final String MESSAGE_29418 = "此单已拒收或妥投，请扫描正确面单或退商家打印正确面单或操作异常处理!";

    public static final Integer CODE_39000 = 39000;
    public static final String MESSAGE_39000 = "此单与分拣[站点]不一致,确定装箱？";
    public static final String MESSAGE_39000_PDA = "注意：包裹/运单与预分拣站点不一致";

    public static final Integer CODE_39001 = 39001;
    public static final String MESSAGE_39001 = "包裹[目的地分拣中心]与扫描装箱箱号不一致,确定装箱？";
    public static final String MESSAGE_39011 = "当前场地到目的场地不存在混装箱规则！确定装箱？";
    public static final String MESSAGE_39021 = "跨区校验：当前网点与目的网点不在同一区域！确定装箱？";

    public static final Integer CODE_39002 = 39002;
    public static final String MESSAGE_39002 = "无此包裹或运单信息,确定装箱？";

    public static final Integer CODE_39003 = 39003;
    public static final String MESSAGE_39003 = "此单为[中转站订单],确定装箱？";

    public static final Integer CODE_39004 = 39004;
    public static final String MESSAGE_39004 = "此[站点]为[中转站],确定装箱？";

    public static final Integer CODE_39005 = 39005;
    public static final String MESSAGE_39005 = "此包裹不可混装入此箱,确定装箱？";

    public static final Integer CODE_39116 = 39116;
    public static final String MESSAGE_39116 = "此单为[自助式自提柜订单],确定装箱？";

    public static final Integer CODE_39118 = 39118;
    public static final String MESSAGE_39118 = "此单为[速递中心订单],确定装箱？";

    public static final Integer CODE_39120 = 39120;
    public static final String MESSAGE_39120 = "[包裹]与[库房号]不一致,确定装箱？";

    public static final Integer CODE_39121 = 39121;
    public static final String MESSAGE_39121 = "[包裹]与[箱号]的承运类型不一致,确定装箱？";
    public static final String MESSAGE_39121_1 = "当前箱号为航空箱,非航空订单,确定装箱?";

    public static final Integer CODE_WAYBILL_SUPER_AREA =29999;
    public static final String MESSAGE_WAYBILL_SUPER_AREA = "超区订单，请联系现场调度操作现场预分拣";

    public static final Integer CODE_IS_MUST_PER_SORITNG_SITE =29998;
    public static final String MESSAGE_IS_MUST_PER_SORITNG_SITE = "此单预分拣站点与发货目的地不一致，操作拦截";

    public static final Integer CODE_39122 = 39122;
    public static final String MESSAGE_39122 = "订单有包裹没有称重信息";

    public static final Integer CODE_WAYBILL_SITE_NULL=39124;
    public static final String MESSAGE_WAYBILL_SITE_NULL ="此订单预分拣站不存在或已关闭,确定装箱";

    public static final Integer CODE_WAYBILL_SITE_NONE=39131;
    public static final String MESSAGE_WAYBILL_SITE_NONE ="此订单无预分拣站点,确定装箱";

    public static final Integer CODE_WAYBILL_SITE_CLOSE=39132;
    public static final String MESSAGE_WAYBILL_SITE_CLOSE ="此订单预分拣站已关闭,确定装箱";

    public static final Integer CODE_39125 = 39125;
    public static final String MESSAGE_39125 = "此单是{0}商家订单,需要退货到{1}库房";

    public static final Integer CODE_39126 = 39126;
    public static final String MESSAGE_39126 = "此单所有商品在本地库房均未配置";

    public static final Integer CODE_39127 = 39127;
    public static final String MESSAGE_39127 = "订单需退库房 {0}";

    public static final Integer CODE_39128 = 39128;
    public static final String MESSAGE_39128 = "该订单没有重量或体积信息，确定装箱？";

    public static final Integer CODE_39129 = 39129;
    public static final String MESSAGE_39129 = "此单为[合作自提柜订单],确定装箱？";

    public static final Integer CODE_39130 = 39130;
    public static final String MESSAGE_39130 = "此单为[合作代收],确定装箱？";

    public static final Integer CODE_WAYBILL_ERROR_ORGID = 10001;
    public static final Integer CODE_39123=39123;
    public static final String MESSAGE_39123="订单信息变更,请补打包裹标签,是否继续分拣";
    public static final String WAYBILL_ERROR_WAYBILLSIGN = "运单-运单标示为空,提交IT或咚咚：xnpsxt";

    public static final Integer CODE_39133 = 39133;
    public static final String MESSAGE_DMS_TO_VENDOR_ERROR = "不允许转网，是否强制操作？";

    public static final Integer CODE_39134 = 39134;
    public static final String MESSAGE_39134 = "此运单面单信息有变动禁止建箱，请操作包裹补打更换粘贴新面单!";

    public static final Integer CODE_39135 = 39135;
    public static final String MESSAGE_39135 = "此运单面单信息有变动，请操作包裹补打更换粘贴新面单!";

    public static final Integer CODE_B_TRANSPORT_C = 39136;
    public static final String MESSAGE_B_TRANSPORT_C= "此运单已转网，请操作包裹补打更换粘贴新面单!";

    public static final Integer CODE_C_TRANSPORT_B = 39137;
    public static final String MESSAGE_C_TRANSPORT_B= "此运单已转网，请操作包裹补打更换粘贴新面单!";

    public static final String WAYBILL_ERROR_ORGID = "运单-机构为空，提交IT或咚咚：xnpsxt";

    public static final Integer CODE_WAYBILL_ERROR_WAYBILLCODE = 10002;
    public static final String WAYBILL_ERROR_WAYBILLCODE = "运单-运单编码为空,提交IT或咚咚：xnpsxt";

    public static final Integer CODE_WAYBILL_ERROR_SITECODE = 10003;
    public static final String WAYBILL_ERROR_SITECODE = "运单-站点编码为空";

    public static final Integer CODE_WAYBILL_ERROR_SITENAME = 10004;
    public static final String WAYBILL_ERROR_SITENAME = "运单-站点名称为空";

    public static final Integer CODE_WAYBILL_ERROR_PAYMENTTYPE = 10005;
    public static final String WAYBILL_ERROR_PAYMENTTYPE = "支付类型为空，联系IT咚咚：XNPSXT处理";

    public static final Integer CODE_WAYBILL_ERROR_SENDPAY = 10006;
    public static final String WAYBILL_ERROR_SENDPAY = "特殊属性为空，联系IT咚咚：XNPSXT处理";


    public static final Integer CODE_WAYBILL_ERROR_TYPE = 10007;
    public static final String WAYBILL_ERROR_TYPE = "运单类型为空，联系IT咚咚：XNPSXT处理";

    public static final Integer CODE_10008=10008;
    public static final String MESSAGE_WAYBILL_SPS_RECEIVED="备件库已收货！";

    public static final Integer CODE_10009=10009;
    public static final String MESSAGE_WAYBILL_STOREHOUSE_RECEIVED="仓储已收货！";

    public static final Integer CODE_10010 =10010;
    public static final String MESSAGE_WAYBILL_WHOLE_LOSS="整单报损不可退大库！";

    public static final Integer CODE_10011 =10011;
    public static final String MESSAGE_WAYBILL_HAS_LOSS="整单或者部分报损不可退备件库！";

    public static final Integer CODE_DIRECT_PASS=200;
    public static final String WAYBILL_DIRECT_PASS = "OK";

    public static final Integer CODE_B_TO_C_29311 = 29311;
    public static final String MESSAGE_B_TO_C_29311 = "此单为B网转C网订单，请操作补打面单";

    public static final Integer CODE_C_TO_B_29312 = 29312;
    public static final String MESSAGE_C_TO_B_29312 = "此单为C网转B网订单，请操作补打面单";

    public static final Integer CODE_C2C_SPWMS_29317 = 29317;
    public static final String MESSAGE_C2C_SPWMS_29317 = "此单备件库已收货，禁止继续操作！";

    public static final String CODE_SiteType_BIANMINZITI="48";
    public static final String CODE_SiteType_DaiShou="46";

    //
    public static final Integer CODE_CROUTER_ERROR =40007;
    public static final String MESSAGE_CROUTER_ERROR="批次目的地与运单路由不一致，是否继续？";
    public static final String MESSAGE_BOARD_ROUTER_ERROR="组板目的地与运单路由不一致，是否继续？";
    public static final String MESSAGE_BOARD_ROUTER_EMPTY_ERROR="未查询到路由请核查是否错发，是否强制组板？";
    public static final String MESSAGE_BOARD_ERROR="组板目的地与此订单预分拣站点不一致，是否继续？";

    public static final Integer MESSAGE_SHOW_TYPE_TIP = 1;
    public static final Integer MESSAGE_SHOW_TYPE_CONFIRM = 2;
    public static final Integer MESSAGE_SHOW_TYPE_INTERCEPT = 3;


    /** 单纯提示语 **/
    List<String> tipMessages;

    /**
     * 提示语展示类型
     * 1：需要要用户确认
     * 2：强制拦截
     * 3：提醒用户
     */
    Integer messageShowType;

    public SortingResponse(JdResponse jdResponse) {
        super(jdResponse.getCode(), jdResponse.getMessage());
    }

    public List<String> getTipMessages() {
        return tipMessages;
    }

    public void setTipMessages(List<String> tipMessages) {
        this.tipMessages = tipMessages;
    }

    public Integer getMessageShowType() {
        return messageShowType;
    }

    public void setMessageShowType(Integer messageShowType) {
        this.messageShowType = messageShowType;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return this.packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return this.createTime!=null?(Date)this.createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime!=null?(Date)createTime.clone():null;
    }

    public Integer getUpdateUserCode() {
        return this.updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return this.updateTime!=null?(Date)this.updateTime.clone():null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }

    public Map<String,Integer> getCancelResult() {
		return cancelResult;
	}

	public void setCancelResult(Map<String,Integer> cancelResult) {
		this.cancelResult = cancelResult;
	}

	public SortingResponse() {
        super();
    }

    public SortingResponse(Integer code, String message) {
        super(code, message);
    }

    public SortingResponse(Integer code, String message, Map<String,Integer> cancelResult) {
		super(code, message);
		this.cancelResult = cancelResult;
	}

	@Override
    public String toString() {
        return "SortingResponse [getRequest()=" + this.getRequest() + ", getCode()="
                + this.getCode() + ", getMessage()=" + this.getMessage() + "]";
    }

    public static SortingResponse sortingSended() {
        return new SortingResponse(SortingResponse.CODE_SORTING_SENDED, SortingResponse.MESSAGE_SORTING_SENDED);
    }

    public static SortingResponse sortingInspected() {
        return new SortingResponse(SortingResponse.CODE_SORTING_INSPECTED, SortingResponse.MESSAGE_SORTING_INSPECTED);
    }

    public static SortingResponse waitingProcess() {
        return new SortingResponse(SortingResponse.CODE_SORTING_WAITING_PROCESS,
                SortingResponse.MESSAGE_SORTING_WAITING_PROCESS);
    }

    public static SortingResponse sortingNotFund() {
        return new SortingResponse(SortingResponse.CODE_SORTING_RECORD_NOT_FOUND,
                SortingResponse.MESSAGE_SORTING_RECORD_NOT_FOUND);
    }

    public static SortingResponse paramIsNull() {
        return new SortingResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
    }

    public static SortingResponse paramIsError() {
        return new SortingResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
    }

    public static SortingResponse waitingCancelProcess() {
        return new SortingResponse(SortingResponse.CODE_SORTING_CANCEL_PROCESS, SortingResponse.MESSAGE_SORTING_CANCEL_PROCESS);
    }

    public static SortingResponse packageNumLimit() {
        return new SortingResponse(SortingResponse.CODE_PACKAGE_NUM_LIMIT, SortingResponse.MESSAGE_PACKAGE_NUM_LIMIT);
    }


    public static SortingResponse ok() {
        return new SortingResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    public static SortingResponse exeError(){
        return new SortingResponse(SortingResponse.CODE_EXE_ERROR,SortingResponse.MESSAGE_EXE_ERROR);
    }

}
