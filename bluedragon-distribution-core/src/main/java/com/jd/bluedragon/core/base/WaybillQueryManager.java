package com.jd.bluedragon.core.base;

import java.util.Date;

import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;

public interface WaybillQueryManager{
	
	/**
	 * 反调度操作—操作反调度
	 */
	public static final Integer REDISPATCH_YES = 1;
	
	/**
	 * 反调度操作—未操作反调度
	 */
	public static final Integer REDISPATCH_NO = 2;
	
	/**
	 * 反调度操作—查询异常
	 */
	public static final Integer REDISPATCH_ERROR = 3;
	

	public abstract BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode, WChoice  wChoice);

	/**
	 * 
	 * @param waybillCode 运单号
	 * @param isWaybillC 是否查询运单基本信息
	 * @param isWaybillE 是否查询运单扩展信息
	 * @param isWaybillM 是否查询运单管理表信息
	 * @param isPackList 是否查询运单下的包裹列表
	 * @return
	 */
	public abstract BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode, Boolean isWaybillC,
			Boolean isWaybillE, Boolean isWaybillM, Boolean isPackList);


	/**
	 * @param waybillCode 运单号
	 * @param isWaybillC 是否查询运单基本信息
	 * @param isWaybillE 是否查询运单扩展信息
	 * @param isWaybillM 是否查询运单管理表信息
	 * @param isGoodList 是否查询运单下的商品列表
	 * @param isPackList 是否查询运单下的包裹列表
	 * @param isPickupTask 是否查询取件单信息
	 * @param isServiceBillPay 是否查询运单服务费信息
	 * @return
	 */
	public abstract BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM,
			Boolean isGoodList, Boolean isPackList, Boolean isPickupTask,
			Boolean isServiceBillPay);

	/**
	 * 发送全程跟踪消息
	 * @param businessKey 业务主键 如订单号 取件单号
	 * @param msgType 消息类型
	 * @param title 消息主题
	 * @param content 消息内容
	 * @param operatorName 操作人
	 * @param operateTime 操作时间
	 * @return 发送成功与否
	 */
	public abstract boolean sendOrderTrace(String businessKey, int msgType,
			String title, String content, String operatorName, Date operateTime);

	/**
	 * 发送全程跟踪消息
	 * @param bdTraceDto
	 * @return
	 */
	public abstract boolean sendBdTrace(BdTraceDto bdTraceDto);

	/**
	 * 根据扫描单号获得换单前单号,主要用于逆向
	 * @param waybillCode
	 * @return
	 */
	public BaseEntity<Waybill> getWaybillByReturnWaybillCode(String waybillCode);

	/**
	 * 适配,查询包裹是否进行过站点发调度操作
	 * @param packageCode
	 * @return Integer
	 */
	public Integer checkReDispatch(String packageCode);

	/**
	 * 获取取件单对应的面单号W单
	 * @param oldWaybillCode
	 * @return
	 */
	public String getChangeWaybillCode(String oldWaybillCode);
}
