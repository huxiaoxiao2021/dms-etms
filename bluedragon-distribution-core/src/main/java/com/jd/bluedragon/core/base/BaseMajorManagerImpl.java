package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.jd.etms.basic.domain.BaseResult;
import com.jd.etms.basic.domain.PsStoreInfo;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.etms.basic.cache.proxy.BasicMajorWSProxy;
import com.jd.etms.basic.cache.proxy.BasicMinorWSProxy;
import com.jd.etms.basic.domain.BaseDataDict;
import com.jd.etms.basic.domain.BaseOrg;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.basic.dto.BaseStoreInfoDto;
import com.jd.etms.basic.dto.BaseTradeInfoDto;
import com.jd.etms.basic.dto.SimpleBaseSite;
import com.jd.etms.basic.wss.BasicMajorWS;
import com.jd.etms.utils.cache.annotation.Cache;

@Service("baseMajorManager")
public class BaseMajorManagerImpl implements BaseMajorManager {

	private Log logger = LogFactory.getLog(BaseMajorManagerImpl.class);

	@Autowired
	@Qualifier("basicMajorWSSaf")
	private BasicMajorWS basicMajorWSSaf;
	
	@Autowired
	@Qualifier("basicMajorWSProxy")
	private BasicMajorWSProxy basicMajorWSProxy;
	
	@Autowired
	@Qualifier("basicMinorWSProxy")
	private BasicMinorWSProxy basicMinorWSProxy;


	@Cache(key = "basicMajorWSProxy.getBaseSiteBySiteId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteBySiteId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseSiteBySiteId(Integer paramInteger) {
		return basicMajorWSSaf.getBaseSiteBySiteId(paramInteger);
	}

