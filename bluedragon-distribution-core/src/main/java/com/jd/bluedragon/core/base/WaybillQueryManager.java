package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.SkuSn;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExtPro;
import com.jd.etms.waybill.dto.*;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;

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
	 * 根据运单号查询产品能力信息
	 * https://cf.jd.com/pages/viewpage.action?pageId=506496819
	 * @param waybillCode 运单号
	 * @return
	 */
	BaseEntity<List<WaybillProductDto>> getProductAbilityInfoByWaybillCode(String waybillCode);

	/**
	 * 校验产品能力是否存在
	 * @param waybillProductDtos 产品能力列表
	 * @param productAbility 产品能力指
	 * @return
	 */
	boolean checkWaybillProductAbility(List<WaybillProductDto> waybillProductDtos,String productAbility);

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
	 * 根据运单号获取运单基本信息（轻量）
	 */
	Waybill queryWaybillByWaybillCode(String waybillCode);

    /**
	 * 根据运单号获取运单信息
	 * @param waybillCode
	 * @return
	 */
	Waybill getOnlyWaybillByWaybillCode(String waybillCode);

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

	/*
	 *
	 * 根据运单号获取商家ID
	 *
	 * */
	Integer getBusiId(String waybillCode);

    /**
     * 根据运单号查询运单增值服务信息
     * @param waybillCode
     * @return
     */
    BaseEntity<List<WaybillVasDto>> getWaybillVasInfosByWaybillCode(String waybillCode);
    /**
     * 根据运单号查询出关联的原单和返单单号
     * @param waybillCode
     * @return
     */
	JdResult<List<String>> getOriginalAndReturnWaybillCodes(String waybillCode);

	/**
	 * 查询附件、图片等，需根据对应附件类型查询
	 * @param waybill
	 * @param attachmentType
	 */
	public  BaseEntity<List<WaybillAttachmentDto>> getWaybillAttachmentByWaybillCodeAndType(String waybill,
																							Integer attachmentType);

	/**
	 * 根据运单号，包裹号获取包裹增值信息
	 * @param wayBillCode
	 * @return
	 */
	Map<String,String> doGetPackageVasInfo(String wayBillCode);

	/**
	 * 根据运单号,包裹号获取包裹维度商品名称
	 */
	Map<String,String> doGetPackageGoodsVasInfo(String wayBillCode);

	/**
	 * 根据运单信息获取运输产品类型
	 * 判断waybill_sign,按以下规则设置产品名称
	 序号	标记位	   产品名称	面单打印形式
	 1	55位=0且31位=5	微小件	特惠送
	 2	55位=0且31位=B	函速达	函速达
	 3	31=7且29位=8	特瞬送同城	极速达
	 4	55位=0且31位=7且29位=8	同城速配	同城速配
	 5	31=0	特惠送	特惠送
	 6	55位=0且31位=3	特瞬送城际	城际即日
	 7	55位=0且31位=A	生鲜特惠	生鲜特惠
	 8	55位=0且31位=9	生鲜特快	生鲜特快
	 9	55位=1且84位=3代表航空 55位=1且84位≠3代表非航空	生鲜专送	生鲜专送
	 10	（55位=0且31位=1且116位=0）或（55位=0且31位=1且116位=1）	特快送	特快送
	 11	（55位=0且31位=2且16位=1,2,3,7,8）或（55位=0且31位=1且116位=2）	特快送"同城即日"	特快送即日
	 12	55位=0且31位=4或55位=0且31位=1且116位=3	特快送“特快次晨”	特快送次晨
	 13	31=6	京准达+其他主产品	京准达
	 14	31=6	京准达	京准达
	 15	55位=0且31位=1且116位=4	特快陆运	特快送
	 16	55位=0且31位=C	特惠小包	特惠包裹
	 * @param waybill
	 * @return
	 */
	String getTransportMode(Waybill waybill);

	/**
	 * 根据运单信息获取托寄物名称
	 * @param bigWaybillDto
	 * @return
	 */
	String getConsignmentNameByWaybillDto(BigWaybillDto bigWaybillDto);

}
