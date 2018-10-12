package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * 运单反调度状态
	 */
	public static final String WAYBILL_STATUS_REDISPATCH = "140";
	

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
	 * 批量获取运单信息
	 *
	 * @param waybillCodes 运单号列表
	 * @param isWaybillC   是否查询运单基本信息
	 * @param isWaybillE   是否查询运单扩展信息
	 * @param isWaybillM   是否查询运单管理表信息
	 * @param isPackList   是否查询运单下的包裹列表
	 * @return
	 */
	BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> waybillCodes, Boolean isWaybillC,
													 Boolean isWaybillE, Boolean isWaybillM, Boolean isPackList);

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
	 * @param waybillCode
	 * @return Integer
	 */
	public Integer checkReDispatch(String waybillCode);

	/**
	 * 获取取件单对应的面单号W单
	 * @param oldWaybillCode
	 * @return
	 */
	public String getChangeWaybillCode(String oldWaybillCode);

	/**
	 * 根据旧运单号获取新运单信息
	 *
	 * @param waybillCode 运单号
	 * @param queryC 获取的运单信息中是否包含waybillC数据
	 * @param queryE 获取的运单信息中是否包含waybillE数据
	 * @param queryM 获取的运单信息中是否包含waybillM数据
	 * @param queryPackList 获取的运单信息中是否包含PackList数据
	 * @return
	 */
	BigWaybillDto getReturnWaybillByOldWaybillCode(String waybillCode, boolean queryC, boolean queryE, boolean queryM, boolean queryPackList);
    /**
     * 包裹称重和体积测量数据上传
     * 来源 PackOpeController
     *
     * @param packOpeJson 称重和体积测量信息
     * @return map data:true or false,code:-1:参数非法 -3:服务端内部处理异常 1:处理成功,message:code对应描述
     */
    public Map<String, Object> uploadOpe(String packOpeJson);
	/**
	 * 根据运单号获取运单数据信息给打印用
	 * @param waybillCode
	 * @return
	 */
	BaseEntity<BigWaybillDto> getWaybillDataForPrint(String waybillCode);

	/**
	 * 根据操作单号和状态查询B网全程跟踪数据,包含extend扩展属性
	 * @param operatorCode 运单号
	 * @param state 状态码
	 * @return
	 */
	List<BillBusinessTraceAndExtendDTO> queryBillBTraceAndExtendByOperatorCode(String operatorCode, String state);

	/**
	 * 根据旧单号获取新单号
	 * @param dto 旧单号对象
	 * @return
	 */
	ResponseDTO<ReturnSignatureResult> waybillReturnSignature(WaybillReturnSignatureDTO dto);

	/**
	 * 根据运单号获得运单信息
	 * @param waybillCode 运单号
	 * @return
	 */
	ResponseDTO<ReturnSignatureMessageDTO> queryReturnSignatureMessage(String waybillCode);
}
