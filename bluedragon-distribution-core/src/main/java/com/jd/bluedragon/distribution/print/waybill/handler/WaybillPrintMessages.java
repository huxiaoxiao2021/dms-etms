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
	public static final JdMessage FAIL_MESSAGE_WAYBILL_NULL = new JdMessage(41001001,"运单/包裹信息为空，请咚咚联系：org.wlxt2 处理");
	public static final JdMessage FAIL_MESSAGE_GET_NEW_WAYBILL_NULL = new JdMessage(41001002,"此单无换单操作，请咚咚联系：org.wlxt2 处理");
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
	 * 该运单已是终结点时提示信息错误码
	 * */
	public static final Integer CODE_WAYBILL_FINISHED = 21001003;
	/**
	 * 该运单已是终结点时提示信息
	 * */
	public static final String MESSAGE_WAYBILL_FINISHED = "此订单号（外单包裹号）已【%s】，是否再次打印？";
	/**
	 * 该运单已是终结点并且是补打时错误码
	 * */
	public static final Integer CODE_WAYBILL_FINISHED_REPRINT = 21001004;
	/**
	 * 该运单已是终结点并且是补打时提示信息
	 * */
	public static final String MESSAGE_WAYBILL_FINISHED_REPRINT = "当前面单已【%s】状态，且已操作过补打，请确认是否打印";
	/**
	 * B2C&C2C寄付现结的单子禁止打印
	 */
	public static final String MESSAGE_B2C_C2C_PREPAID_INTERCEPT="现结订单，请在揽收端完成称重！";
	/**
	 * 该运单未揽收完成时提示信息
	 */
	public static final String MESSAGE_NEED_COLLECT_FINISHED="此运单未揽收完成禁止打印面单，请揽收";

	public static final String MESSAGE_WAYBILL_FINISHED_INTERCEPT = "【此单已%s】，禁止包裹补打";

	public static final String MESSAGE_WAYBILL_FIRST_PRINT_INTERCEPT = "该单尚未完成首次打印，请使用站点平台打印功能，或者使用站长工作台-任务中心-分拣打印-包裹打印功能，进行打印，谢谢！";

    public static final String MESSAGE_PACKAGE_PRINTED = "订单标签已打印，如需打印请操作包裹补打或去站长工作台操作包裹打印";

}
