package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;
import com.jd.bluedragon.core.security.dataam.SecurityCheckerExecutor;
import com.jd.bluedragon.core.security.dataam.enums.SecurityDataMapFuncEnum;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.middleend.sorting.domain.DmsCustomSite;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.sdk.modules.menu.CommonUseMenuApi;
import com.jd.bluedragon.sdk.modules.menu.dto.MenuPdaRequest;
import com.jd.bluedragon.utils.*;
import com.jd.coldchain.distribution.api.WaybillPackageContainerApi;
import com.jd.coldchain.distribution.dto.BaseResponse;
import com.jd.etms.basic.jsf.BasicSiteUpdateService;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ldop.basic.dto.PageDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.*;
import com.jd.ql.basic.dto.*;
import com.jd.ql.basic.proxy.BasicPrimaryWSProxy;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.basic.ws.BasicSiteQueryWS;
import com.jd.ql.basic.ws.BasicStaffQueryWS;
import com.jd.ql.dms.report.SiteQueryService;
import com.jd.ql.dms.report.domain.*;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.*;

@Service("baseMajorManager")
public class BaseMajorManagerImpl implements BaseMajorManager {

    private Logger log = LoggerFactory.getLogger(BaseMajorManagerImpl.class);
    private static final String PROTOCOL = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS") + "/services/bases/siteString/";
    //系统标识
    private final String DMS = "dms";
    /**
     * 监控key的前缀
     */
    private static final String UMP_KEY_PREFIX = UmpConstants.UMP_KEY_JSF_CLIENT+"basic.";

    @Autowired
    @Qualifier("basicPrimaryWS")
    private BasicPrimaryWS basicPrimaryWS;

    @Autowired
    @Qualifier("basicTraderAPI")
    private BasicTraderAPI basicTraderAPI;

    @Autowired
    @Qualifier("basicSiteQueryWS")
    BasicSiteQueryWS basicSiteQueryWS;

    @Autowired
    @Qualifier("basicPrimaryWSProxy")
    private BasicPrimaryWSProxy basicPrimaryWSProxy;

    @Autowired
    @Qualifier("commonUseMenuApi")
    private CommonUseMenuApi commonUseMenuApi;

    @Autowired
    private BasicSiteUpdateService basicSiteUpdateService;

    @Autowired
    private SiteQueryService siteQueryService;

    @Autowired
    private WaybillPackageContainerApi waybillPackageContainerApi;

    @Autowired
    private SecurityCheckerExecutor securityCheckerExecutor;
    
    @Autowired
    private BasicStaffQueryWS basicStaffQueryWS;

