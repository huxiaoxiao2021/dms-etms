package com.jd.bluedragon.common.service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.domain.WaybillErrorDomain;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WaybillVasDto;

import java.util.List;
import java.util.Map;


/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-9-27 上午11:21:27
 *
 * 获取运单信息公共服务
 */
public interface WaybillCommonService {
	/**
     * 根据运单号查询运单明细（不包括包裹信息）
     *
     * @param waybillCode
     * @return
     */
    public Waybill findByWaybillCode(String waybillCode);


	/**
	 * 根据运单号查询订单号
	 *
	 * 如果输入的是数字则直接返回，认为输入的就是订单号。
	 *
	 * @param waybillCode
	 * @return
	 */
	public Long findOrderIdByWaybillCode(String waybillCode);
    /**
     * 根据运单号查询运单明细
     * 	直接调用运单接口查询运单数据及包裹信息（验证必要字段）
     *
     * @param waybillCode
     * @return
     */
    public Waybill findWaybillAndPack(String waybillCode);

	/**
	 * 补全异常运单信息
	 * @param waybillCodeC 正确运单号
	 * @param WaybillCodeE 错误运单号(运单生成的重复运单号)
	 * @return
	 */
	public List<WaybillErrorDomain> complementWaybillError(String waybillCodeC, String waybillCodeE);


	public List<WaybillErrorDomain> complementWaybillError(String waybillCodeC);

    /**
     * 根据运单号查询运单信息
     * 	调用运单中间件
     *
     * @param waybillCode
     * @return
     */
    public Waybill getWaybillFromOrderService(String waybillCode);

    /**
     * 根据运单号查询历史运单信息
     * 	调用运单中间件
     *
     * @param waybillCode
     * @return
     */
    public Waybill getHisWaybillFromOrderService(String waybillCode);


    /**
     * 根据运单号查询运单明细
     * 	直接调用运单接口查询运单数据及包裹信息（验证必要字段）
     *
	 * @param waybillCode 运单号
	 * @param isQueryWaybillC
	 * @param QueryWaybillE
	 * @param QueryWaybillM
	 * @param isQueryPackList 获得包裹数据
	 * @return
	 */
	Waybill findWaybillAndPack(String waybillCode, boolean isQueryWaybillC,
			boolean QueryWaybillE, boolean QueryWaybillM,
			boolean isQueryPackList);


    /**
     * 根据原单号查询新运单信息
     * @param oldWaybillCode 原运单号
     * @return
     */
    InvokeResult<Waybill> getReverseWaybill(String oldWaybillCode);

	/**
	 *  根据运单号查询运单及商品明细
	 * 	直接调用运单接口查询运单数据及商品信息
	 *
	 * @param waybillCode
	 * @return
	 */
	public Waybill findWaybillAndGoods(String waybillCode);

	/**
	 * 根据运单号和操作类型，获取运单称重流水,运单号为空/opeType为空直接返回空Map
	 * @param waybillCode
	 * @param opeType
	 * @return 以包裹号为key的map
	 */
	public Map<String,PackOpeFlowDto> getPackOpeFlowsByOpeType(String waybillCode,Integer opeType);
    /**
     * 通过运单对象，设置基础打印信息
     * <p>设置商家id和name(busiId、busiName)
	 * <p>设置配送方式deliveryMethod
	 * <p>以始发分拣中心获取始发城市code和名称(originalCityCode、originalCityName)
     * <p>设置寄件人、电话、手机号、地址信息(consigner、consignerTel、consignerMobile、consignerAddress)
     * <p>设置设置价格保护标识和显示值：(priceProtectFlag、priceProtectText)
     * <p>设置打标信息：签单返还、配送类型、运输产品(signBackText、distributTypeText、transportMode)
     * @param target 目标对象(BasePrintWaybill类型)
     * @param waybill 原始运单对象
     */
	BasePrintWaybill setBasePrintInfoByWaybill(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill);
	/**
	 * 将运单数据包装成自己的waybill数据
	 * @param bigWaybillDto
	 * @param isSetName
	 * @param isSetPack
	 * @return
	 */
	Waybill convWaybillWS(BigWaybillDto bigWaybillDto, boolean isSetName, boolean isSetPack);

