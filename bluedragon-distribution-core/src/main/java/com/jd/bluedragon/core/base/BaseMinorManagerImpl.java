package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.PageDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.*;
import com.jd.ql.basic.dto.BaseGoodsPositionDto;
import com.jd.ql.basic.ws.BasicAirConfigWS;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service("baseMinorManager")
public class BaseMinorManagerImpl implements BaseMinorManager {
	private Log log = LogFactory.getLog(BaseMinorManagerImpl.class);
	
	public static final String SEPARATOR_HYPHEN = "-";

	@Autowired
	@Qualifier("basicTraderAPI")
	private BasicTraderAPI basicTraderAPI;

	@Autowired
	@Qualifier("basicSecondaryWS")
	private BasicSecondaryWS basicSecondaryWS;

	/**
	 * 
	 * 获取航空标示信息接口
	 */
	@Autowired
	private BasicAirConfigWS basicAirConfigWS;
	
	
	
	@Cache(key = "TbaseMinorManagerImpl.getBaseTraderById@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderById", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BasicTraderInfoDTO getBaseTraderById(Integer paramInteger) {
		ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
		responseDTO = basicTraderAPI.getBaseTraderById(paramInteger);
		if(responseDTO != null){
			return responseDTO.getResult();
		}
		return null;
	}

	@Cache(key = "baseMinorManagerImpl.getAirConfig@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getAirConfig", mState = {JProEnum.TP, JProEnum.FunctionError})
	public AirTransport getAirConfig(Integer originalProvinceId,
			Integer originalCityId, Integer destinationProvinceId,
			Integer destinationCityId) {

		return basicAirConfigWS
				.getAirConfig(originalProvinceId, originalCityId,
						destinationProvinceId, destinationCityId);
	}
	
	@Cache(key = "baseMinorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID,String flage, Integer siteCode) {
		return ((List<BaseGoodsPositionDto>) basicSecondaryWS.getBaseGoodsPositionDmsCodeSiteCode(dmsID, siteCode));
		 
	}

	@Cache(key = "baseMinorManagerImpl.getBaseGoodsPositionTaskAreaNoDmsId@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseGoodsPositionTaskAreaNoDmsId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(
			Integer dmsID,String flage, Integer taskAreaNo) {
		return (List<BaseGoodsPositionDto>) basicSecondaryWS.getBaseGoodsPositionTaskAreaNoDmsId(String.valueOf(taskAreaNo), dmsID);
	}

	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getCrossPackageTagByPara", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResult<CrossPackageTagNew> getCrossPackageTagByPara(
			BaseDmsStore bds, Integer siteCode, Integer startDmsCode) {
		// TODO Auto-generated method stub
		return basicSecondaryWS.getCrossPackageTagByPara(bds, siteCode, startDmsCode);
	}


	@Cache(key = "TbaseMinorManagerImpl.getBaseTraderByName@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderByName", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BasicTraderInfoDTO> getBaseTraderByName(String name) {
		ResponseDTO<List<BasicTraderInfoDTO>> responseDTO =  basicTraderAPI.getBaseTraderByName(name);
		if(responseDTO == null){
			return Collections.emptyList();
		}
		return responseDTO.getResult();
	}

    /**
     * 获取签约商家ID列表
     * @return
     */
    @Cache(key = "baseMinorManagerImpl.getTraderInfoPopCodeAll", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getSignCustomer", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<String> getSignCustomer(){
		ResponseDTO<List<String>> responseDTO = null;
         responseDTO = basicTraderAPI.getTraderInfoPopCodeAll();
         if(responseDTO == null){
         	return null;
		 }
		return responseDTO.getResult();
    }

	@Cache(key = "TbaseMinorManagerImpl.getBaseAllTrader", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseAllTrader", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BasicTraderInfoDTO> getBaseAllTrader() {
		log.info("基础资料客户端--getBaseAllTrader获取所有商家，开始调用分页接口获取数据");
		List<BasicTraderInfoDTO> traderList = new ArrayList();
		int count = 0;
		long startTime = System.currentTimeMillis();
		ResponseDTO<PageDTO<BasicTraderInfoDTO>> resPageDto = this.basicTraderAPI.getTraderListByPage(1);
		if(null != resPageDto && null != resPageDto.getResult() && null != resPageDto.getResult().getData()
				&& (resPageDto.getResult().getData()).size() > 0) {
			PageDTO<BasicTraderInfoDTO> pageDTO = resPageDto.getResult();
			traderList.addAll((Collection)pageDTO.getData());
			count = pageDTO.getTotalRow();
			int totalPage = pageDTO.getTotalPage();

			for(int i = 2; i <= totalPage; ++i) {
				traderList.addAll((Collection)this.basicTraderAPI.getTraderListByPage(i).getResult().getData());
			}
		} else {
			log.error("getBaseAllTrader获取数据为空");
		}

		log.info("getBaseAllTrader获取数据count[" + count + "]");
		log.info("getBaseAllTrader获取数据耗时[" + (System.currentTimeMillis() - startTime) + "]");
		return traderList;
	}

	@Cache(key = "DMS.BASE.BaseMinorManagerImpl.getGoodsVolumeLimitBySiteCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getGoodsVolumeLimitBySiteCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseSiteGoods getGoodsVolumeLimitBySiteCode(Integer siteCode) {
		try {
			BaseResult<BaseSiteGoods> baseResult = basicSecondaryWS.getGoodsVolumeLimitBySiteCode(siteCode);
			if (baseResult != null && baseResult.getResultCode() == BaseResult.RESULT_SUCCESS) {
				return baseResult.getData();
			}else{
				log.warn("获取三方站点超限配置为空，siteCode："+siteCode+
						",返回结果："+baseResult==null?"null":JsonHelper.toJson(baseResult));
			}
		} catch (Exception e) {
			log.error("获取三方站点超限配置异常，siteCode："+siteCode, e);
		}
		return null;
	}


	@Override
	public BasicTraderInfoDTO getTraderInfoByPopCode(String popCode){
		ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
		responseDTO = basicTraderAPI.getBasicTraderInfoByPopId(popCode);
		if(responseDTO != null){
			return responseDTO.getResult();
		}
		return null;
	}
}
