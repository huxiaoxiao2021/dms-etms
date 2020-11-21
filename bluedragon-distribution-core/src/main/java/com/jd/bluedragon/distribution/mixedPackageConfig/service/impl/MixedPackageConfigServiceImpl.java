package com.jd.bluedragon.distribution.mixedPackageConfig.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.mixedPackageConfig.dao.MixedPackageConfigDao;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.RuleTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.SiteTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.TransportTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.MixedPackageConfigService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.annotation.CacheMethod;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("mixedPackageConfigService")
public class MixedPackageConfigServiceImpl implements MixedPackageConfigService {

    private static final Logger logger = LoggerFactory.getLogger(MixedPackageConfigServiceImpl.class);

    private static final String COLLECTION_ADDRESS_REDIS_KEY_PREFIX = "COLLECTION_ADDRESS_";

    @Autowired
    private MixedPackageConfigDao mixedPackageConfigDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public boolean checkMixedPackageConfig(Integer createSiteCode, Integer reciveSiteCode, Integer mixedSiteCode, Integer transportType, Integer ruleType) {
        MixedPackageConfig mixedPackageConfig = new MixedPackageConfig();
        mixedPackageConfig.setCreateSiteCode(createSiteCode);
        mixedPackageConfig.setReceiveSiteCode(reciveSiteCode);
        mixedPackageConfig.setMixedSiteCode(mixedSiteCode);
        mixedPackageConfig.setTransportType(transportType);
        mixedPackageConfig.setRuleType(ruleType);
        //直接根据预分拣站点查询混装规则
        boolean passFlag = mixedPackageConfigDao.queryConfigs(mixedPackageConfig) > 0 ? true : false;
        // 如果查询不到，则根据预分拣站点绑定的分拣中心进行查询
        if (!passFlag) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = getBaseStaffSiteOrgDtoBySiteCode(mixedSiteCode);
            if (null != baseStaffSiteOrgDto && null != baseStaffSiteOrgDto.getDmsId()) {
                mixedPackageConfig.setMixedSiteCode(baseStaffSiteOrgDto.getDmsId());
                passFlag = mixedPackageConfigDao.queryConfigs(mixedPackageConfig) > 0 ? true : false;
            }
        }
        return passFlag;
    }

    /**
     * 通过混装站点查询站点信息
     *
     * @param mixedSiteCode 混装站点
     */
    private BaseStaffSiteOrgDto getBaseStaffSiteOrgDtoBySiteCode(Integer mixedSiteCode) {

        return baseMajorManager.getBaseSiteBySiteId(mixedSiteCode);
    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<MixedPackageConfig> queryMixedPackageConfigs(MixedPackageConfigRequest mixedPackageConfigRequest, Pager pager) {
        List<MixedPackageConfig> mixedPackageConfigList = new ArrayList<MixedPackageConfig>();
        Integer count = mixedPackageConfigDao.queryMixedPackageConfigCountByRequest(mixedPackageConfigRequest);
        if (pager == null) {
            pager = new Pager();
        }
        if (count != null && count > 0) {
            pager.setTotalSize(count);
            pager.init();
            mixedPackageConfigRequest.setStartIndex(pager.getStartIndex());
            mixedPackageConfigRequest.setPageSize(pager.getPageSize());
            mixedPackageConfigList = mixedPackageConfigDao.queryMixedPackageConfigs(mixedPackageConfigRequest);
        }

        return getEnumNameByCode(mixedPackageConfigList);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateConfigYNById(Integer id, Integer userCode, String userName) {
        MixedPackageConfig mixedPackageConfig = new MixedPackageConfig();
        mixedPackageConfig.setId(id);
        mixedPackageConfig.setUpdateUser(userName);
        mixedPackageConfig.setUpdateUserCode(userCode);
        mixedPackageConfig.setTs(System.currentTimeMillis());
        return mixedPackageConfigDao.updateConfigYNById(mixedPackageConfig);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<MixedPackageConfig> querySelectedConfigs(MixedPackageConfigRequest mixedPackageConfigRequest) {
        return mixedPackageConfigDao.querySelectedConfigs(mixedPackageConfigRequest);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer saveConfigs(MixedPackageConfigRequest request, Integer userCode, String userName) {
        List<MixedPackageConfig> mixedPackageConfigList = new ArrayList<MixedPackageConfig>();
        mixedPackageConfigList.addAll(getMixedPackageConfigList(request, userCode, userName));
        return mixedPackageConfigDao.saveConfigs(mixedPackageConfigList);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateConfigs(MixedPackageConfigRequest mixedPackageConfigRequest, Integer userCode, String userName) {
        List<String> valueList = new ArrayList();
        for (String value : mixedPackageConfigRequest.getMixedSiteList()) {
            valueList.add(value.split("___")[0]);
        }
        mixedPackageConfigRequest.setMixedSiteList(valueList);
        mixedPackageConfigRequest.setUserCode(userCode);
        mixedPackageConfigRequest.setUserName(userName);
        mixedPackageConfigRequest.setTs(System.currentTimeMillis());
        return mixedPackageConfigDao.updateConfigs(mixedPackageConfigRequest);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<MixedPackageConfig> queryConfigsForPrint(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType) {
        MixedPackageConfig mixedPackageConfig = new MixedPackageConfig();
        mixedPackageConfig.setCreateSiteCode(createSiteCode);
        mixedPackageConfig.setReceiveSiteCode(receiveSiteCode);
        mixedPackageConfig.setTransportType(transportType);
        mixedPackageConfig.setRuleType(ruleType);
        return mixedPackageConfigDao.queryConfigsForPrint(mixedPackageConfig);
    }

    /**
     * 查询集包地
     * <p>
     *     1、面单上始发对应箱号始发；面单上目的对应可混装目的地；面单上运输方式对应承运类型；面单上集包地对应箱号目的地
     *     2、面单打印【航】运输方式优先取航空运输，未获取到则取公路运输，面单不打印【航】则取公路运输
     *     3、面单上目的地不在可混装目的地中，则从箱号目的地中查询
     * </p>
     * @param request
     * @return
     */
    @Override
    @CacheMethod(key="MixedPackageConfigServiceImpl.queryMixedSiteCodeForPrint-{0.originalDmsCode}-{0.destinationDmsCode}" +
            "-{0.transportType}-{0.ruleType}",cacheBean="redisCache", nullTimeout = 1000 * 60 * 10, timeout=1000 * 60 * 10)
    public MixedSite queryMixedSiteCodeForPrint(PrintQueryRequest request) {
        if(request == null || request.getOriginalDmsCode() == null
                || request.getDestinationDmsCode() == null
                || request.getTransportType() == null
                || !TransportTypeEnum.transportTypeMap.containsKey(request.getTransportType())){
            logger.warn("参数错误!");
            return null;
        }
        try {
            MixedPackageConfig mixedPackageConfig = queryMixedSiteCode(request);
            if(mixedPackageConfig == null){
                logger.warn("始发【{}】目的【{}】未维护集包地!",request.getOriginalDmsCode(),request.getDestinationDmsCode());
                return null;
            }
            MixedSite mixedSite = new MixedSite();
            mixedSite.setMixedSiteCode(mixedPackageConfig.getReceiveSiteCode());
            mixedSite.setMixedSiteName(mixedPackageConfig.getReceiveSiteName());
            String mixedSiteName;
            BaseStaffSiteOrgDto baseDto = baseMajorManager.getBaseSiteBySiteId(mixedPackageConfig.getReceiveSiteCode());
            if(baseDto == null || StringUtils.isEmpty(baseDto.getDmsShortName())){
                mixedSiteName = mixedPackageConfig.getReceiveSiteName();
            }else {
                mixedSiteName = baseDto.getDmsShortName();
                mixedSite.setMixedSiteShortName(mixedSiteName);
            }
            if(StringUtils.isNotEmpty(mixedSiteName)){
                mixedSite.setCollectionAddress(Constants.MIXED_SITE_NAME_PREFIX + mixedSiteName);
            }
            return mixedSite;
        }catch (Exception e){
            logger.error("查询集包地异常,入参【{}】", JsonHelper.toJson(request),e);
        }
        return null;
    }

    /**
     * 根据条件查询混集包配置
     * @param printQueryRequest
     * @return
     */
    private MixedPackageConfig queryMixedSiteCode(PrintQueryRequest printQueryRequest) {
        MixedPackageConfigRequest mixedPackageConfig = new MixedPackageConfigRequest();
        mixedPackageConfig.setRuleType(RuleTypeEnum.BUILD_PACKAGE.getCode());
        mixedPackageConfig.setCreateSiteCode(printQueryRequest.getOriginalDmsCode());
        mixedPackageConfig.setMixedSiteCode(printQueryRequest.getDestinationDmsCode());
        Integer receiveSiteCode = printQueryRequest.getDestinationDmsCode();
        if(TransportTypeEnum.HIGHWAY_TRANSPORT.getCode().equals(printQueryRequest.getTransportType())){
            mixedPackageConfig.setTransportType(printQueryRequest.getTransportType());
            return getMixedPackageConfig(receiveSiteCode, mixedPackageConfig);
        }
        if(TransportTypeEnum.AIR_TRANSPORT.getCode().equals(printQueryRequest.getTransportType())){
            mixedPackageConfig.setTransportType(printQueryRequest.getTransportType());
            MixedPackageConfig mixedResult = getMixedPackageConfig(receiveSiteCode, mixedPackageConfig);
            if(mixedResult != null){
                return mixedResult;
            }
            mixedPackageConfig.setTransportType(TransportTypeEnum.HIGHWAY_TRANSPORT.getCode());
            return getMixedPackageConfig(receiveSiteCode, mixedPackageConfig);
        }
        return null;
    }

    /**
     * 根据条件查询集包地
     *  1、根据始发、集包地查询
     *  2、未查询到则根据始发、目的查询
     * @param receiveSiteCode
     * @param mixedPackageConfig
     * @return
     */
    private MixedPackageConfig getMixedPackageConfig(Integer receiveSiteCode, MixedPackageConfigRequest mixedPackageConfig) {
        MixedPackageConfig mixedPack = mixedPackageConfigDao.queryMixedSite(mixedPackageConfig);
        if(mixedPack == null){
            mixedPackageConfig.setMixedSiteCode(null);
            mixedPackageConfig.setReceiveSiteCode(receiveSiteCode);
            mixedPack = mixedPackageConfigDao.queryMixedSite(mixedPackageConfig);
        }
        return mixedPack;
    }


    /**
     * 将规则类型、承运类型、站点类型转化为对应的枚举名称用于展示
     *
     * @param mixedPackageConfigList 配置规则列表
     * @return 配置规则列表
     */
    private List<MixedPackageConfig> getEnumNameByCode(List<MixedPackageConfig> mixedPackageConfigList) {
        for (MixedPackageConfig mixedPackageConfig : mixedPackageConfigList) {
            mixedPackageConfig.setRuleTypeName(RuleTypeEnum.getNameByKey(mixedPackageConfig.getRuleType()));
            mixedPackageConfig.setTransportTypeName(TransportTypeEnum.getNameByKey(mixedPackageConfig.getTransportType()));
            mixedPackageConfig.setSiteTypeName(SiteTypeEnum.getNameByKey(mixedPackageConfig.getSiteType()));
        }
        return mixedPackageConfigList;
    }

    /**
     * 封装批量插入对象，包含承运类型以及规则类型选择全部的情况
     *
     * @param request  请求对象
     * @param userCode 当前用户户名
     * @param userName 当前用户名称
     * @return 组装好的返回对象
     */
    private List<MixedPackageConfig> getMixedPackageConfigList(MixedPackageConfigRequest request, Integer userCode, String userName) {
        List<MixedPackageConfig> mixedPackageConfigList = new ArrayList<MixedPackageConfig>();
        for (String value : request.getMixedSiteList()) {
            if (StringHelper.isNotEmpty(value)) {
                List<Integer> ruleList = new ArrayList<Integer>();
                List<Integer> transportList = new ArrayList<Integer>();
                if (null == request.getRuleType()) {
                    for (Integer rule : RuleTypeEnum.ruleTypeMap.keySet()) {
                        ruleList.add(rule);
                    }
                } else {
                    ruleList.add(request.getRuleType());
                }
                if (null == request.getTransportType()) {
                    for (Integer transportType : TransportTypeEnum.transportTypeMap.keySet()) {
                        transportList.add(transportType);
                    }
                } else {
                    transportList.add(request.getTransportType());
                }
                Long now = System.currentTimeMillis();
                for (Integer rule : ruleList) {
                    for (Integer transportType : transportList) {
                        MixedPackageConfig mixedPackageConfig = new MixedPackageConfig();
                        mixedPackageConfig.setCreateSiteArea(request.getCreateSiteArea());
                        mixedPackageConfig.setCreateSiteCode(request.getCreateSiteCode());
                        mixedPackageConfig.setCreateSiteName(request.getCreateSiteName());
                        mixedPackageConfig.setReceiveSiteArea(request.getReceiveSiteArea());
                        mixedPackageConfig.setReceiveSiteCode(request.getReceiveSiteCode());
                        mixedPackageConfig.setReceiveSiteName(request.getReceiveSiteName());
                        mixedPackageConfig.setMixedSiteArea(request.getMixedSiteArea());
                        String mixedSiteCode = value.split("___")[0];
                        mixedPackageConfig.setMixedSiteCode(Integer.parseInt(mixedSiteCode.trim()));
                        String mixedSiteName = value.split("___")[1];
                        mixedPackageConfig.setMixedSiteName(mixedSiteName);
                        mixedPackageConfig.setSiteType(request.getSiteType());
                        mixedPackageConfig.setCreateUser(userName);
                        mixedPackageConfig.setCreateUserCode(userCode);
                        mixedPackageConfig.setRuleType(rule);
                        mixedPackageConfig.setTransportType(transportType);
                        mixedPackageConfig.setTs(now);
                        if (mixedPackageConfigDao.queryMixedPackageConfigCount(mixedPackageConfig) == 0) {
                            mixedPackageConfigList.add(mixedPackageConfig);
                        }

                    }
                }
            }
        }
        return mixedPackageConfigList;
    }
}
