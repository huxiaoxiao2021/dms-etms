package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.PageDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.*;
import com.jd.ql.basic.proxy.BasicPrimaryWSProxy;
import com.jd.ql.basic.proxy.BasicSecondaryWSProxy;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.basic.ws.BasicSecondaryWS;
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
import java.util.Collection;
import java.util.List;

@Service("baseMajorManager")
public class BaseMajorManagerImpl implements BaseMajorManager {

    private Log logger = LogFactory.getLog(BaseMajorManagerImpl.class);
    private static final String PROTOCOL = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS") + "/services/bases/siteString/";

    @Autowired
    @Qualifier("basicPrimaryWS")
    private BasicPrimaryWS basicPrimaryWS;

    @Autowired
    @Qualifier("basicTraderAPI")
    private BasicTraderAPI basicTraderAPI;

//    @Autowired
//    @Qualifier("basicSecondaryWS")
//    private BasicSecondaryWS basicSecondaryWS;

    @Autowired
    @Qualifier("basicPrimaryWSProxy")
    private BasicPrimaryWSProxy basicPrimaryWSProxy;

    @Autowired
    @Qualifier("basicSecondaryWSProxy")
    private BasicSecondaryWSProxy basicSecondaryWSProxy;

    /**
     * 站点ID
     */
    @Cache(key = "baseMajorManagerImpl.getBaseSiteBySiteId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteBySiteId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getBaseSiteBySiteId(Integer paramInteger) {
        BaseStaffSiteOrgDto dtoStaff = basicPrimaryWS.getBaseSiteBySiteId(paramInteger);
        ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
        if (dtoStaff != null)
            return dtoStaff;
        else
            dtoStaff = basicPrimaryWS.getBaseStoreByDmsSiteId(paramInteger);
        if (dtoStaff != null)
            return dtoStaff;
        else
            responseDTO = basicTraderAPI.getBaseTraderById(paramInteger);

        if (responseDTO != null && responseDTO.getResult() != null)
            dtoStaff = getBaseStaffSiteOrgDtoFromTrader(responseDTO.getResult());
        return dtoStaff;
    }

    @Cache(key = "baseMajorManagerImpl.getBaseDataDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseDataDictList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseDataDict> getBaseDataDictList(Integer paramInteger1,
                                                  Integer paramInteger2, Integer paramInteger3) {
        List<BaseDataDict> result = basicPrimaryWS.getBaseDataDictList(paramInteger1,
                paramInteger2, paramInteger3);
        if (result == null) result = new ArrayList<BaseDataDict>();
        return result;
    }

    @Cache(key = "baseMajorManagerImpl.getBaseStaffByStaffId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseStaffByStaffId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getBaseStaffByStaffId(Integer paramInteger) {
        return getBaseStaffByStaffIdNoCache(paramInteger);
    }

