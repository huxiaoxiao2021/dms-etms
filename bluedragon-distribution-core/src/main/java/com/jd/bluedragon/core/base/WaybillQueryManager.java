package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.SkuSn;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExtPro;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.SkuPackRelationDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;

import java.util.List;
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

	/**
	 * 根据运单号获取运单数据，无大包裹运单数据缓存和包裹信息分页查询(慎用)
	 *
	 * @param waybillCode
	 * @param wChoice
	 * @return
	 */
	BaseEntity<BigWaybillDto> getDataByChoiceNoCache(String waybillCode, WChoice wChoice);

	BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode, WChoice  wChoice);

	/**
	 * 
	 * @param waybillCode 运单号
	 * @param isWaybillC 是否查询运单基本信息
	 * @param isWaybillE 是否查询运单扩展信息
	 * @param isWaybillM 是否查询运单管理表信息
	 * @param isPackList 是否查询运单下的包裹列表
	 * @return
	 */
	BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode, Boolean isWaybillC,
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
	BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
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
	 * 批量获取运单信息
	 *
	 * @param waybillCodes 运单号列表
	 * @return
	 */
	BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> waybillCodes,WChoice wChoice);

	/**
	 * 发送全程跟踪消息
	 * @param bdTraceDto
	 * @return
	 */
	boolean sendBdTrace(BdTraceDto bdTraceDto);

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
	 * @param oldWaybillCode 旧的运单号
	 * @param wChoice 获取的运单信息中是否包含waybillC数据
	 * @return
	 */
	BaseEntity<BigWaybillDto> getReturnWaybillByOldWaybillCode(String oldWaybillCode, WChoice wChoice);

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
	 * 根据包裹号获取运单信息
	 * @param code
	 * @return
	 */
	BaseEntity<Waybill> getWaybillByPackCode(String code);

	/**
	 * 根据运单号获取运单信息和包裹信息
	 * @param waybillCode
	 * @return
	 */
	BaseEntity<BigWaybillDto> getWaybillAndPackByWaybillCode(String waybillCode);

	/**
	 * 根据运单号获取运单信息
	 * @param waybillCode
	 * @return
	 */
	BaseEntity<Waybill> getWaybillByWaybillCode(String waybillCode);

    Waybill getWaybillByWayCode(String waybillCode);

    /**
	 * 根据运单号查询运单sn码和69码
	 *
	 * @param waybillCode
	 * @return
	 */
	BaseEntity<List<SkuSn>> getSkuSnListByOrderId(String waybillCode);

	/**
	 * 根据父订单号（履约单号）查询所有子订单号
	 * @param parentWaybillCode 父订单号（履约单号）
	 * @return
	 */
	List<String> getOrderParentChildList(String parentWaybillCode);

	/**
	 * 根据运单号获取订单号
	 * @param waybillCode
	 * @param  source
	 * source说明：
	 *1.如果waybillCode为正向运单，则直接返回订单号
	 *2.如果waybillCode为返单号，并且source为true时，返回原运单的订单号
	 *3.如果waybillCode为返单号，并且source为false时，返回为空
	 * @return 订单号
	 */
	String getOrderCodeByWaybillCode(String waybillCode, boolean source);

	/**
	 * 根据运单号和属性获取运单扩展属性
	 * @param waybillCodes
	 * @param properties
	 * @return
	 */
	List<WaybillExtPro> getWaybillExtByProperties(List<String> waybillCodes, List<String> properties);

	/**
	 * 根据配送中心ID和仓ID查询是否强制换单
	 *
	 * @param cky2 配送中心ID
	 * @param storeId 仓ID
	 * @return
	 */
	Boolean ifForceCheckByWarehouse(Integer cky2, Integer storeId);

	/**
	 * 查询运单号是否存在
	 * @param waybillCode
	 * @return
	 */
	Boolean queryExist(String waybillCode);

	/**
	 * 根据运单号查询waybillSign
	 * @param waybillCode
	 * @return
	 */
	BaseEntity<String> getWaybillSignByWaybillCode(String waybillCode);

	/**
	 * 查询SKU与包裹的关系
	 * @param sku 商品sku：自营是sku；ECLP是EMG码
	 * @return
     */
	BaseEntity<SkuPackRelationDto> getSkuPackRelation(String sku);

	/**
	 * 修改包裹数量
	 * @param waybillCode
	 * @param list
	 * @return
	 */
	BaseEntity<Boolean> batchUpdatePackageByWaybillCode(String waybillCode,List list);
    /*
     *
     * 查询运单接口获取包裹列表
     *
     * */
    List<DeliveryPackageD> findWaybillPackList(String waybillCode);

}
