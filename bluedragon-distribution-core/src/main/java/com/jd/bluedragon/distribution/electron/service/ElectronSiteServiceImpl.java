package com.jd.bluedragon.distribution.electron.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseGoodsPositionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("electronSiteService")
public class ElectronSiteServiceImpl implements ElectronSiteService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	BaseMinorManager baseMinorManager;

	@Autowired
	WaybillQueryManager waybillQueryManager;

	@Override
	public ElectronSite getElecSiteInfo(Integer dmsID, String waybillorPackCode) {
		Integer siteCode = 0;

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager
				.getDataByChoice(waybillorPackCode, wChoice);
		if (baseEntity != null && baseEntity.getData() != null) {
			siteCode = baseEntity.getData().getWaybill().getOldSiteId();
		} else {
			this.log.error("获取订单【{}】 信息返回 NULL",waybillorPackCode);
			return null;
		}
		if (siteCode == 0) {
			this.log.error("订单：{}预分拣站点是{}",waybillorPackCode, siteCode);
			return this.Not0Found();
		}
		List<BaseGoodsPositionDto> baseGoodsPositionDto = baseMinorManager
				.getBaseGoodsPositionDmsCodeSiteCode(dmsID,Constants.SEPARATOR_HYPHEN, siteCode);
		if (baseGoodsPositionDto != null && !baseGoodsPositionDto.isEmpty()) {
			return toElectronSite(baseGoodsPositionDto.get(0));
		} else {
			this.log
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
			this.log
					.error("读取接口失败 baseMinorManager.getBaseGoodsPositionTaskAreaNoDmsId");
			return null;
		}
	}

}