    @Cache(key = "baseMajorManagerImpl.getDmsSiteAll", memoryEnable = false,
            redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsSiteAll", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseStaffSiteOrgDto> getDmsSiteAll() {
        List<BaseStaffSiteOrgDto> allSite = new ArrayList<BaseStaffSiteOrgDto>();

        //基础站点
        List<BaseStaffSiteOrgDto> baseSite = basicPrimaryWSProxy.getBaseSiteAll();
        allSite.addAll(baseSite);

        //库房站点
        List<BaseStoreInfoDto> baseStore = basicPrimaryWSProxy.getBaseAllStore();
        for (BaseStoreInfoDto dto : baseStore) {
            allSite.add(getBaseStaffSiteOrgDtoFromStore(dto));
        }

        //商家站点
        List<BasicTraderInfoDTO> baseTrader = this.getBaseAllTrader();
        for (BasicTraderInfoDTO dto : baseTrader) {
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
    public List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSubType(Integer orgId, Integer targetType) {
		return basicPrimaryWSProxy.getBaseSiteByOrgIdSubType(orgId,targetType);
    }

    @Cache(key = "baseMajorManagerImpl.getBaseSiteByOrgIdSiteType@args0@args1", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByOrgIdSiteType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSiteType(Integer orgId, Integer siteType) {
        return getBaseSiteByOrgIdSiteTypeAll(orgId, siteType);
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
            BasicTraderInfoDTO trader) {
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
     */
    @Override
    @Cache(key = "baseMajorManagerImpl.getBaseSiteByDmsCode@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByDmsCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getBaseSiteByDmsCode(String siteCode) {
        BaseStaffSiteOrgDto dtoStaff = basicPrimaryWS.getBaseSiteByDmsCode(siteCode);
        BasicTraderInfoDTO dtoTrade = null;
        ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
        if (dtoStaff != null)
            return dtoStaff;
        else
            dtoStaff = basicPrimaryWS.getBaseStoreByDmsCode(siteCode);

        if (dtoStaff != null)
            return dtoStaff;
        else
            responseDTO = basicTraderAPI.getBaseTraderByCode(siteCode);

        if (responseDTO != null && responseDTO.getResult() != null)
            dtoStaff = getBaseStaffSiteOrgDtoFromTrader(responseDTO.getResult());
        return dtoStaff;
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
        try {
            BaseResult<PsStoreInfo> storeInfoResult = basicPrimaryWS.getStoreByCky2Id(storeType, cky2, storeID, sys);
            if (0 == storeInfoResult.getResultCode()) {
                return storeInfoResult.getData();
            } else {
                logger.warn("根据cky2获取仓库信息失败，接口返回code "
                        + storeInfoResult.getResultCode() + ", message " + storeInfoResult.getMessage());
            }
        } catch (Exception ex) {
            logger.warn("根据cky2获取仓库信息失败", ex);
            Profiler.functionError(info);
            return null;
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return null;
    }
    @Cache(key = "baseMajorManagerImpl.getDmsListByOrgId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsListByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<SimpleBaseSite> getDmsListByOrgId(Integer orgId) {
        List<BaseStaffSiteOrgDto> baseStaffSiteOrgDtos = getBaseSiteByOrgIdSiteTypeAll(orgId, Constants.DMS_SITE_TYPE);
        return convertBaseStaffSiteOrgDto2SimpleBaseSite(baseStaffSiteOrgDtos);
    }

    /**
     * 转换BaseStaffSiteOrgDto到SimpleBaseSite
     *
     * @param baseStaffSiteOrgDtos
     * @return
     */
    private List<SimpleBaseSite> convertBaseStaffSiteOrgDto2SimpleBaseSite(List<BaseStaffSiteOrgDto> baseStaffSiteOrgDtos) {
        if (baseStaffSiteOrgDtos == null || baseStaffSiteOrgDtos.size() == 0) {
            return new ArrayList<SimpleBaseSite>();
        }
        List<SimpleBaseSite> simpleBaseSites = new ArrayList<SimpleBaseSite>(baseStaffSiteOrgDtos.size());
        for (BaseStaffSiteOrgDto bdto : baseStaffSiteOrgDtos) {
            simpleBaseSites.add(convertBaseStaffSiteOrgDto(bdto));
        }
        return simpleBaseSites;
    }

    /**
     * VO装换
     * @param bdto
     * @return
     */
    private SimpleBaseSite convertBaseStaffSiteOrgDto(BaseStaffSiteOrgDto bdto) {
        SimpleBaseSite sbs = new SimpleBaseSite();
        sbs.setAddress(bdto.getAddress());
        sbs.setAirTransport(bdto.getAirTransport());
        sbs.setCityId(bdto.getCityId());
        sbs.setCountryId(bdto.getCountryId());
        sbs.setCustomCode(bdto.getCustomCode());
        sbs.setDmsId(bdto.getDmsId());
        sbs.setDmsName(bdto.getDmsName());
        sbs.setAreaId((int) bdto.getAreaId());
        sbs.setTelphone(bdto.getSitePhone());
        sbs.setDmsCode(bdto.getDmsSiteCode());
        sbs.setIsMiniWarehouse(bdto.getIsMiniWarehouse());
        sbs.setOperateState(bdto.getOperateState());
        sbs.setOrgId(bdto.getOrgId());
        sbs.setProvinceId(bdto.getProvinceId());
        sbs.setSiteCode(bdto.getSiteCode());
        sbs.setTownId(bdto.getTownId());
        sbs.setSubType(bdto.getSubType());
        sbs.setSiteType(bdto.getSiteType());
        sbs.setSiteName(bdto.getSiteName());
        sbs.setSiteBusinessType(bdto.getSiteBusinessType());
        return sbs;
    }

    /**
     * 根据type和orgID查询所有站点
     *
     * @param orgId
     * @param type
     * @return
     */
    private List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSiteTypeAll(Integer orgId, Integer type) {
        List<BaseStaffSiteOrgDto> result = new ArrayList<BaseStaffSiteOrgDto>();
        Integer pageIndex = 1;
        while (true) {
            PageDto<List<BaseStaffSiteOrgDto>> pageDto = basicPrimaryWS.getBaseSiteByOrgIdSiteTypePage(orgId, type, pageIndex);
            if (pageDto == null || pageDto.getData() == null || 0 == pageDto.getTotalRow() || 0 == pageDto.getTotalPage()) {
                break;
            }
            result.addAll(pageDto.getData());
            if (pageDto.getCurPage() == pageDto.getTotalPage()) {
                break;
            }
            pageIndex++;
        }
        return result;
    }

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
                if (logger.isInfoEnabled()) {
                    logger.debug(com.jd.bluedragon.utils.JsonHelper.toJson(base));
                }
                if (base != null && JdResponse.CODE_OK.equals(base.getCode())) {
                    SiteEntity site = com.jd.bluedragon.utils.JsonHelper.fromJsonUseGson(base.getRequest(), SiteEntity.class);
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
    @Cache(key = "baseMajorManagerImpl.getValidBaseDataDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getValidBaseDataDictList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseDataDict> getValidBaseDataDictList(Integer parentGroup, Integer nodeLevel, Integer typeGroup) {
        return basicPrimaryWS.getValidDataDict(parentGroup, nodeLevel, typeGroup);
    }

    @Override
    @Cache(key = "baseMajorManagerImpl.getBaseStaffListByOrgId@args02", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public List<BaseStaffSiteOrgDto> getBaseStaffListByOrgId(Integer orgid, int num) {
        // TODO Auto-generated method stub
        return basicPrimaryWSProxy.getBaseStaffListByOrgId(orgid, 2);
    }

    @Override
    public BaseStaffSiteOrgDto getThirdStaffByJdAccountNoCache(String jdAccount) {
        return basicPrimaryWS.getThirdStaffByJdAccount(jdAccount);
    }

    @Override
    public BaseStaffSiteOrgDto getBaseStaffByStaffIdNoCache(Integer paramInteger) {
        return basicPrimaryWS.getBaseStaffByStaffId(paramInteger);
    }

    @Override
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseStaffByErpNoCache", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseStaffByErpNoCache(String erp) {
        return basicPrimaryWS.getBaseStaffByErp(erp);
    }

    public Pager<List<SiteWareHouseMerchant>> getBaseSiteByPage(int pageIndex) {
        PageDto<List<BaseStaffSiteOrgDto>> resPageDto = basicPrimaryWS.getBaseSiteAllByPage(pageIndex);
        Pager<List<SiteWareHouseMerchant>> result = new Pager<List<SiteWareHouseMerchant>>();
        if (null == resPageDto || null == resPageDto.getData()) {
            result.setTotalSize(0);
            result.setTotalNo(1);
            return result;
        }
        result.setPageSize(resPageDto.getPageSize());
        result.setTotalSize(resPageDto.getTotalRow());
        result.setTotalNo(resPageDto.getTotalPage());
        result.setPageNo(resPageDto.getCurPage());
        result.setData(convertFromSite(resPageDto.getData()));
        return result;
    }

    private List<SiteWareHouseMerchant> convertFromSite(List<BaseStaffSiteOrgDto> list) {
        if (null == list)
            return new ArrayList<SiteWareHouseMerchant>();
        List<SiteWareHouseMerchant> result = new ArrayList<SiteWareHouseMerchant>(list.size());
        for (BaseStaffSiteOrgDto item : list) {
            SiteWareHouseMerchant site = new SiteWareHouseMerchant();
            site.setId(item.getSiteCode());
            site.setName(item.getSiteName());
            site.setDmsCode(item.getDmsSiteCode());
            site.setOrgId(item.getOrgId());
            site.setOrgName(item.getOrgName());
            site.setParentId(item.getParentSiteCode());
            site.setParentName(item.getParentSiteName());
            site.setSortingCenterId(item.getDmsId());
            site.setSortingCenterName(item.getDmsName());
            site.setPinyinCode(item.getSiteNamePym());
            site.setType(item.getSiteType());
            site.setSubType(item.getSubType());
            site.setProvinceId(item.getProvinceId());
            site.setCityId(item.getCityId());
            site.setCountryId(item.getCountryId());
            result.add(site);
        }
        return result;
    }

    /**
     * 分页获取库房
     *
     * @param pageIndex
     * @return
     */
    public Pager<List<SiteWareHouseMerchant>> getBaseStoreInfoByPage(Integer pageIndex) {
        PageDto<List<BaseStoreInfoDto>> resPageDto = basicPrimaryWS.getBaseStoreInfoByPage(pageIndex);
        Pager<List<SiteWareHouseMerchant>> result = new Pager<List<SiteWareHouseMerchant>>();
        if (null == resPageDto || null == resPageDto.getData()) {
            result.setTotalSize(0);
            result.setTotalNo(1);
            return result;
        }
        result.setPageSize(resPageDto.getPageSize());
        result.setTotalSize(resPageDto.getTotalRow());
        result.setTotalNo(resPageDto.getTotalPage());
        result.setPageNo(resPageDto.getCurPage());
        result.setData(convertFromStore(resPageDto.getData()));
        return result;
    }

    private List<SiteWareHouseMerchant> convertFromStore(List<BaseStoreInfoDto> list) {
        if (null == list)
            return new ArrayList<SiteWareHouseMerchant>();
        List<SiteWareHouseMerchant> result = new ArrayList<SiteWareHouseMerchant>(list.size());
        for (BaseStoreInfoDto item : list) {
            SiteWareHouseMerchant site = new SiteWareHouseMerchant();
            site.setId(item.getStoreSiteCode());
            site.setName(item.getDmsStoreName());
            site.setDmsCode(item.getDmsSiteCode());
            site.setOrgId(item.getParentId());
            site.setOrgName(item.getOrgName());
            site.setType(item.getDmsType());
            site.setParentId(item.getParentId());
            site.setParentName(null);
            site.setSortingCenterId(null);
            site.setSortingCenterName(null);
            site.setPinyinCode(item.getSiteNamePym());
            site.setProvinceId(item.getProvinceId());
            site.setCityId(item.getCityId());
            site.setCountryId(null);
            result.add(site);
        }
        return result;
    }

    public Pager<List<SiteWareHouseMerchant>> getTraderListByPage(int pageIndex) {
        ResponseDTO<PageDTO<BasicTraderInfoDTO>> responseDTO = null;
        PageDTO<BasicTraderInfoDTO> resPageDto = null;
        responseDTO = this.basicTraderAPI.getTraderListByPage(pageIndex);
        Pager<List<SiteWareHouseMerchant>> result = new Pager<List<SiteWareHouseMerchant>>();
        if (null == responseDTO || null == responseDTO.getResult() || null == responseDTO.getResult().getData()) {
            result.setTotalSize(0);
            result.setTotalNo(1);
            return result;
        }
        resPageDto = responseDTO.getResult();
        result.setPageSize(resPageDto.getPageSize());
        result.setTotalSize(resPageDto.getTotalRow());
        result.setTotalNo(resPageDto.getTotalPage());
        result.setPageNo(resPageDto.getCurPage());
        result.setData(convertFromTrade(resPageDto.getData()));
        return result;
    }

    private List<SiteWareHouseMerchant> convertFromTrade(List<BasicTraderInfoDTO> list) {
        if (null == list)
            return new ArrayList<SiteWareHouseMerchant>();
        List<SiteWareHouseMerchant> result = new ArrayList<SiteWareHouseMerchant>(list.size());
        for (BasicTraderInfoDTO item : list) {
            SiteWareHouseMerchant site = new SiteWareHouseMerchant();
            site.setId(item.getId());
            site.setName(item.getTraderName());
            site.setDmsCode(item.getTraderCode());
            site.setOrgId(BaseContants.BASIC_B_TRADER_ORG);
            site.setOrgName(BaseContants.BASIC_B_TRADER_ORG_NAME);
            site.setType(BaseContants.BASIC_B_TRADER_SITE_TYPE);
            site.setParentId(null);
            site.setParentName(null);
            site.setSortingCenterId(null);
            site.setSortingCenterName(null);
            site.setPinyinCode(item.getSiteNamePym());
            result.add(site);
        }
        return result;
    }

    private List<BasicTraderInfoDTO> getBaseAllTrader() {
        logger.info("基础资料客户端--getBaseAllTrader获取所有商家，开始调用分页接口获取数据");
        List<BasicTraderInfoDTO> traderList = new ArrayList();
        int count = 0;
        long startTime = System.currentTimeMillis();
        ResponseDTO<PageDTO<BasicTraderInfoDTO>> responseDTO = this.basicTraderAPI.getTraderListByPage(1);
        if(responseDTO != null && null != responseDTO.getResult() && null != responseDTO.getResult().getData()
                && ((List)responseDTO.getResult().getData()).size() > 0) {
            PageDTO<BasicTraderInfoDTO> resPageDto = responseDTO.getResult();
            traderList.addAll((Collection)resPageDto.getData());
            count = resPageDto.getTotalRow();
            int totalPage = resPageDto.getTotalPage();

            for(int i = 2; i <= totalPage; ++i) {
                traderList.addAll((Collection)this.basicTraderAPI.getTraderListByPage(i).getResult().getData());
            }
        } else {
            logger.error("getBaseAllTrader获取数据为空");
        }

        logger.info("getBaseAllTrader获取数据count[" + count + "]");
        logger.info("getBaseAllTrader获取数据耗时[" + (System.currentTimeMillis() - startTime) + "]");
        return traderList;
    }

    @Cache(key = "baseMajorManagerImpl.getBaseAllStore", memoryEnable = false,
            redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseAllStore", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseStaffSiteOrgDto> getBaseAllStore() {
        List<BaseStaffSiteOrgDto> allSite = new ArrayList<BaseStaffSiteOrgDto>();

        //库房站点
        List<BaseStoreInfoDto> baseStore = basicPrimaryWSProxy.getBaseAllStore();
        for (BaseStoreInfoDto dto : baseStore) {
            allSite.add(getBaseStaffSiteOrgDtoFromStore(dto));
        }

        return allSite;
    }

}