    /**
     * 站点ID
     */
    @Cache(key = "baseMajorManagerImpl.getBaseSiteBySiteId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public BaseStaffSiteOrgDto getBaseSiteBySiteId(Integer paramInteger) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.getBaseSiteBySiteId", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try{
            BaseStaffSiteOrgDto dtoStaff = basicPrimaryWS.getBaseSiteBySiteId(paramInteger);
            ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
            if (dtoStaff != null)
                return dtoStaff;
            else
                dtoStaff = basicPrimaryWS.getBaseStoreByDmsSiteId(paramInteger);
            if (dtoStaff != null)
                return dtoStaff;
            else
                responseDTO = basicTraderAPI.getBasicTraderById(paramInteger);

            if (responseDTO != null && responseDTO.getResult() != null)
                dtoStaff = getBaseStaffSiteOrgDtoFromTrader(responseDTO.getResult());
            return dtoStaff;
        }catch (Exception e){
            Profiler.functionError(info);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
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


    @Override
    @Cache(key = "baseMajorManagerImpl.getSiteBySiteCode@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getSiteBySiteCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseSite getSiteBySiteCode(Integer siteCode) {
        return basicPrimaryWS.getSiteBySiteCode(String.valueOf(siteCode));
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

    @Cache(key = "baseMajorManagerImpl.getBaseSiteByOrgIdSubType@args0@args1", memoryEnable = false, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByOrgId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSubType(Integer orgId, Integer targetType) {
		return basicPrimaryWSProxy.getBaseSiteByOrgIdSubType(orgId,targetType);
    }

    @Cache(key = "baseMajorManagerImpl.getBaseSiteByOrgIdSiteType@args0@args1", memoryEnable = false, memoryExpiredTime = 5 * 60 * 1000,
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
        baseStaffSiteOrgDto.setSitePhone(trader.getTelephone());
        baseStaffSiteOrgDto.setPhone(trader.getContactMobile());
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

    /**
     * 根据字符型编码查询场地数据，包含库房数据，支持 "spwms-605-603"这种查询
     *
     * @param siteCode 场地编码
     * @return 场地信息
     * @author fanggang7
     * @time 2023-07-26 14:36:47 周三
     */
    @Override
    @Cache(key = "baseMajorManagerImpl.getBaseSiteByCodeIncludeStore@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseSiteByCodeIncludeStore", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getBaseSiteByCodeIncludeStore(String siteCode) {
        BaseStaffSiteOrgDto dtoStaff = basicPrimaryWS.getBaseSiteByDmsCode(siteCode);
        ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
        if (dtoStaff != null)
            return dtoStaff;
        else
            dtoStaff = basicPrimaryWS.getBaseStoreByDmsCode(siteCode);

        if (dtoStaff != null){
            return dtoStaff;
        } else{
            responseDTO = basicTraderAPI.getBaseTraderByCode(siteCode);
            if(responseDTO == null || responseDTO.getResult() == null){
                final PsStoreInfo storeInfo = this.getStoreByCode(siteCode);
                if(storeInfo != null){
                    dtoStaff = basicPrimaryWS.getBaseStoreByDmsSiteId(storeInfo.getDmsSiteId());
                    return dtoStaff;
                }
            }
        }

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

    /**
     * 获取库房信息
     * code 如：spwms-709-712
     *
     * @param code
     */
    @Override
    @Cache(key = "baseMajorManagerImpl.getStoreByCode@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
    public PsStoreInfo getStoreByCode(String code) {
        try{
            if(StringUtils.isNotBlank(code)){
                String[] endNodeTmp = code.split(Constants.SEPARATOR_HYPHEN);
                if(endNodeTmp.length == 3){
                    return getStoreByCky2(endNodeTmp[0], Integer.valueOf(endNodeTmp[1]), Integer.valueOf(endNodeTmp[2]));
                }
            }
        }catch (Exception e){
            log.error("根据code获取仓库信息失败：code={}",code, e.getMessage(),e);
        }

        return null;
    }


    @Override
    @Cache(key = "baseMajorManagerImpl.getStoreByCky2@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
    public PsStoreInfo getStoreByCky2(String storeType, Integer cky2, Integer storeID) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.getStoreByCky2", false, true);
        try {
            BaseResult<PsStoreInfo> storeInfoResult = basicPrimaryWS.getStoreByCky2Id(storeType, cky2, storeID, Constants.UMP_APP_NAME_DMSWEB);
            if (0 == storeInfoResult.getResultCode()) {
                return storeInfoResult.getData();
            } else {
                log.warn("根据cky2获取仓库信息失败，接口返回code={}，message ={}",storeInfoResult.getResultCode(),  storeInfoResult.getMessage());
            }
        } catch (Exception ex) {
            log.error("根据cky2获取仓库信息失败：cky2={}，storeID={}",cky2,storeID, ex);
            Profiler.functionError(info);
            return null;
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return null;
    }
    @Cache(key = "baseMajorManagerImpl.getDmsListByOrgId@args0", memoryEnable = false, memoryExpiredTime = 5 * 60 * 1000,
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
                if (log.isInfoEnabled()) {
                    log.debug(com.jd.bluedragon.utils.JsonHelper.toJson(base));
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
            log.error("dmsver获取站点[{}]信息失败，异常为",siteCode, e);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        log.warn("dmsver获取站点[{}]信息失败",siteCode);
        return null;
    }

    @Override
    @Cache(key = "baseMajorManagerImpl.getValidBaseDataDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getValidBaseDataDictList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseDataDict> getValidBaseDataDictList(Integer parentGroup, Integer nodeLevel, Integer typeGroup) {
        return basicPrimaryWS.getValidDataDict(parentGroup, nodeLevel, typeGroup);
    }

    /**
     * 基础资料字典值 集合转换成MAP 方便定位。 key 对应typeCode
     * @param parentGroup
     * @param nodeLevel
     * @param typeGroup
     * @return
     */
    @Override
    @Cache(key = "baseMajorManagerImpl.getValidBaseDataDictListToMap@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getValidBaseDataDictListToMap", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Map<Integer,BaseDataDict> getValidBaseDataDictListToMap(Integer parentGroup, Integer nodeLevel, Integer typeGroup){
        Map<Integer,BaseDataDict> result = new HashMap<Integer, BaseDataDict>();

        List<BaseDataDict> baseDataDicts = basicPrimaryWS.getValidDataDict(parentGroup, nodeLevel, typeGroup);
        if(baseDataDicts!=null && baseDataDicts.size()>0){

            for(BaseDataDict baseDataDict: baseDataDicts){
                result.put(baseDataDict.getTypeCode(),baseDataDict);
            }
        }
        return result;
    }

    @Override
    @Cache(key = "baseMajorManagerImpl.getBaseStaffListByOrgId@args02", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public List<BaseStaffSiteOrgDto> getBaseStaffListByOrgId(Integer orgid, int num) {
        return basicPrimaryWSProxy.getBaseStaffListByOrgId(orgid, 2);
    }

    @Override
    public BaseStaffSiteOrgDto getThirdStaffByJdAccountNoCache(String jdAccount) {
        return basicPrimaryWS.getThirdStaffByJdAccount(jdAccount);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.baseMajorManagerImpl.getBaseStaffByStaffIdNoCache", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getBaseStaffByStaffIdNoCache(Integer paramInteger) {
        return basicPrimaryWS.getBaseStaffByStaffId(paramInteger);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.baseMajorManagerImpl.getBaseStaffIgnoreIsResignByStaffIdNoCache", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getBaseStaffIgnoreIsResignByStaffIdNoCache(Integer userId) {
        return basicPrimaryWS.getBaseStaffIgnoreIsResignByStaffId(userId);
    }

    @Override
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseStaffByErpNoCache", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseStaffSiteOrgDto getBaseStaffByErpNoCache(String erp) {
        return basicPrimaryWS.getBaseStaffByErp(erp);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseStaffByErpCache", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "baseMajorManagerImpl.getBaseStaffByErpCache@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public BaseStaffSiteOrgDto getBaseStaffByErpCache(String erp) {
        return basicPrimaryWS.getBaseStaffByErp(erp);
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseStaffIgnoreIsResignByErp", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "baseMajorManagerImpl.getBaseStaffIgnoreIsResignByErp@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public BaseStaffSiteOrgDto getBaseStaffIgnoreIsResignByErp(String erpCode) {
        return basicPrimaryWS.getBaseStaffIgnoreIsResignByErp(erpCode);
    }

    /**
     * 根据员工编号获取用户信息
     * @param staffNo 员工编号
     * @return 员工信息
     * @author fanggang7
     * @time 2020-11-05 13:56:29 周四
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseStaffInAllRoleByStaffNo", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "baseMajorManagerImpl.getBaseStaffInAllRoleByStaffNo@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public BaseStaffSiteOrgDto getBaseStaffInAllRoleByStaffNo(Integer staffNo) {
        return basicPrimaryWS.getBaseStaffInAllRoleByStaffNo(staffNo);
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
            log.warn("getBaseAllTrader获取数据为空");
        }

        log.info("getBaseAllTrader获取数据count[{}]",count);
        log.info("getBaseAllTrader获取数据耗时[{}]",(System.currentTimeMillis() - startTime));
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

    /**
     * 根据站点ID查询站点和扩展信息，返回值包含归属站点ID和名称（belongCode,belongName）
     * @param sitecode 三方-合作站点ID
     * @return
     */
    @Override
    @Cache(key = "baseMajorManagerImpl.getPartnerSiteBySiteId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getPartnerSiteBySiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer getPartnerSiteBySiteId(Integer sitecode){
        SiteExtensionDTO result = basicSiteQueryWS.getSiteExtensionBySiteId(sitecode);

        if(result == null || result.getBelongCode() == null || result.getBelongCode() <= 0){
            return -1;
        }

        return result.getBelongCode();
    }

    @Cache(key = "baseMajorManagerImpl.allCityBindDms", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getAllCityBindDms", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BaseDataDict> getAllCityBindDms(){
        Integer parentId= 156;
        Integer nodeLevelSecond = 2;
        Integer nodeLevelThird = 3;
        Integer typeGroup = 156;
        List<BaseDataDict> cityAndDmsList = new ArrayList<BaseDataDict>();
        List<BaseDataDict> baseDataDictList = basicPrimaryWS.getValidDataDict(parentId,nodeLevelSecond,typeGroup);
        if(baseDataDictList != null){
            cityAndDmsList.addAll(baseDataDictList);
            for(BaseDataDict baseDateDict : baseDataDictList){
                List<BaseDataDict> subList = basicPrimaryWS.getValidDataDict(baseDateDict.getTypeCode(),nodeLevelThird,typeGroup);
                if (subList != null && subList.size()>0){
                    cityAndDmsList.addAll(subList);
                }
            }
        }
        return cityAndDmsList;
    }
    @Cache(key = "baseMajorManagerImpl.getBaseSiteInfoBySiteId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = UMP_KEY_PREFIX + "basicSiteQueryWS.getBaseSiteInfoBySiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public BaseSiteInfoDto getBaseSiteInfoBySiteId(Integer siteId) {
		return basicSiteQueryWS.getBaseSiteInfoBySiteId(siteId);
	}

    /**
     * 加盟商基础资料中获取 预付款是否充足
     *
     * @param allianceBusiId
     * @return
     */
    @Override
    @JProfiler(jKey = UMP_KEY_PREFIX + "basicSiteQueryWS.allianceBusiMoneyEnough", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean allianceBusiMoneyEnough(String allianceBusiId) {
        //加盟商无流量下掉 jsf 调用 WaybillManagerApi
        return false;
    }

    /**
     * 获取常用功能
     * @param siteCode
     * @param erp
     * @return
     */
    @Override
    public String menuConstantAccount(String siteCode,String erp,Integer source){
        MenuPdaRequest request = new MenuPdaRequest();
        request.setOperatorErp(erp);
        request.setSiteCode(siteCode);
        request.setSource(source);
        com.jd.bluedragon.sdk.modules.quarantine.dto.BaseResult<String> result =  commonUseMenuApi.getMenuConstantAccount(request);
        if(result!=null && result.getStatusCode() == com.jd.bluedragon.sdk.modules.quarantine.dto.BaseResult.SUCCESS_CODE) {
            return result.getData();
        }
        return "[]";
    }


    @Cache(key = "baseMajorManagerImpl.getDmsCustomSiteBySiteId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsCustomSiteBySiteId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public DmsCustomSite getDmsCustomSiteBySiteId(Integer paramInteger) {
        DmsCustomSite dmsCustomSite = new DmsCustomSite();

        BaseStaffSiteOrgDto dtoStaff = basicPrimaryWS.getBaseSiteBySiteId(paramInteger);
        ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
        if(dtoStaff != null) {
            dmsCustomSite.setCustomSiteType(DmsCustomSite.CUSTOM_SITE_TYPE_SITE);
        }

        if(dtoStaff == null){
            dtoStaff = basicPrimaryWS.getBaseStoreByDmsSiteId(paramInteger);
            if(dtoStaff != null) {
                dmsCustomSite.setCustomSiteType(DmsCustomSite.CUSTOM_SITE_TYPE_WMS);
            }
        }
        if(dtoStaff == null){
            responseDTO = basicTraderAPI.getBasicTraderById(paramInteger);
            if(responseDTO != null&& responseDTO.getResult() != null){
                dtoStaff = getBaseStaffSiteOrgDtoFromTrader(responseDTO.getResult());
            }
            if(dtoStaff != null) {
                dmsCustomSite.setCustomSiteType(DmsCustomSite.CUSTOM_SITE_TYPE_B_ENTERPRISE);
            }
        }
        if(dtoStaff != null) {

            dmsCustomSite.setSiteId(dtoStaff.getSiteCode());
            dmsCustomSite.setSiteCode(dtoStaff.getDmsSiteCode());
            dmsCustomSite.setSiteName(dtoStaff.getSiteName());
            dmsCustomSite.setSiteType(dtoStaff.getSiteType());
            dmsCustomSite.setSubType(dtoStaff.getSubType());
        }

        return dmsCustomSite;
    }

    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getBaseTraderNeccesaryInfoById", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "DMS.BASE.BaseMajorManagerImpl.getBaseTraderNeccesaryInfoById@args0", memoryEnable = true, memoryExpiredTime = 1 * 60 * 1000,
            redisEnable = false, redisExpiredTime = 2 * 60 * 1000)
    public BasicTraderNeccesaryInfoDTO getBaseTraderNeccesaryInfoById(Integer merchantId) {
        ResponseDTO<BasicTraderNeccesaryInfoDTO> responseDTO
                = basicTraderAPI.getBaseTraderNeccesaryInfoById(merchantId);
        if(responseDTO != null && responseDTO.isSuccess()){
            return responseDTO.getResult();
        }else {
            log.warn("通过商家ID{}查询商家信息失败!",merchantId);
            return null;
        }
    }

    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.updateBaseSiteBasicProperty", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean updateBaseSiteBasicProperty(BaseStaffSiteDTO baseStaffSiteDTO) {
        BaseResult<String> baseResult = basicSiteUpdateService.updateBaseSiteBasicProperty(baseStaffSiteDTO);
        if(baseResult != null && baseResult.getResultCode() == BaseResult.RESULT_SUCCESS){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 根据条件查询站点
     * @param siteQueryCondition
     * @param limit
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.querySiteByConditionFromEs", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<BasicSite> querySiteByConditionFromEs(SiteQueryCondition siteQueryCondition, Integer limit) {
        return siteQueryService.querySiteByConditionFromEs(siteQueryCondition, limit);
    }

    /**
     * 根据条件查询站点
     * 		<p>
     * 		    查询精简站点数据
     * 		</p>
     * @param siteQueryCondition
     * @param limit
     * @return
     */
    @Override
    public List<StreamlinedBasicSite> querySiteByConditionFromStreamlinedSite(StreamlinedSiteQueryCondition siteQueryCondition, Integer limit) {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.querySiteByConditionFromStreamlinedSite",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try{
            BaseEntity<List<StreamlinedBasicSite>> baseEntity = siteQueryService.querySiteByConditionFromStreamlinedSite(siteQueryCondition, limit);
            if(baseEntity == null || !baseEntity.isSuccess()){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            log.error("根据条件查询站点异常！",e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    /**
     * 根据条件查询站点
     * @return 分页数据
     * @author fangang7
     * @time 2022-10-12 16:22:24 周三
     */
    @Override
    public BaseEntity<com.jd.bluedragon.common.dto.base.request.Pager<StreamlinedBasicSite>> querySiteByConditionFromStreamlinedSite(com.jd.bluedragon.common.dto.base.request.Pager<StreamlinedBasicSiteQuery> request) {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.BASE.BaseMajorManagerImpl.querySiteByConditionFromStreamlinedSite",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        BaseEntity<com.jd.bluedragon.common.dto.base.request.Pager<StreamlinedBasicSite>> result = new BaseEntity<>();
        try{
            com.jd.bluedragon.common.dto.base.request.Pager<StreamlinedBasicSite> pagerData = new com.jd.bluedragon.common.dto.base.request.Pager<>(request.getPageNo(), request.getPageSize(), 0L);
            pagerData.setData(new ArrayList<StreamlinedBasicSite>());
            result.setData(pagerData);

            com.jd.ql.dms.report.domain.Pager<StreamlinedSiteQueryCondition> pagerRequest = new com.jd.ql.dms.report.domain.Pager<>();
            final StreamlinedSiteQueryCondition siteQueryCondition = new StreamlinedSiteQueryCondition();
            BeanCopyUtil.copy(request.getSearchVo(), siteQueryCondition);
            pagerRequest.setSearchVo(siteQueryCondition);
            pagerRequest.setPageNo(request.getPageNo());
            pagerRequest.setPageSize(request.getPageSize());
            BaseEntity<com.jd.ql.dms.report.domain.Pager<StreamlinedBasicSite>> queryResultEntity = siteQueryService.querySitePageByConditionFromStreamlinedSite(pagerRequest);
            if(queryResultEntity == null || !queryResultEntity.isSuccess()){
                log.warn("siteQueryService.querySitePageByConditionFromStreamlinedSite error {}", JsonHelper.toJson(queryResultEntity));
                result.setCode(BaseEntity.CODE_SERVICE_ERROR);
                result.setMessage("根据条件查询站点异常");
                return result;
            }
            final com.jd.ql.dms.report.domain.Pager<StreamlinedBasicSite> queryResultData = queryResultEntity.getData();
            if(queryResultData != null){
                pagerData.setTotal(queryResultData.getTotal());
                pagerData.setData(queryResultData.getData());
            }
            return result;
        }catch (Exception e){
            log.error("根据条件查询站点异常！",e);
            Profiler.functionError(callerInfo);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage("根据条件查询站点异常");
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    /**
     * 校验冷链配置路由
     * @param createSiteCode
     * @param endSiteCode
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.validateDirectlySentLine", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "DMS.BASE.BaseMajorManagerImpl.validateDirectlySentLine@args0@args1", memoryEnable = true, memoryExpiredTime = 1 * 60 * 1000,
            redisEnable = false, redisExpiredTime = 2 * 60 * 1000)
    public boolean validateDirectlySentLine(Integer createSiteCode,Integer endSiteCode) {
        BaseStaffSiteOrgDto boxCreateSiteDto = getBaseSiteBySiteId(createSiteCode);
        BaseStaffSiteOrgDto boxReceiveSite = getBaseSiteBySiteId(endSiteCode);
        BaseResponse baseResponse = waybillPackageContainerApi.validateDirectlySentLine(boxCreateSiteDto.getDmsSiteCode(), boxReceiveSite.getDmsSiteCode());
        if(baseResponse != null && BaseResponse.OK_CODE == baseResponse.getCode()){
            return (boolean)baseResponse.getData();
        }else {
            log.warn("校验路由信息失败!起始分拣中心code{},目的分拣中心code{},",boxCreateSiteDto.getDmsSiteCode(),boxReceiveSite.getDmsSiteCode());
            return false;
        }
    }

    @JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.securityCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<Boolean> securityCheck(WaybillPrintRequest request) {
        // 分拣打印才执行安全校验
        if(WaybillPrintOperateTypeEnum.isInnerPrint(request.getOperateType())){
            String userErp = request.getUserERP();
            if(org.apache.commons.lang.StringUtils.isEmpty(userErp) && request.getUserCode() != null){
                BaseStaffSiteOrgDto baseStaff = getBaseStaffByStaffId(request.getUserCode());
                userErp = baseStaff == null ? null : baseStaff.getErp();
            }
            return securityCheckerExecutor.verifyWaybillDetailPermission(SecurityDataMapFuncEnum.WAYBILL_PRINT, userErp, WaybillUtil.getWaybillCode(request.getBarCode()));
        }
        return new InvokeResult<>();
    }

    @Cache(key = "BaseMajorManager.queryBaseStaffByRole@args0@args1", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Override
    public List<BaseStaffSiteDTO> queryBaseStaffByRole(String provinceAgencyCode, Integer role) {
        CallerInfo callerInfo = Profiler.registerInfo("com.jd.bluedragon.core.base.BaseMajorManager.queryBaseStaffByRole",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        List<BaseStaffSiteDTO> list = Lists.newArrayList();
        try{
            if(StringUtils.isEmpty(provinceAgencyCode) || role == null){
                return list;
            }
            BaseStaffSiteDTO baseStaffSiteDTO = new BaseStaffSiteDTO();
            baseStaffSiteDTO.setProvinceAgencyCode(provinceAgencyCode);
            baseStaffSiteDTO.setRole(role);
            // 当前页
            int pageIndex = 0;
            while (pageIndex ++ < 100){
                PageDto<List<BaseStaffSiteDTO>> singlePage = basicStaffQueryWS.queryBaseStaff(baseStaffSiteDTO, pageIndex);
                if(singlePage == null || CollectionUtils.isEmpty(singlePage.getData())){
                    break;
                }
                list.addAll(singlePage.getData());
            }
        }catch (Exception e){
            log.error("查询省区:{}下角色:{}的员工信息异常！",provinceAgencyCode, role, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return list;
    }

    @Override
    @Cache(key = "BaseMajorManager.checkIDCardNoExists@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public BaseStaff checkIDCardNoExists(String idCardNo) {
        if(StringUtils.isNotBlank(idCardNo)){
            BaseResult<BaseStaff> result = basicStaffQueryWS.checkIDCardNoExists(idCardNo);
            if(result != null && BaseResult.RESULT_SUCCESS== result.getResultCode()){
                return result.getData();
            }
        }
        return null;
    }



    @Override
    @Cache(key = "BaseMajorManager.getThirdStaffByUserCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public BaseStaffSiteOrgDto getThirdStaffByUserCode(String userCode) {
        if(StringUtils.isNotBlank(userCode)){
            return basicStaffQueryWS.getThirdStaffByUserCode(userCode);
        }
        return null;
    }

    @Override
    @Cache(key = "BaseMajorManager.queryBaseStaffByStaffId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public BaseStaffSiteDTO queryBaseStaffByStaffId(Integer staffId) {
        if(staffId != null){
            ResultData<BaseStaffSiteDTO> resultData = basicStaffQueryWS.queryBaseStaffByStaffId(staffId);
            if(resultData != null && resultData.getData() != null){
                return resultData.getData();
            }
        }
        return null;
    }

    @Override
    @Cache(key = "BaseMajorManager.selectBaseStoreByDmsSiteId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public PsStoreInfo selectBaseStoreByDmsSiteId(Integer siteId) {
        return basicPrimaryWS.selectBaseStoreByDmsSiteId(siteId);
    }
}
