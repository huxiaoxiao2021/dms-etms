package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.etms.utils.cache.annotation.Cache;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.BaseStoreInfoDto;
import com.jd.ql.basic.dto.BaseTradeInfoDto;
import com.jd.ql.basic.dto.SimpleBaseSite;
import com.jd.ql.basic.proxy.BasicPrimaryWSProxy;
import com.jd.ql.basic.proxy.BasicSecondaryWSProxy;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Service("baseMajorManager")
public class BaseMajorManagerImpl implements BaseMajorManager {

	private Log logger = LogFactory.getLog(BaseMajorManagerImpl.class);

	@Autowired
	@Qualifier("basicPrimaryWS")
	private BasicPrimaryWS basicPrimaryWS;
	
	@Autowired
	@Qualifier("basicPrimaryWSProxy")
	private BasicPrimaryWSProxy basicPrimaryWSProxy;
	
	@Autowired
	@Qualifier("basicSecondaryWSProxy")
	private BasicSecondaryWSProxy basicSecondaryWSProxy;

	/**
	 * 站点ID
	 * */
	@Cache(key = "baseMajorManagerImpl.getBaseSiteBySiteId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteBySiteId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseSiteBySiteId(Integer paramInteger) {
		return basicPrimaryWS.getBaseSiteBySiteId(paramInteger);
	}

	@Cache(key = "baseMajorManagerImpl.getBaseDataDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseDataDictList", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseDataDict> getBaseDataDictList(Integer paramInteger1,
			Integer paramInteger2, Integer paramInteger3) {
		return basicPrimaryWS.getBaseDataDictList(paramInteger1,
				paramInteger2, paramInteger3);
	}

	@Cache(key = "baseMajorManagerImpl.getBaseStaffByStaffId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseStaffByStaffId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseStaffByStaffId(Integer paramInteger) {
		return basicPrimaryWS.getBaseStaffByStaffId(paramInteger);
	}

	@Cache(key = "baseMajorManagerImpl.getDmsSiteAll", memoryEnable = true, memoryExpiredTime = 20 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsSiteAll", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseStaffSiteOrgDto> getDmsSiteAll() {
		List<BaseStaffSiteOrgDto> allSite = new ArrayList<BaseStaffSiteOrgDto>();
		
		//基础站点
		List<BaseStaffSiteOrgDto> baseSite = basicPrimaryWSProxy.getBaseSiteAll();
		allSite.addAll(baseSite);
		
		//库房站点
		List<BaseStoreInfoDto> baseStore = basicPrimaryWSProxy.getBaseAllStore();
		for(BaseStoreInfoDto dto : baseStore){
			allSite.add(getBaseStaffSiteOrgDtoFromStore(dto));
		}
		
		//商家站点
		List<BaseTradeInfoDto> baseTrader = basicSecondaryWSProxy.getBaseAllTrader();
		for(BaseTradeInfoDto dto : baseTrader){
			allSite.add(getBaseStaffSiteOrgDtoFromTrader(dto));
		}
		
		return allSite;
	}

	@Cache(key = "baseMajorManagerImpl.getBaseOrgByOrgId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseOrgByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseOrg getBaseOrgByOrgId(Integer orgId) {
		return basicPrimaryWS.getBaseOrgByOrgId(orgId);
	}

	@Cache(key = "baseMajorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode@args0@args1", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	public Integer getBaseGoodsPositionDmsCodeSiteCode(String createCode,
			String receiveCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Cache(key = "baseMajorManagerImpl.getBaseSiteAll", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteAll", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseStaffSiteOrgDto> getBaseSiteAll() {
		return basicPrimaryWSProxy.getBaseSiteAll();
	}
	
	@Cache(key = "baseMajorManagerImpl.getBaseSiteByOrgIdSubType@args0@args1", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSubType(Integer orgId,
			Integer targetType) {
		return basicPrimaryWSProxy.getBaseSiteByOrgIdSubType(orgId,targetType);
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
	/**
	 * 7位编码
	 * */
	@Override
	@Cache(key = "baseMajorManagerImpl.getBaseSiteByDmsCode@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByDmsCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseSiteByDmsCode(String siteCode) {
		return basicPrimaryWS.getBaseSiteByDmsCode(siteCode);
	}

	@Cache(key = "baseMajorManagerImpl.getBaseDataDictById@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseDataDictById", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseDataDict getBaseDataDictById(Integer id) {
		return basicPrimaryWS.getBaseDataDictById(id);
	}


	@Override
	@Cache(key = "baseMajorManagerImpl.getStoreByCky2@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	public PsStoreInfo getStoreByCky2(String storeType, Integer cky2, Integer storeID, String sys) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.getStoreByCky2", false, true);
		try{
			BaseResult<PsStoreInfo> storeInfoResult =  basicPrimaryWS.getStoreByCky2Id(storeType, cky2, storeID, sys);
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
		return basicPrimaryWSProxy.getSiteByOrgSubTypeAll(orgId, new Integer(64).toString());
	}
	
	private static final String PROTOCOL = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS")+"/services/bases/siteString/";
	@Override
	@Cache(key = "baseMajorManagerImpl.queryDmsBaseSiteByCodeDmsver@args0", memoryEnable = false, memoryExpiredTime = 60 * 60 * 1000,
		redisEnable = true, redisExpiredTime = 3 * 60 * 60 * 1000)
	public BaseStaffSiteOrgDto queryDmsBaseSiteByCodeDmsver(String siteCode) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.queryDmsBaseSiteByCodeDmsver", false, true);
		try {
            String uri = PROTOCOL + siteCode;
            ClientRequest request = new ClientRequest(uri);
            request.accept(MediaType.APPLICATION_JSON);
            
            BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();

            ClientResponse<JdResponse> response = request.get(JdResponse.class);
            if (200 == response.getStatus()) {
            	JdResponse base = response.getEntity();
                if(logger.isInfoEnabled()) {
                    logger.debug(com.jd.bluedragon.utils.JsonHelper.toJson(base));
                }
            	if(base!=null&&JdResponse.CODE_OK.equals(base.getCode())){
                    SiteEntity site= com.jd.bluedragon.utils.JsonHelper.fromJsonUseGson(base.getRequest(),SiteEntity.class);
            		dto.setSiteCode(site.getCode());
            		dto.setSiteName(site.getName());
            		dto.setSiteType(site.getType());
            		return dto;
            	}
            }
        } catch (Exception e) {
            logger.error("dmsver获取站点[" + siteCode + "]信息失败，异常为：", e);
			Profiler.functionError(info);
        } finally {
			Profiler.registerInfoEnd(info);
		}
        logger.error("dmsver获取站点[" + siteCode + "]信息失败");
        return null;
	}

	@Override
	@Cache(key = "baseMajorManagerImpl.getValidBaseDataDictList@args0@args1@args2" , memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000 )
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getValidBaseDataDictList", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseDataDict> getValidBaseDataDictList(Integer parentGroup, Integer nodeLevel, Integer typeGroup) {
		return basicPrimaryWS.getValidDataDict(parentGroup, nodeLevel, typeGroup);
	}

	@Override
	@Cache(key = "baseMajorManagerImpl.getBaseStaffListByOrgId@args02", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseStaffSiteOrgDto> getBaseStaffListByOrgId(Integer orgid, int num) {
		// TODO Auto-generated method stub
		return basicPrimaryWSProxy.getBaseStaffListByOrgId(orgid,2);
	}

}
