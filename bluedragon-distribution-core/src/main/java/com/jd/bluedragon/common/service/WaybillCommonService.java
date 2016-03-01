package com.jd.bluedragon.common.service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;


/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-9-27 上午11:21:27
 *
 * 获取运单信息公共服务
 */
public interface WaybillCommonService {

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
}
