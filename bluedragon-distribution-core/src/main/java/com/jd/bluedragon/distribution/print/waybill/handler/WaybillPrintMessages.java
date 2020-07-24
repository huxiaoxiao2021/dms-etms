package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdMessage;

/**
 * 
 * @ClassName: JdMessage
 * @Description: 消息类型
 * @author: wuyoude
 * @date: 2018年1月28日 下午1:42:10
 *
 */
public class WaybillPrintMessages{
	public static final JdMessage FAIL_MESSAGE_WAYBILL_NULL = new JdMessage(41001001,"运单/包裹信息为空，请联系IT人员处理！");
	public static final JdMessage FAIL_MESSAGE_GET_NEW_WAYBILL_NULL = new JdMessage(41001002,"此单无换单操作！");
	/**
	 * 三方超限提示信息
	 */
	public static final JdMessage FAIL_MESSAGE_THIRD_OVERRUN = new JdMessage(41001003,"包裹%s超出三方站点配送范围无法配送，%s，%s！");
	public static final JdMessage WARN_MESSAGE = new JdMessage(21001001,"预分拣站点变更，务必重新打印包裹！");

	/**
	 * 三方超限-平台打印提示语
	 */
	public static final String FAIL_MESSAGE_THIRD_OVERRUN_PLATE_PRINT = "请提交异常后操作逆向换单返回商家";

	/**
	 * 三方超限-站点平台打印提示语
	 */
	public static final String FAIL_MESSAGE_THIRD_OVERRUN_SITE_PLATE_PRINT = "请操作揽收退回后返回商家";


	/**
	 * 信任商家提示信息
	 * */
	public static final JdMessage WARN_MESSAGE_TRUST_BUSINESS = new JdMessage(21001002,"%s为防止认为操作错误导致误差过大，请重新称重量方。");
	/**
	 * c2c判断揽收
	 */
	public static final String MESSAGE_NEED_RECEIVE="此运单未揽收/交接完成禁止打印面单，请先操作揽收/交接";

	public static final String MESSAGE_WAYBILL_STATE_FINISHED = "此运单为妥投状态，禁止操作此功能，请检查单号是否正确";

	/**
	 * 站点操作发货
	 * */
	public static final String MESSAGE_WAYBILL_STATE_SEND_BY_SITE = "此箱号已经操作站点发货，无法继续使用，请更换箱号";

	/**
	 * 超长包裹
	 * */
	public static final String MESSAGE_PACKAGE_OVER_LENGTH_REMIND = "此包裹超过{0}*{1}*{2}厘米，为超长揽收包裹";
	/**
	 * 该运单已是终结点时提示信息MESASAGE_WAYBILL_FINISHED
	 * */
	public static final JdMessage MESSAGE_WAYBILL_FINISHED = new JdMessage(21001003,"此订单号（外单包裹号）已【%s】，是否再次打印？");
	/**
	 * 该运单已是终结点时提示信息MESASAGE_WAYBILL_FINISHED
	 * */
	public static final JdMessage MESSAGE_WAYBILL_FINISHED_REPRINT = new JdMessage(21001004,"当前面单已【%s】状态，且已操作过补打，请确认是否打印");

}
