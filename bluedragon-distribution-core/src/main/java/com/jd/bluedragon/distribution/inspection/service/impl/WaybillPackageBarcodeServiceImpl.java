package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.PackageResponse;
import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 运单包裹关联查询Service
 * @author wangzichen
 *
 */
@Service
public class WaybillPackageBarcodeServiceImpl implements WaybillPackageBarcodeService{

	/*运单查询*/
	@Autowired
	WaybillQueryManager waybillQueryManager;

	@Autowired
	WaybillPackageManager waybillPackageManager;
	
	private final static Logger logger = Logger.getLogger(WaybillPackageBarcodeServiceImpl.class);
	
	/**
	 * 调用wss调用运单接口核心类
	 * @param waybillCode
	 * @return
	 */
	private BaseEntity<BigWaybillDto> wssByWaybillCode(String waybillCode) {
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryPackList(true);
		BaseEntity<com.jd.etms.waybill.dto.BigWaybillDto> entity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
		
		if(null==entity){
			logger.info(" Waybill wss: 运单接口waybillQueryWSProxy.getDataByChoice ,调用返回空，运单不存在， waybillCode: "+waybillCode);
			return null;
		}
		if(entity.getResultCode()!=1){
			logger.info(" Waybill wss: 运单接口waybillQueryWSProxy.getDataByChoice ,调用返回失败, waybillCode: "+waybillCode+". resultCode code: "+entity.getResultCode()
					+"\n error message: "+entity.getMessage());//print stack trace
			return null;
		}
		if(null==entity.getData() || null==entity.getData().getPackageList()){
			logger.warn(" Waybill wss: 运单接口waybillQueryWSProxy.getDataByChoice , 运单: "+waybillCode + " 无包裹数据");
			return null;
		}
		return entity;
	}
	
	/**
	 * 通过运单号获得所有包裹
	 */
	@Override
	public List<DeliveryPackageD> getPackageBarcodeByWaybillCode(String waybillCode ){
		
		BaseEntity<com.jd.etms.waybill.dto.BigWaybillDto> entity = wssByWaybillCode(waybillCode);
		if(null==entity)	return null;
		
		List<DeliveryPackageD> packages = entity.getData().getPackageList();
		if( null==packages||packages.isEmpty()){ 
			throw new InspectionException("  method : WaybillWS.getWaybillAndPackByWaybillCode ,parameter " + " waybillCode "+waybillCode);
		}else 
			return packages;
	}
	
	/**
	 * 通过运单号/包裹号 获得包裹信息以及运单的客户地址
	 * @param code
	 */
	@Override
	public WaybillResponse getWaybillPackageBarcode(String code,Integer siteCode, Integer receiveSiteCode) {
		
		WaybillResponse waybillResponse = new WaybillResponse();

		//根据规则查出是包裹还是运单
		if(WaybillUtil.isPackageCode(code) || WaybillUtil.isSurfaceCode(code)){//如果是包裹或者取件单则调用getWaybillByPackCode方法查询
			
			waybillResponse = this.wssGetWaybillByPackCode(waybillResponse,code, siteCode, receiveSiteCode);
			
		}else if( WaybillUtil.isWaybillCode(code)){//如果是运单则调用getWaybillAndPackByWaybillCode方法查询

			waybillResponse = this.getWaybillAndPackByWaybillCode(waybillResponse,code,siteCode,receiveSiteCode);
		}else{
			logger.error("  getWaybillPackageBarcode 失败，因为参数code无法解析, code: "+code);
		}
		return waybillResponse;
	}

	/**
	 * 通过运单获得包裹及运单信息
	 * @param waybillResponse
	 * @param code
	 * @param siteCode
	 * @param receiveSiteCode
	 * @return
	 */
	private WaybillResponse getWaybillAndPackByWaybillCode( WaybillResponse waybillResponse, String code, Integer siteCode, Integer receiveSiteCode ) {
		BaseEntity<com.jd.etms.waybill.dto.BigWaybillDto> entity =  wssByWaybillCode(code);
		if(null==entity)	return waybillResponse;
		
		Waybill waybill = entity.getData().getWaybill();
		List<DeliveryPackageD> packages = entity.getData().getPackageList();
		if( null==waybill || StringUtils.isBlank(waybill.getReceiverAddress())){
			logger.error(" Waybill wss:  waybillWSInfoProxy.getWaybillAndPackByWaybillCode(code) fail, address of waybill is empty , code: "+code);
			waybillResponse.setCode(WaybillResponse.CODE_NO_WAYBILL);
			waybillResponse.setMessage(WaybillResponse.MESSAGE_NO_WAYBILL);
		}
		
		List<PackageResponse> responseList = new ArrayList<PackageResponse>();
		if (BusinessHelper.checkIntNumRange(packages.size())) {
			for(DeliveryPackageD pack: packages){
				PackageResponse responseBean =new PackageResponse();
				responseBean.setWaybillCode(code);
				responseBean.setPackageBarcode(pack.getPackageBarcode());
				if( !( null==waybill || StringUtils.isBlank(waybill.getReceiverAddress())) ){
					responseBean.setAddress(waybill.getReceiverAddress());
				}
				responseBean.setCreateSiteCode(siteCode);
				responseBean.setReceiveSiteCode(receiveSiteCode);
				responseList.add(responseBean);
			}
		}
		
		waybillResponse.setPackages(responseList);
		return waybillResponse;
	}

