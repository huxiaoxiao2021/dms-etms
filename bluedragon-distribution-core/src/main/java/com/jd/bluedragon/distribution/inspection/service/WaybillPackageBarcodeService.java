package com.jd.bluedragon.distribution.inspection.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.etms.waybill.domain.DeliveryPackageD;

/**
 * 运单包裹关联查询Service
 * @author wangzichen
 *
 */
public interface WaybillPackageBarcodeService {
	/**
	 * 通过运单号/包裹号 获得包裹信息以及运单的客户地址
	 * @param code
	 * @param receiveSiteCode 
	 */
	public WaybillResponse getWaybillPackageBarcode(String code,Integer siteCode, Integer receiveSiteCode);

	/**
	 * 通过运单号获得所有包裹
	 * @param waybillCode
	 */
	List<DeliveryPackageD> getPackageBarcodeByWaybillCode(String waybillCode);
	
	/**
	 * 批量通过运单号获取包裹号
	 * @param waybillCodes
	 */
	Map<String, List<DeliveryPackageD>> getPackageBarcodeByWaybillCodeBatch(List<String> waybillCodes);

	/**
	 * 通过运单号获取运单下的包裹数量
	 * @param waybillCode
	 * @return
	 */
	Integer getPackageNumbersByWaybill(String waybillCode);

	/**
	 * 通过运单号获取所有的包裹号列表
	 * @param waybillCode
	 * @return
	 */
	List<String> getPackageCodeListByWaybillCode(String waybillCode);
}
