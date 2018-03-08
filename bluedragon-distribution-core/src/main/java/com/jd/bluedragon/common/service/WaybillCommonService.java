package com.jd.bluedragon.common.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;


/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-9-27 上午11:21:27
 *
 * 获取运单信息公共服务
 */
public interface WaybillCommonService {
	//b2b快运 运输产品类型
	enum ExpressTypeEnum{
		EXPRESS_PART_LOAD('2',"快运零担"),
		WAREHOUSE_PART_LOAD('3',"仓配零担"),
		CAR_LOAD('1',"整车");
		private char code;
		private String name;

		ExpressTypeEnum(char code, String name) {
			this.code = code;
			this.name = name;
		}
		public static String getNameByCode(char code){
			for(ExpressTypeEnum et : ExpressTypeEnum.values()){
				if(et.code == code){
					return et.name;
				}
			}
			return "";
		}
	}

	/**
     * 根据运单号查询运单明细
     * 	先调用运单，运单获取不到数据，调用订单中间件
     * 
     * @param waybillCode
     * @return
     */
    public Waybill findByWaybillCode(String waybillCode);
    
    /**
     * 根据运单号查询运单明细
     * 	直接调用运单接口查询运单数据及包裹信息（验证必要字段）
     * 
     * @param waybillCode
     * @return
     */
    public Waybill findWaybillAndPack(String waybillCode);
    
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
	 * 获取包裹称重数据
	 * @param waybillCode 运单号
	 * @return
	 */
	InvokeResult<List<PackageWeigh>> getPackListByCode(String waybillCode);
}
