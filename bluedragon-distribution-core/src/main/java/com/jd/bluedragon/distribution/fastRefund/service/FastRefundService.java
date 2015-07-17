package com.jd.bluedragon.distribution.fastRefund.service;

import com.jd.bluedragon.distribution.api.request.FastRefundRequest;

public interface FastRefundService {
	
	/**
	 * 操作成功
	 */
	public static final String SUCCESS = "SUCCESS";
	
	/**
	 * 先货，操作成功
	 */
	public static final String SUCCESS_GOODS = "GOODS";
	
	/**
	 * 参数错误
	 */
	public static final String FAIL_GOODS_PARAM = "GOODS_PARAM";
	/**
	 * 重复提交
	 */
	public static final String FAIL_GOODS_REPEAT = "GOODS_REPEAT";
	/**
	 * 服务异常
	 */
	public static final String FAIL_GOODS_ERROR = "GOODS_ERROR";
	
	/**
	 * 先款，操作成功
	 */
	public static final String SUCCESS_MONEY = "MONEY";
	
	/**
	 * 失败
	 */
	public static final String FAIL_MONEY = "FAIL_MONEY";
	
	/**
	 * 服务异常
	 */
	public static final String FAIL_MONEY_ERROR = "MONEY_ERROR";
	
	/**
	 * 其他类型订单，不做处理
	 */
	public static final String OTHER = "OTHER";
	
	/**
	 * 运单为空
	 */
	public static final String WAYBILL_NOT_FIND = "WAYBILL_NOT_FIND";
	
	
	/**
	 * 取消订单
	 */
	public static final String WAYBILL_IS_CANCEL = "WAYBILL_IS_CANCEL";
	
	/**
	 * 服务异常
	 */
	public static final String ERROR = "ERROR";

	public String execRefund(String waybillCode) throws Exception;
	public String execRefund(FastRefundRequest fastRefundRequest) throws Exception;
	

}
