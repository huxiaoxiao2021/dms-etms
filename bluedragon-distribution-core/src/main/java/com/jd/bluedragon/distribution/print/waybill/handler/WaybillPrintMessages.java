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
	public static final JdMessage FAIL_MESSAGE_WEIGHT = new JdMessage(41001003,"包裹%s超限！");
	public static final JdMessage WARN_MESSAGE = new JdMessage(21001001,"预分拣站点变更，务必重新打印包裹！");
}
