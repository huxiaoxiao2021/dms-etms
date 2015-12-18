package com.jd.bluedragon.distribution.electron.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseGoodsPositionDto;

@Service("electronSiteService")
public class ElectronSiteServiceImpl implements ElectronSiteService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	BaseMinorManager baseMinorManager;

	@Autowired
	WaybillQueryApi waybillQueryApi;

	@Override
	@Profiled(tag = "ElectronSiteService.getElecSiteInfo")
	public ElectronSite getElecSiteInfo(Integer dmsID, String waybillorPackCode) {
		Integer siteCode = 0;

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi
				.getDataByChoice(waybillorPackCode, wChoice);
		if (baseEntity != null && baseEntity.getData() != null) {
			siteCode = baseEntity.getData().getWaybill().getOldSiteId();
		} else {
			this.logger.error("获取订单【   " + waybillorPackCode + " 】 信息返回 NULL");
			return null;
		}
		if (siteCode == 0) {
			this.logger.error("订单：  " + waybillorPackCode + " 预分拣站点是  "
					+ siteCode);
			return this.Not0Found();
		}
		List<BaseGoodsPositionDto> baseGoodsPositionDto = baseMinorManager
				.getBaseGoodsPositionDmsCodeSiteCode(dmsID,Constants.SEPARATOR_HYPHEN, siteCode);
		if (baseGoodsPositionDto != null && !baseGoodsPositionDto.isEmpty()) {
			return toElectronSite(baseGoodsPositionDto.get(0));
		} else {
			this.logger
					.error("读取接口失败 baseMinorManager.getBaseGoodsPositionDmsCodeSiteCode");
			return null;
		}

	}

	/**
	 * 调用成功预分拣站点0
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	private ElectronSite Not0Found() {
		ElectronSite electronSite = new ElectronSite();
		electronSite.setCode(electronSite.CODE_OK_NULL);
		electronSite.setMessage(electronSite.MESSAGE_OK_NULL);
		return electronSite;
	}
	
	@SuppressWarnings("static-access")
	private ElectronSite toElectronSite(BaseGoodsPositionDto baseGoodsPositionDto){
		ElectronSite electronSite = new ElectronSite();
		
		if(baseGoodsPositionDto!=null){
			electronSite.setCode(electronSite.CODE_OK);
			electronSite.setTaskAreaNo(baseGoodsPositionDto.getTaskAreaNo());
			electronSite.setElectronNo(baseGoodsPositionDto.getElectronNo());
		}else{
			electronSite.setCode(electronSite.CODE_OK_NULL);
			electronSite.setMessage(electronSite.MESSAGE_OK_NULL);
			
		}
		
		return electronSite;
	}

	@Override
	public ElectronSite getTaskAreaNo(Integer dmsID, Integer taskAreaNo) {
		List<BaseGoodsPositionDto> baseGoodsPositionDto = baseMinorManager
				.getBaseGoodsPositionTaskAreaNoDmsId(dmsID,Constants.SEPARATOR_HYPHEN, taskAreaNo);
		if (baseGoodsPositionDto != null && !baseGoodsPositionDto.isEmpty()) {
			return toElectronSite(baseGoodsPositionDto.get(0));
		} else {
			this.logger
					.error("读取接口失败 baseMinorManager.getBaseGoodsPositionTaskAreaNoDmsId");
			return null;
		}
	}

}