	/**
	 * 将运单数据包装成自己的waybill数据
	 * @param bigWaybillDto
	 * @param isSetName 设置站点信息
	 * @param isSetPack 设置包裹信息
	 * @param loadPrintInfo 是否加载包裹打印信息
	 * @param loadPweight 是否加载复重
	 * @return
	 */
	Waybill convWaybillWS(BigWaybillDto bigWaybillDto, boolean isSetName, boolean isSetPack,
			boolean loadPrintInfo,boolean loadPweight);

	/**
	 * 获取包裹称重数据
	 * @param waybillCode 运单号
	 * @return
	 */
	InvokeResult<List<PackageWeigh>> getPackListByCode(String waybillCode);
	/**
	 * 校验运单是否录入运单总重量
	 * @param waybillCode 运单号
	 * @return
	 */
	boolean hasTotalWeight(String waybillCode);

    BaseEntity<PickupTask> getPickupTask(String oldWaybillCode);

	/**
	 * 通过运单号获取履约单号
	 * @param waybillCode
	 * @return 运单不存在时返回null
	 */
	String getPerformanceCode(String waybillCode);

	/**
	 * 此运单是否为加履中心订单
	 * @param waybillCode
	 * @return
	 */
	boolean isPerformanceWaybill(String waybillCode);

    /**
     * 是否快运暂存运单
     * @param waybillCode
     * @return
     */
    boolean isStorageWaybill(String waybillCode);

	/**
	 * 	加载面单中的特殊需求字段
	 * @param printWaybill
	 * @param waybillSign
	 */
	void loadSpecialRequirement(BasePrintWaybill printWaybill,String waybillSign,com.jd.etms.waybill.domain.Waybill waybill);

	/**
	 * 	加载始发站点信息
	 * @param printWaybill
	 * @param bigWaybillDto
	 */
	void loadOriginalDmsInfo(WaybillPrintContext context,BasePrintWaybill printWaybill, BigWaybillDto bigWaybillDto);

	/**
	 * B网面单获取路由
	 * @param request
	 * @param printWaybill
	 * @param originalDmsCode
	 * @param destinationDmsCode
	 * @param waybillSign
	 */
	void loadWaybillRouter(WaybillPrintRequest request,BasePrintWaybill printWaybill, Integer originalDmsCode, Integer destinationDmsCode, String waybillSign);

	/**
	 * 通过运单号获取包裹数量
	 * @param waybillCode
	 * @return
	 */
	InvokeResult<Integer> getPackNum(String waybillCode);

	/**
	 * 修改取件单换单后新单的包裹数
	 * @param waybillCode 新单号
	 * @param packNum 新单包裹数量
	 * @return
	 */
	InvokeResult batchUpdatePackageByWaybillCode(String waybillCode, Integer packNum);

	/**
	 * 校验包裹是否存在
	 * @param packCode
	 * @return
	 */
	boolean checkPackExist(String packCode);

	/**
	 * 根据运单号获得增值服务列表
	 * @param waybillCode
	 * @return
	 */
	List<WaybillVasDto> getWaybillVasList(String waybillCode);

	/**
	 * 针对航空/航填面单是否匹配滑道获取逻辑
	 * @param waybillSign
	 * @param sendPay
	 * @param prepareSiteCode 预分拣派送站点
	 * @param dmsSiteCode 当前操作分拣中心场地编码
	 * @param waybillCode 运单号
	 * @return
	 */
	boolean isMatchGetCrossOfAir(String waybillSign, String sendPay,Integer prepareSiteCode,Integer dmsSiteCode,String waybillCode);



	/**
	 * 获取预分拣站点id
	 * @param waybillCode
	 * @return
	 */
	InvokeResult<Integer> fetchOldSiteId(String waybillCode);
    /**
     * 查询增值服务
     * @param waybillCode 运单号
     * @param waybillVasEnum 自定义增值服务枚举
     * @return 是否包含增值服务
     * @author fanggang7
     * @time 2023-08-10 10:55:02 周四
     */
    Result<Boolean> checkWaybillVas(String waybillCode, WaybillVasEnum waybillVasEnum);

    /**
     * 查询增值服务
     * @param waybillCode 运单号
     * @param waybillVasEnum 自定义增值服务枚举
     * @return 是否包含增值服务
     * @author fanggang7
     * @time 2023-08-10 10:55:02 周四
     */
    Result<Boolean> checkWaybillVas(String waybillCode, WaybillVasEnum waybillVasEnum, List<WaybillVasDto> waybillVasDtoList);
}
