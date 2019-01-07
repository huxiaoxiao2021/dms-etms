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
	public static final JdMessage FAIL_MESSAGE_THIRD_OVERRUN = new JdMessage(41001003,"包裹%s超出三方站点配送范围无法配送，三方最大限额为%s，%s！");
	public static final JdMessage WARN_MESSAGE = new JdMessage(21001001,"预分拣站点变更，务必重新打印包裹！");

	public static final String FAIL_MESSAGE_THIRD_OVERRUN_PLATE_PRINT = "请提交异常后操作逆向换单返回商家";
	public static final String FAIL_MESSAGE_THIRD_OVERRUN_SITE_PLATE_PRINT = "请操作揽收退回后返回商家";


	包裹XXXX（体积/重量/长宽高）超出三方站点配送范围无法配送，三方最大限额为XXXX，请提交异常后操作逆向换单返回商家。


	/**
	 * 信任商家提示信息
	 * */
	public static final JdMessage WARN_MESSAGE_TRUST_BUSINESS = new JdMessage(21001002,"%s为防止认为操作错误导致误差过大，请重新称重量方。");
	/**
	 * c2c判断揽收
	 */
	public static final String MESSAGE_NEED_RECEIVE="此运单未揽收完成禁止打印面单，请先揽收";
}