	/**
	 * 通过包裹获得包裹信息及运单信息
	 * @param waybillResponse
	 * @param code
	 * @param siteCode
	 * @param receiveSiteCode
	 * @return
	 */
	private WaybillResponse wssGetWaybillByPackCode(WaybillResponse waybillResponse,String code, Integer siteCode, Integer receiveSiteCode) {
		BaseEntity<Waybill> entity = waybillQueryManager.getWaybillByPackCode(code);
		if(null==entity){
			logger.error(" Waybill wss: waybillWSInfoProxy.getWaybillByPackCode(code) fail that entity is null . package barcode: "+code);
			return waybillResponse;
		}
		if(entity.getResultCode()!=1){
			logger.error(" Waybill wss: waybillWSInfoProxy.getWaybillByPackCode(code) fail, code: "+code+". resultCode code: "+entity.getResultCode()
					+"\n error message: "+entity.getMessage());//print stack trace
			return waybillResponse;
		}
		Waybill waybill = entity.getData();
		
		PackageResponse pack = null;
		
		if( null==waybill || StringUtils.isBlank(waybill.getWaybillCode()) ){
			logger.error(" Waybill wss: waybillWSInfoProxy.getWaybillByPackCode(code) error. waybill is null,package barcode: "+code);
			pack =new PackageResponse(WaybillUtil.getWaybillCode(code), code, null, null);
			List<PackageResponse> responseList = new ArrayList<PackageResponse>();
			responseList.add(pack);
			waybillResponse.setPackages(responseList);
			waybillResponse.setCode(WaybillResponse.CODE_NO_WAYBILL);
			waybillResponse.setMessage(WaybillResponse.MESSAGE_NO_WAYBILL);
			return waybillResponse;
		}else if(StringUtils.isBlank(waybill.getReceiverAddress())){
			logger.error(" Waybill wss: waybillWSInfoProxy.getWaybillByPackCode(code) error. waybill's address is null,package barcode: "+code);
		}
		
		/*//调用治澎接口通过包裹号获得箱号、接收站点、创建站点
		Sorting sorting = new Sorting();
		sorting.setPackageCode(code);
		sorting.setCreateSiteCode(siteCode);
		List<Sorting> resultSorting = sortingService.findSortingPackages(sorting);//查询出未验货的记录,即箱号状态为2,3(分拣、验货开始)的
		
		List<PackageResponse> responseList = new ArrayList<PackageResponse>();
		for( Sorting sortingEach:resultSorting){
			PackageResponse pack =new PackageResponse(waybill.getWaybillCode(), code, null, waybill.getReceiverAddress());
			if(StringUtils.isBlank(sortingEach.getBoxCode())){
				return new WaybillResponse(WaybillResponse.CODE_NO_BOX,code + ":" +WaybillResponse.MESSAGE_NO_BOX);
			}
			pack.setBoxCode(sortingEach.getBoxCode());
			pack.setCreateSiteCode(siteCode);
			pack.setReceiveSiteCode(receiveSiteCode);
			responseList.add(pack);
		}*/
		
		pack =new PackageResponse(waybill.getWaybillCode(), code, null, waybill.getReceiverAddress());
		pack.setCreateSiteCode(siteCode);
		pack.setReceiveSiteCode(receiveSiteCode);
		List<PackageResponse> responseList = new ArrayList<PackageResponse>();
		responseList.add(pack);
		
		waybillResponse.setPackages(responseList);
		
		return waybillResponse;
	}

	/**
	 * 批量通过运单号获取包裹号
	 * @param waybillCodes
	 */
	@Override
	public Map<String, List<DeliveryPackageD>> getPackageBarcodeByWaybillCodeBatch(List<String> waybillCodes) {
		BaseEntity<Map<String, List<DeliveryPackageD>>> entity = waybillPackageManager.batchGetPackListByCodeList(waybillCodes);
		if(entity.getResultCode()!=1)	throw new InspectionException(" batchGetPackListByCodeList is empty , parameter waybillCodes: "+waybillCodes);
		Map<String, List<DeliveryPackageD>> map = entity.getData();
		return map;
	}
	
	/**
	 * 通过运单号获取运单下的包裹数量
	 * @param waybillCode
	 * @return
	 */
	@Override
	public Integer getPackageNumbersByWaybill(String waybillCode){
		BaseEntity<BigWaybillDto> entity = wssByWaybillCode(waybillCode);
		if(null==entity.getData().getWaybill() || null==entity.getData().getWaybill().getGoodNumber()){
			return -1;
		}else{
			return entity.getData().getWaybill().getGoodNumber();
		}
	}

	/**
	 * 通过运单号获取运单下的包裹号列表
	 * @param waybillCode
	 * @return
	 */
	public List<String> getPackageCodeListByWaybillCode(String waybillCode) {
		List<String> packageCodeList = new ArrayList<String>();
		BaseEntity<BigWaybillDto> entity = wssByWaybillCode(waybillCode);
		if (null == entity) return null;

		List<DeliveryPackageD> packages = entity.getData().getPackageList();
		for(DeliveryPackageD packageD : packages){
			packageCodeList.add(packageD.getPackageBarcode());
		}
		return packageCodeList;
	}
}