	@Cache(key = "basicMajorWSProxy.getBaseDataDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseDataDictList", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseDataDict> getBaseDataDictList(Integer paramInteger1,
			Integer paramInteger2, Integer paramInteger3) {
		return basicMajorWSSaf.getBaseDataDictList(paramInteger1,
				paramInteger2, paramInteger3);
	}

	@Cache(key = "basicMajorWSProxy.getBaseStaffByStaffId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseStaffByStaffId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseStaffByStaffId(Integer paramInteger) {
		return basicMajorWSSaf.getBaseStaffByStaffId(paramInteger);
	}

	@Cache(key = "basicMajorWSProxy.getDmsSiteAll", memoryEnable = true, memoryExpiredTime = 20 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsSiteAll", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseStaffSiteOrgDto> getDmsSiteAll() {
		List<BaseStaffSiteOrgDto> allSite = new ArrayList<BaseStaffSiteOrgDto>();
		
		//基础站点
		List<BaseStaffSiteOrgDto> baseSite = basicMajorWSProxy.getBaseSiteAll();
		allSite.addAll(baseSite);
		
		//库房站点
		List<BaseStoreInfoDto> baseStore = basicMajorWSProxy.getBaseAllStore();
		for(BaseStoreInfoDto dto : baseStore){
			allSite.add(getBaseStaffSiteOrgDtoFromStore(dto));
		}
		
		//商家站点
		List<BaseTradeInfoDto> baseTrader = basicMinorWSProxy.getBaseAllTrader();
		for(BaseTradeInfoDto dto : baseTrader){
			allSite.add(getBaseStaffSiteOrgDtoFromTrader(dto));
		}
		
		return allSite;
	}

	@Cache(key = "basicMajorWSProxy.getBaseOrgByOrgId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseOrgByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseOrg getBaseOrgByOrgId(Integer orgId) {
		return basicMajorWSSaf.getBaseOrgByOrgId(orgId);
	}

	@Cache(key = "basicMajorWSProxy.getBaseGoodsPositionDmsCodeSiteCode@args0@args1", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	public Integer getBaseGoodsPositionDmsCodeSiteCode(String createCode,
			String receiveCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Cache(key = "basicMajorWSProxy.getBaseSiteAll", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteAll", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseStaffSiteOrgDto> getBaseSiteAll() {
		return basicMajorWSProxy.getBaseSiteAll();
	}
	
	@Cache(key = "basicMajorWSProxy.getBaseSiteByOrgId@args0@args1", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseStaffSiteOrgDto> getBaseSiteByOrgId(Integer orgId,
			String targetType) {
		return basicMajorWSProxy.getBaseSiteByOrgId(orgId,targetType);
	}
	
	public BaseStaffSiteOrgDto getBaseStaffSiteOrgDtoFromStore(
			BaseStoreInfoDto store) {
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
		baseStaffSiteOrgDto.setDmsSiteCode(store.getDmsSiteCode());
		baseStaffSiteOrgDto.setStoreCode(store.getDmsStoreId());
		baseStaffSiteOrgDto.setSiteCode(store.getStoreSiteCode());
		baseStaffSiteOrgDto.setSiteName(store.getDmsStoreName());
		baseStaffSiteOrgDto.setCustomCode(store.getCustomCode());
		baseStaffSiteOrgDto.setSiteType(store.getDmsType());
		baseStaffSiteOrgDto.setOrgId(store.getParentId());
		baseStaffSiteOrgDto.setOrgName(store.getOrgName());
		return baseStaffSiteOrgDto;
	}
	
	
	
	public BaseStaffSiteOrgDto getBaseStaffSiteOrgDtoFromTrader(
			BaseTradeInfoDto trader) {
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
		baseStaffSiteOrgDto.setDmsSiteCode(trader.getTraderCode());
		baseStaffSiteOrgDto.setSiteCode(trader.getId());
		baseStaffSiteOrgDto.setSiteName(trader.getTraderName());
		baseStaffSiteOrgDto.setSiteType(BaseContants.BASIC_B_TRADER_SITE_TYPE);
		baseStaffSiteOrgDto.setOrgId(BaseContants.BASIC_B_TRADER_ORG);
		baseStaffSiteOrgDto.setOrgName(BaseContants.BASIC_B_TRADER_ORG_NAME);
		baseStaffSiteOrgDto.setTraderTypeEbs(trader.getTraderTypeEbs());
		baseStaffSiteOrgDto.setAccountingOrg(trader.getAccountingOrg());
		baseStaffSiteOrgDto.setAirTransport(trader.getAirTransport());
		return baseStaffSiteOrgDto;
	}
	
	@Override
	@Cache(key = "basicMajorWSProxy.getDmsBaseSiteByCode@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsBaseSiteByCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getDmsBaseSiteByCode(String siteCode) {
		return basicMajorWSSaf.getDmsBaseSiteByCode(siteCode);
	}

	@Override
	@Cache(key = "basicMajorWSProxy.getBaseStoreInfoBySiteId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseStoreInfoBySiteId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStoreInfoDto getBaseStoreInfoBySiteId(String dmsSiteCode) {
		BaseStoreInfoDto storeInfoDto = new BaseStoreInfoDto();
		storeInfoDto.setDmsSiteCode(dmsSiteCode);
		return basicMajorWSSaf.getBaseStoreInfo(storeInfoDto);
	}

	@Cache(key = "basicMajorWSProxy.getBaseDataDictById@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseDataDictById", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseDataDict getBaseDataDictById(Integer id) {
		return basicMajorWSSaf.getBaseDataDictById(id);
	}


	@Override
	@Cache(key = "basicMajorWSProxy.getStoreByCky2@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	public PsStoreInfo getStoreByCky2(String storeType, Integer cky2, Integer storeID, String sys) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.getStoreByCky2", false, true);
		try{
			BaseResult<PsStoreInfo> storeInfoResult =  basicMajorWSSaf.getStoreByCky2Id(storeType, cky2, storeID, sys);
			if(0 == storeInfoResult.getResultCode()){
				return storeInfoResult.getData();
			}else{
				logger.warn("根据cky2获取仓库信息失败，接口返回code "
						+ storeInfoResult.getResultCode() + ", message " + storeInfoResult.getMessage());
			}
		}catch (Exception ex){
			logger.warn("根据cky2获取仓库信息失败",ex);
			Profiler.functionError(info);
			return null;
		}finally {
			Profiler.registerInfoEnd(info);
		}

		return null;
	}

	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsListByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<SimpleBaseSite> getDmsListByOrgId(Integer orgId){
		return basicMajorWSProxy.getSiteByOrgSubTypeAll(orgId, new Integer(64).toString());
	}
	
	private static final String PROTOCOL = "http://dmsver.etms.360buy.com/services/bases/site/";

	@Override
	@Profiled
	@Cache(key = "basicMajorServiceProxy.queryDmsBaseSiteByCodeDmsver@args0", memoryEnable = false, memoryExpiredTime = 60 * 60 * 1000, 
		redisEnable = true, redisExpiredTime = 3 * 60 * 60 * 1000)
	public BaseStaffSiteOrgDto queryDmsBaseSiteByCodeDmsver(String siteCode) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.queryDmsBaseSiteByCodeDmsver", false, true);
		try {
            String uri = PROTOCOL + siteCode;
            ClientRequest request = new ClientRequest(uri);
            request.accept(MediaType.APPLICATION_JSON);
            
            BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();

            ClientResponse<BaseResponse> response = request.get(BaseResponse.class);
            if (200 == response.getStatus()) {
            	BaseResponse base = response.getEntity();
            	if(base!=null){
            		dto.setSiteCode(base.getSiteCode());
            		dto.setSiteName(base.getSiteName());
            		dto.setSiteType(base.getSiteType());
            		return dto;
            	}
            }
        } catch (Exception e) {
            logger.error("dmsver获取站点[" + siteCode + "]信息失败，异常为：", e);
			Profiler.functionError(info);
        } finally {
			Profiler.registerInfoEnd(info);
		}
        return null;
	}
}
