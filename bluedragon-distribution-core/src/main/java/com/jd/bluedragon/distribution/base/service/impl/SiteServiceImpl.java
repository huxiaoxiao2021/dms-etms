package com.jd.bluedragon.distribution.base.service.impl;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.BasicSelectWsManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.distribution.site.dao.SiteMapper;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.proxy.VtsQueryWSProxy;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.tms.basic.dto.TransportResourceDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("siteService")
public class SiteServiceImpl implements SiteService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    RedisManager redisManager;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private BaseMinorManager baseMinorManager;
    @Autowired
    private VtsQueryWS vtsQueryWS;
    @Autowired
    private VtsQueryWSProxy vtsQueryWSProxy;
    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BasicSelectWsManager basicSelectWsManager;

    @Autowired
    SiteMapper siteMapper;

    @Autowired
    private ReassignWaybillService reassignWaybillService;

    public BaseStaffSiteOrgDto getSite(Integer siteCode) {
        return this.baseMajorManager.getBaseSiteBySiteId(siteCode);
    }

    public BasicTraderInfoDTO getTrader(Integer siteCode) {
        return this.baseMinorManager.getBaseTraderById(siteCode);
    }

    //车辆管理系统获取运力编码
    @Override
    public RouteTypeResponse getCapacityCodeInfo(String capacityCode) {
        CommonDto<VtsTransportResourceDto> vtsDto = vtsQueryWS.getTransportResourceByTransCode(capacityCode);
        RouteTypeResponse base = new RouteTypeResponse();
        if (vtsDto == null) {    //JSF接口返回空
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage("查询运力信息结果为空:" + capacityCode);
            return base;
        }
        if (Constants.RESULT_SUCCESS == vtsDto.getCode()) { //JSF接口调用成功
            VtsTransportResourceDto vtrd = vtsDto.getData();
            if (vtrd != null) {
                base.setSiteCode(vtrd.getEndNodeId());
                base.setSendUserType(vtrd.getTransType());
                base.setDriverId(vtrd.getCarrierId());
                base.setRouteType(vtrd.getRouteType()); // 增加运输类型返回值
                base.setDriver(vtrd.getCarrierName());
                base.setTransWay(vtrd.getTransMode());
                base.setCarrierType(vtrd.getTransType());
                base.setCode(JdResponse.CODE_OK);
                base.setMessage(JdResponse.MESSAGE_OK);
            } else {
                base.setCode(JdResponse.CODE_SERVICE_ERROR);
                base.setMessage("查询运力信息结果为空:" + capacityCode);
            }
        } else if (Constants.RESULT_WARN == vtsDto.getCode()) {    //查询运力信息接口返回警告，给出前台提示
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage(vtsDto.getMessage());
        } else { //服务出错或者出异常，打日志
            base.setCode(JdResponse.CODE_SERVICE_ERROR);
            base.setMessage("查询运力信息出错！");
            log.warn("查询运力信息出错,运力编码:{}，出错原因:{}",capacityCode, vtsDto.getMessage());
        }
        return base;
    }

    /**
     * 运力编码打印信息查询
     * 运输接口人 : xuzhangwang
     * @param request
     * @return
     */
    @Override
    public CapacityCodeResponse queryCapacityCodeInfo(CapacityCodeRequest request) {
        CapacityCodeResponse response = new CapacityCodeResponse();
        TransportResourceDto transportResourceDto = new TransportResourceDto();

        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        try {
            // 始发区域
            if (request.getSorgid() != null)
                transportResourceDto.setStartOrgCode(String.valueOf(request.getSorgid()));
            // 始发站
            if (request.getScode() != null)
               transportResourceDto.setStartNodeId(request.getScode());
            // 目的区域
            if (request.getRorgid() != null)
                transportResourceDto.setEndOrgCode(String.valueOf(request.getRorgid()));
            // 目的站
            if (request.getRcode() != null)
                transportResourceDto.setEndNodeId(request.getRcode());
            // 线路类型
            if (request.getRouteType() != null)
                transportResourceDto.setTransType(request.getRouteType());
            // 运力类型-承运商类型
            if (request.getTranType() != null)
                transportResourceDto.setCarrierType(request.getTranType());
            // 运输方式
            if (request.getTranMode() != null)
                transportResourceDto.setTransWay(request.getTranMode());
            // 运力编码
            if (request.getTranCode() != null)
                transportResourceDto.setTransCode(request.getTranCode());
            // 承运人信息
            if (request.getCarrierId() != null) {
                //承运商ID
                if (NumberHelper.isNumber(request.getCarrierId()) || request.getCarrierId().equals(Constants.JDZY))
                    transportResourceDto.setCarrierCode(request.getCarrierId());
                    //承运商名称
                else {
                    transportResourceDto.setCarrierName(request.getCarrierId());
                }
            }

            List<TransportResourceDto>  result = basicSelectWsManager.queryPageTransportResourceWithNodeId(transportResourceDto);
            if (CollectionUtils.isEmpty(result)) {
                return response;
            }

            List<CapacityDomain> domainList = new ArrayList<>();
            for (TransportResourceDto dto : result) {
                // 返回客户端所有信息
                CapacityDomain domain = new CapacityDomain();

                // 到车时间
                domain.setArriveTime(String.valueOf(dto.getArriveCarTime()));
                // 承运商
                domain.setCarrierName(String.valueOf(dto.getCarrierName()));
                // 目的站
                domain.setRcode(String.valueOf(dto.getEndNodeCode()));
                domain.setRname(dto.getEndNodeName());
                // 目的区域
                domain.setRorgid(String.valueOf(dto.getEndOrgCode()));
                domain.setRorgName(dto.getEndOrgName());
                // 线路类型
                domain.setRouteType(String.valueOf(dto.getTransType()));
                // 始发站
                domain.setScode(String.valueOf(dto.getStartNodeCode()));
                domain.setSname(dto.getStartNodeName());
                // 发车时间
                domain.setSendTime(String.valueOf(dto.getSendCarTime()));
                domain.setSendTimeStr(dto.getSendCarTimeStr());
                // 始发区域
                domain.setSorgid(String.valueOf(dto.getStartOrgCode()));
                domain.setSorgName(dto.getStartOrgName());
                // 运力编码
                domain.setTranCode(String.valueOf(dto.getTransCode()));
                // 运输方式
                domain.setTranMode(String.valueOf(dto.getTransWay()));
                // 运力类型
                domain.setTranType(String.valueOf(dto.getTransType()));
                // 在途时长
                domain.setTravelTime(String.valueOf(dto.getTravelTime()));
                // 承运商名称
                domain.setCarrierName(dto.getCarrierName());
                // 承运商ID
                domain.setCarrierId(String.valueOf(dto.getCarrierCode()));
                //航空班次
                domain.setAirShiftName(dto.getAirShiftName());
                //运力状态-
                domain.setEffectiveStatus(dto.getEffectiveStatus());
                //运力生效时间
                if(dto.getTransEnableTime()!=null){
                    domain.setTransEnableTime(DateHelper.formatDate(dto.getTransEnableTime()));
                }
                //运力失效时间
                if(dto.getTransEnableTime()!=null){
                    domain.setTransDisableTime(DateHelper.formatDate(dto.getTransDisableTime()));
                }
                domainList.add(domain);
            }
            if (!CollectionUtils.isEmpty(domainList)) {
                response.setData(domainList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }

        return response;
    }

    @Cache(key = "SiteServiceImpl.getSitesByPage@args0-@args1", memoryEnable = false, redisExpiredTime = 1000 * 60 * 5, redisEnable = true)
    @Override
    public Pager<List<SiteWareHouseMerchant>> getSitesByPage(int category, int pageNo) {
        switch (category) {
            case 1:
                return baseMajorManager.getBaseSiteByPage(pageNo);
            case 2:
                return baseMajorManager.getBaseStoreInfoByPage(pageNo);
            case 3:
                return baseMajorManager.getTraderListByPage(pageNo);
            default:
                return baseMajorManager.getBaseSiteByPage(pageNo);
        }

    }

    @Override
    public CreateAndReceiveSiteInfo getCreateAndReceiveSiteBySendCode(String sendCode) {
        Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(sendCode);
        if (siteCodes[0] == -1 || siteCodes[1] == -1) {
            return null;
        }
        CreateAndReceiveSiteInfo createAndReceiveSite = new CreateAndReceiveSiteInfo();
        BaseStaffSiteOrgDto createSite = this.getSite(siteCodes[0]);
        BaseStaffSiteOrgDto receiveSite = this.getSite(siteCodes[1]);
        if(createSite != null){
            createAndReceiveSite.setCreateSiteCode(createSite.getSiteCode());
            createAndReceiveSite.setCreateSiteName(createSite.getSiteName());
            createAndReceiveSite.setCreateSiteType(createSite.getSiteType());
            createAndReceiveSite.setCreateSiteSubType(createSite.getSubType());
        }

        if(receiveSite != null){
            createAndReceiveSite.setReceiveSiteCode(receiveSite.getSiteCode());
            createAndReceiveSite.setReceiveSiteName(receiveSite.getSiteName());
            createAndReceiveSite.setReceiveSiteType(receiveSite.getSiteType());
            createAndReceiveSite.setReceiveSiteSubType(receiveSite.getSubType());
        }
        return createAndReceiveSite;
    }

    /**
     * 获取属于北京的分拣中心列表
     */
    @Cache(key = "SiteServiceImpl.getBjDmsSiteCodes", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    @Override
    public Set<Integer> getBjDmsSiteCodes() {
        return this.getSiteCodesFromSysConfig(Constants.SYS_CONFIG_NAME_BJ_DMS_SITE_CODES);
    }

    /**
     * 从sysconfig表里查出来开放C网路由校验的分拣中心列表
     *
     * @return
     */
    @Cache(key = "SiteServiceImpl.getCRouterAllowedList", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    @Override
    public Set<Integer> getCRouterAllowedList() {
        Set<Integer> CRouterVerifyOpenDms = new TreeSet<Integer>();
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_CONFIG_CROUTER_OPEN_DMS_CODES);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String contents = sysConfigs.get(0).getConfigContent();
            Set<String> sites = StringHelper.splitToSet(contents, Constants.SEPARATOR_COMMA);
            for (String site : sites) {
                CRouterVerifyOpenDms.add(Integer.valueOf(site));
            }
        }
        return CRouterVerifyOpenDms;
    }
    /**
     * 从sysconfig表里查出来箱号需要由中台生产的分拣中心列表
     *
     * @return
     */
    @Cache(key = "SiteServiceImpl.getBoxFromSSCAllowedList", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    @Override
    public Set<Integer> getBoxFromSSCAllowedList() {
        Set<Integer> result = new TreeSet<>();
        List<SysConfig> sysConfigList = sysConfigService.getListByConfigName(Constants.CREATE_BOX_FROM_SSC_SITE);
        if (sysConfigList != null && !sysConfigList.isEmpty()) {
            Set<String> sites = StringHelper.splitToSet(sysConfigList.get(0).getConfigContent(), Constants.SEPARATOR_COMMA);
            for (String site : sites) {
                result.add(Integer.valueOf(site));
            }
        }
        return result;
    }

    /**
     * 从sysconfig表里查出来箱号需要由DMS生产的分拣中心列表
     *
     * @return
     */
    @Cache(key = "SiteServiceImpl.getBoxFromDMSAllowedList", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, redisEnable = false)
    @Override
    public Set<Integer> getBoxFromDMSAllowedList() {
        Set<Integer> result = new TreeSet<>();
        List<SysConfig> sysConfigList = sysConfigService.getListByConfigName(Constants.CREATE_BOX_FROM_DMS_SITE);
        if (sysConfigList != null && !sysConfigList.isEmpty()) {
            Set<String> sites = StringHelper.splitToSet(sysConfigList.get(0).getConfigContent(), Constants.SEPARATOR_COMMA);
            for (String site : sites) {
                result.add(Integer.valueOf(site));
            }
        }
        return result;
    }

    /**
     * 获取区域内的所有分拣中心
     * 如果orgId为-1，则获取全国所有的分拣中心
     *
     * @param areaId
     * @return
     */
    public List<BaseStaffSiteOrgDto> getDmsListByAreaId(Integer areaId) {
        List<BaseStaffSiteOrgDto> orgDmsList = new ArrayList<BaseStaffSiteOrgDto>();
        if (AreaHelper.isNotEmptyAndTitle(areaId)) {
            orgDmsList.addAll(baseMajorManager.getBaseSiteByOrgIdSiteType(areaId, Constants.DMS_SITE_TYPE));
        } else {
            orgDmsList.addAll(getAllDmsSite());
        }
        return orgDmsList;
    }
    /**
     * 获取所有分拣中心
     *
     * @return
     */
    @Override
    public List<BaseStaffSiteOrgDto> getAllDmsSite() {
        List<BaseStaffSiteOrgDto> list = this.baseMajorManager.getBaseSiteByOrgIdSiteType(null, Constants.DMS_SITE_TYPE);
        if (list != null && list.size() > 0) {
            return list;
        }
        return new ArrayList<BaseStaffSiteOrgDto>();
    }

    @Override
    public List<BaseStaffSiteOrgDto> getDmsListByCity(Integer cityId) {
        //获取区域内的所有分拣中心
        List<BaseStaffSiteOrgDto> allSites = getAllDmsSite();

        //遍历分拣中心根据城市id进行过滤
        List<BaseStaffSiteOrgDto> data = new ArrayList<BaseStaffSiteOrgDto>();
        for (BaseStaffSiteOrgDto site : allSites) {
            if (cityId.equals(site.getCityId()) ) {
                data.add(site);
            }
        }
        return data;
    }

    @Override
    public List<BaseStaffSiteOrgDto> getDmsListByProvince(Integer provinceId) {
        //获取区域内的所有分拣中心
        List<BaseStaffSiteOrgDto> allSites = getAllDmsSite();

        //遍历分拣中心根据城市id进行过滤
        List<BaseStaffSiteOrgDto> data = new ArrayList<BaseStaffSiteOrgDto>();
        for (BaseStaffSiteOrgDto site : allSites) {
            if (provinceId.equals(site.getProvinceId()) ) {
                data.add(site);
            }
        }
        return data;
    }

    /**
     * 根据站点id获取站点名称
     * @param siteCode
     * @return
     */
    public String getSiteNameByCode(Integer siteCode){
        if(siteCode != null && siteCode > 0){
            BaseStaffSiteOrgDto dto = getSite(siteCode);
            if(dto!= null){
                return dto.getSiteName();
            }
        }
        return null;
    }

    @Cache(key = "SiteServiceImpl.getCityBindDmsCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public Integer getCityBindDmsCode(Integer cityId){
        if (cityId != null && cityId > 0) {
            List<BaseDataDict> cityAndDmsList = baseMajorManager.getAllCityBindDms();
            if(cityAndDmsList != null && cityAndDmsList.size() < 1){
                for(BaseDataDict dataDict : cityAndDmsList){
                    if(dataDict.getTypeName().equals(cityId.toString())){
                        return dataDict.getTypeCode();
                    }
                }
            }
        }
        return null;
    }
	/**
	 * 从系统配置表sysconfig，根据配置名称获取站点编码列表，站点编码以‘,’隔开
	 * @param sysConfigName
	 * @return
	 */
    @Cache(key = "SiteServiceImpl.getSiteCodesFromSysConfig@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    @Override
    public Set<Integer> getSiteCodesFromSysConfig(String sysConfigName) {
        Set<Integer> bjDmsSiteCodes = new HashSet<Integer>();
        if(StringHelper.isNotEmpty(sysConfigName)){
            List<SysConfig> bjDmsSiteConfigs = sysConfigService.getListByConfigName(sysConfigName);
            if (bjDmsSiteConfigs != null && !bjDmsSiteConfigs.isEmpty()) {
                String contents = bjDmsSiteConfigs.get(0).getConfigContent();
                Set<String> sites = StringHelper.splitToSet(contents, Constants.SEPARATOR_COMMA);
                for (String site : sites) {
                    bjDmsSiteCodes.add(Integer.valueOf(site));
                }
            }
        }
        return bjDmsSiteCodes;
    }

    @Override
    public List<BaseStaffSiteOrgDto> fuzzyGetSiteBySiteName(String siteName) {
        if (StringHelper.isEmpty(siteName)) {
            return Collections.emptyList();
        }
        String url = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS") + "/services/bases/siteFuzzyByName/" + siteName;
        List<SiteEntity> siteEntities = RestHelper.jsonGetForEntity(url,new TypeToken<List<SiteEntity>>(){}.getType());
        if (null == siteEntities || siteEntities.isEmpty()) {
        	return Collections.emptyList();
        }
        List<BaseStaffSiteOrgDto> res = new ArrayList<>();
        for (SiteEntity siteEntity : siteEntities) {
            BaseStaffSiteOrgDto siteOrgDto = new BaseStaffSiteOrgDto();
            siteOrgDto.setSiteCode(siteEntity.getCode());
            siteOrgDto.setSiteName(siteEntity.getName());
            siteOrgDto.setSiteType(siteEntity.getType());
            res.add(siteOrgDto);
        }
        return res;
    }

    @Override
    @Cache(key = "SiteServiceImpl.get@args0",  memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public Site get(Integer siteCode) {
        if (siteCode == null) {
            return null;
        }
        Site site = this.siteMapper.get(siteCode);
        if (site == null) {
            site = getNoCache(siteCode);
        }
        //需要补上site的subType
        return dealSiteType(site);
    }

    @Override
    public Site getNoCache(Integer siteCode) {
        BaseStaffSiteOrgDto dto = null;
        try {
            dto = baseMajorManager.getBaseSiteBySiteId(siteCode);
            if (null == dto) {
                log.warn("根据编码获取site信息为空：{}", siteCode);
                return null;
            } else {
                Site temp = new Site();
                temp.setCode(dto.getSiteCode());
                temp.setName(dto.getSiteName());
                temp.setDmsCode(dto.getDmsSiteCode() != null ? dto.getDmsSiteCode() : "");
                temp.setSubType(dto.getSubType());
                temp.setType(dto.getSiteType());
                temp.setOrgId(dto.getSubType());
                temp.setSiteBusinessType(dto.getSiteBusinessType());
                return temp;
            }
        } catch (Exception e) {
            log.error("根据编码获取site信息失败：{}", siteCode, e);
            return null;
        }
    }

    @Override
    public Integer getLastScheduleSite(String packageCode) {
        if(StringHelper.isEmpty(packageCode)){
            log.warn("获取包裹最后一次反调度站点失败，参数包裹号为空。");
            return null;
        }

        ReassignWaybill reassignWaybill = null;
        try{
            if(WaybillUtil.isPackageCode(packageCode))//判断是否是包裹号
                reassignWaybill = reassignWaybillService.queryByPackageCode(packageCode);
            else                                         //否则默认按运单号处理
                reassignWaybill = reassignWaybillService.queryByWaybillCode(packageCode);
        }catch(Exception e){
            log.error("获取包裹 [{}] 最后一次反调度站点异常，原因：",packageCode, e);
            return null;
        }
        if(null == reassignWaybill){
            log.warn("获取包裹 [{}] 最后一次反调度站点失败，反调度站点为空", packageCode);
            return null;
        }
        return reassignWaybill.getChangeSiteCode();
    }

    /**
     * 根据分拣中心编码获取分拣中心名称并截取掉 ”分拣中心","中转场","分拨中心"等
     * @param dmsCode
     * @return
     */
    @Override
    public String getDmsShortNameByCode(Integer dmsCode){
        Site site = get(dmsCode);
        if(site == null){
            return null;
        }

        //读取站点名称
        String siteName = site.getName();

        if (StringHelper.isEmpty(siteName)){
            return null;
        }
        //截取分拣中心、分拨中心、中转场
        return siteName.replace(Constants.SUFFIX_DMS_ONE,"").replace(Constants.SUFFIX_DMS_TWO,"").replace(Constants.SUFFIX_TRANSIT,"");
    }

    private Site dealSiteType(Site site) {
        if (site == null) {
            return null;
        }
        if (site.getSubType() == null && site.getType() != null) {
            site.setSubType(site.getType());
        }
        return site;
    }
}
