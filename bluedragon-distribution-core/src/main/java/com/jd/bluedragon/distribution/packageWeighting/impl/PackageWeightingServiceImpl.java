package com.jd.bluedragon.distribution.packageWeighting.impl;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.router.CacheTablePartition;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.packageWeighting.dao.PackageWeightingDao;
import com.jd.bluedragon.distribution.packageWeighting.domain.BusinessTypeEnum;
import com.jd.bluedragon.distribution.packageWeighting.domain.PackageWeighting;
import com.jd.bluedragon.distribution.weightVolume.domain.ZeroWeightVolumeCheckType;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.domain.PackageOpeFlowDetail;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import javax.annotation.Resource;
import java.util.*;

/**
 * Created by jinjingcheng on 2018/4/22.
 */
@Service("packageWeightingService")
public class PackageWeightingServiceImpl implements PackageWeightingService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PackageWeightingDao packageWeightingDao;
    @Resource(name="waybillPackageManager")
    private WaybillPackageManager waybillPackageManager;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public List<PackageWeighting> findWeightVolume(String waybillCode, String packageCode, List<Integer> businessTypes) {
        return packageWeightingDao.findWeightVolume(waybillCode, packageCode, businessTypes);
    }

    @Override
    public int add(PackageWeighting packageWeighting) {
        packageWeighting.setTableName(CacheTablePartition.getPackageWeightingTableName(packageWeighting.getWaybillCode()));
        return packageWeightingDao.add(packageWeighting);
    }

    @Override
    public List<PackageWeighting> findPackageWeightFlow(String waybillCode, String packageCode, List<Integer> businessTypes) {
        return packageWeightingDao.findPackageWeightFlow(waybillCode, packageCode, businessTypes);
    }

    @Override
    public List<PackageWeighting> findWaybillWeightFlow(String waybillCode, List<Integer> businessTypes) {
       return packageWeightingDao.findWaybillWeightFlow(waybillCode, businessTypes);
    }

    @Override
    public List<PackageWeighting> findAllPackageWeightFlow(String waybillCode, List<Integer> businessTypes) {
       return packageWeightingDao.findAllPackageWeightFlow(waybillCode, businessTypes);
    }

    @Override
    public boolean weightVolumeValidate(String waybillCode, String packageCode) {

        List<PackageWeighting> packageWeightings;
        try {
            if (WaybillUtil.isEconomicNet(waybillCode)) {
                packageWeightings = findWeightVolume(waybillCode, packageCode,
                        Arrays.asList(BusinessTypeEnum.DMS.getCode(),BusinessTypeEnum.TOTAL.getCode()));
            } else {
                packageWeightings = findWeightVolume(waybillCode, packageCode,
                        Arrays.asList(BusinessTypeEnum.DMS.getCode(),BusinessTypeEnum.PICKER.getCode(),BusinessTypeEnum.PICK_RESIDENT.getCode(),BusinessTypeEnum.PICK.getCode(),
                                BusinessTypeEnum.CAR_TEAM.getCode(),BusinessTypeEnum.TOTAL.getCode()));
            }
        } catch (Exception e) {
            logger.error("PackageWeightingServiceImpl-->weightVolumeValidate查询称重量方失败：waybillCode=" + waybillCode + ",packageCode=" + packageCode, e);
            return false;//没有称重量方
        }
        if (packageWeightings == null || packageWeightings.isEmpty()) {
            logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate没查到重量数据：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
            return false;//没有称重量方
        } else {
            boolean hasWVflag = true;//是否有称重的标志
            int total = 0;//运单应有的包裹总数量，做漏称检查
            int packageCount = 0;//计数器，当前检查的包裹数量
            for (PackageWeighting packageWeighting : packageWeightings) {
                if (WaybillUtil.isPackageCode(packageWeighting.getPackageCode())) {
                    if (total == 0) {
                        //从包裹号中取得包裹数量
                        total = WaybillUtil.getPackNumByPackCode(packageWeighting.getPackageCode());
                    }
                    //如果是包裹维度，一个一个包裹判断，是否有包裹不满足条件(经济网只校验重量，不校验体积)
                    if (WaybillUtil.isEconomicNet(waybillCode)) {
                        if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0) {
                            hasWVflag = false;
                        }
                    } else if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0 || packageWeighting.getVolume() == null || packageWeighting.getVolume() <= 0) {
                        logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate包裹重量体积没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                        hasWVflag = false;
                    }
                    packageCount++;
                } else {
                    //如果是快运称重维度，没重量量方就直接返回false(经济网只校验重量，不校验体积)
                    if (WaybillUtil.isEconomicNet(waybillCode)) {
                        if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0) {
                            return false;
                        }
                        return true;
                    } else if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0 || packageWeighting.getVolume() == null || packageWeighting.getVolume() <= 0) {
                        logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate运单重量体积没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (WaybillUtil.isWaybillCode(packageCode)) {
                //扫运单号分拣的话，判断称重的包裹齐不齐
                if (packageCount < total) {
                    logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate包裹不全：waybillCode=" + waybillCode + ",packageCode=" + packageCode + ",packageCount=" + packageCount + ",total=" + total);
                    hasWVflag = false;
                }
            }
            return hasWVflag;//有称重量方
        }
    }

    /**
     * 校验包裹或订单是否有称重量方
     *
     * @param waybillCode
     * @param packageCode
     * @param type
     */
    @Override
    public boolean weightVolumeValidate(String waybillCode, String packageCode, ZeroWeightVolumeCheckType type) {

        List<PackageWeighting> packageWeightings;
        try {
            if (ZeroWeightVolumeCheckType.CHECK_DMS_AGAIN_WEIGHT.equals(type)) {
                //查询分拣称重 包裹维度
                List<PackageWeighting> tempPackageWeightings = findWeightVolume(waybillCode, packageCode,
                        Arrays.asList(BusinessTypeEnum.DMS.getCode(), BusinessTypeEnum.DMS_SORT.getCode()));
                //查询运单称重流水表，过滤出拣运称重 运单维度
                List<PackageWeighting> tempWaybillWeightings = createWaybillWeightings(waybillCode,waybillPackageManager.getWaybillWeightVolumeDetail(waybillCode));
                //合并
                packageWeightings = createPackageWeightings(tempPackageWeightings,tempWaybillWeightings);
            } else {
                packageWeightings = findWeightVolume(waybillCode, packageCode,
                        Arrays.asList(BusinessTypeEnum.DMS.getCode(),BusinessTypeEnum.PICKER.getCode(),BusinessTypeEnum.PICK_RESIDENT.getCode(),BusinessTypeEnum.PICK.getCode(),
                                BusinessTypeEnum.CAR_TEAM.getCode(),BusinessTypeEnum.TOTAL.getCode()));
            }
        } catch (Exception e) {
            logger.error("PackageWeightingServiceImpl-->weightVolumeValidate查询称重量方失败：waybillCode=" + waybillCode + ",packageCode=" + packageCode, e);
            return false;//没有称重量方
        }
        if (packageWeightings == null || packageWeightings.isEmpty()) {
            logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate没查到重量数据：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
            return false;//没有称重量方
        } else {
            boolean hasWVflag = true;//是否有称重的标志
            int total = 0;//运单应有的包裹总数量，做漏称检查
            int packageCount = 0;//计数器，当前检查的包裹数量
            for (PackageWeighting packageWeighting : packageWeightings) {
                if (WaybillUtil.isPackageCode(packageWeighting.getPackageCode())) {
                    if (total == 0) {
                        //从包裹号中取得包裹数量
                        total = WaybillUtil.getPackNumByPackCode(packageWeighting.getPackageCode());
                    }
                    //如果是包裹维度，一个一个包裹判断，是否有包裹不满足条件(经济网只校验重量，不校验体积)
                    if (ZeroWeightVolumeCheckType.CHECK_DMS_AGAIN_WEIGHT.equals(type)) {
                        if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0) {
                            logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate运单重量没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                            hasWVflag = false;
                        }
                    } if (ZeroWeightVolumeCheckType.CHECK_GOOD_OR_AGAIN_WEIGHT_OR_VOLUME.equals(type)) {
                        // 重量和体积同时为空
                        if ((packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0)
                                && (packageWeighting.getVolume() == null || packageWeighting.getVolume() <= 0)) {
                            logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate运单重量体积都没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                            hasWVflag = false;
                        }
                    } else if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0 || packageWeighting.getVolume() == null || packageWeighting.getVolume() <= 0) {
                        logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate包裹重量体积有一个没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                        hasWVflag = false;
                    }
                    packageCount++;
                } else {
                    //如果运单维度的称重流水
                    //只校验重量，不校验体积
                    if (ZeroWeightVolumeCheckType.CHECK_DMS_AGAIN_WEIGHT.equals(type)) {
                        if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0) {
                            logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate运单重量没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                            return false;
                        }
                        return true;
                    }

                    if (ZeroWeightVolumeCheckType.CHECK_GOOD_OR_AGAIN_WEIGHT_OR_VOLUME.equals(type)) {
                        // 重量和体积同时为空
                        if ((packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0)
                                && (packageWeighting.getVolume() == null || packageWeighting.getVolume() <= 0)) {
                            logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate运单重量体积都没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                            return false;
                        }
                        return true;
                    }

                    if (packageWeighting.getWeight() == null || packageWeighting.getWeight() <= 0 || packageWeighting.getVolume() == null || packageWeighting.getVolume() <= 0) {
                        logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate运单重量体积有一个没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (WaybillUtil.isWaybillCode(packageCode)) {
                //扫运单号分拣的话，判断称重的包裹齐不齐
                if (packageCount < total) {
                    logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate包裹不全：waybillCode=" + waybillCode + ",packageCode=" + packageCode + ",packageCount=" + packageCount + ",total=" + total);
                    hasWVflag = false;
                }
            }
            return hasWVflag;//有称重量方
        }
    }

    /**
     * 称重流水表过滤拣运称重
     * @param waybillWeightVolumeDetail
     * @return
     */
    private List<PackageWeighting> createWaybillWeightings(String waybillCode,List<PackageOpeFlowDetail> waybillWeightVolumeDetail) {
        if(CollectionUtils.isEmpty(waybillWeightVolumeDetail)){
            return null;
        }
        List<PackageWeighting> resultList = new ArrayList<>(1);
        for(PackageOpeFlowDetail tempPackageOpeFlowDetail : waybillWeightVolumeDetail){
            Integer opeSiteId = tempPackageOpeFlowDetail.getOpeSiteId();
            //只看了复重
            Double againWeight = tempPackageOpeFlowDetail.getAgainWeight();
            if(opeSiteId != null && againWeight != null && againWeight > 0) {
                //todo getBaseSiteInfoBySiteId  getBaseSiteBySiteId
                BaseSiteInfoDto siteOrgDto = baseMajorManager.getBaseSiteInfoBySiteId(opeSiteId);
                if (siteOrgDto != null && Constants.BASE_SITE_DISTRIBUTION_CENTER.equals(siteOrgDto.getSiteType())) {
                    //至少有1个场地操作了称重就可以
                    resultList.add(genWaybillWeighting(waybillCode,tempPackageOpeFlowDetail));
                    break;
                }
            }
        }
        return resultList;
    }

    /**
     * 构建参数
     * @param waybillCode
     * @param tempPackageOpeFlowDetail
     * @return
     */
    private PackageWeighting genWaybillWeighting(String waybillCode,PackageOpeFlowDetail tempPackageOpeFlowDetail) {
        PackageWeighting packageWeighting = new PackageWeighting();
        packageWeighting.setBusinessType(BusinessTypeEnum.TOTAL.getCode());
        packageWeighting.setWaybillCode(waybillCode);
        packageWeighting.setPackageCode(waybillCode);
        packageWeighting.setWeight(tempPackageOpeFlowDetail.getAgainWeight());
        packageWeighting.setVolume(tempPackageOpeFlowDetail.getAgainVolume());
        packageWeighting.setCreateSiteCode(tempPackageOpeFlowDetail.getOpeSiteId());
        packageWeighting.setCreateSiteName(tempPackageOpeFlowDetail.getOpeSiteName());
        return packageWeighting;
    }

    /**
     * 合并
     * @param tempPackageWeightings
     * @param tempWaybillWeightings
     * @return
     */
    private List<PackageWeighting> createPackageWeightings(List<PackageWeighting> tempPackageWeightings, List<PackageWeighting> tempWaybillWeightings) {
        if(CollectionUtils.isNotEmpty(tempPackageWeightings) && CollectionUtils.isNotEmpty(tempWaybillWeightings)){
            List<PackageWeighting> result = new ArrayList<>(tempPackageWeightings.size() + tempWaybillWeightings.size());
            result.addAll(tempPackageWeightings);
            result.addAll(tempWaybillWeightings);
            return result;
        }else if(CollectionUtils.isNotEmpty(tempPackageWeightings)){
            List<PackageWeighting> result = new ArrayList<>(tempPackageWeightings.size() );
            result.addAll(tempPackageWeightings);
            return result;
        }else if(CollectionUtils.isNotEmpty(tempWaybillWeightings)){
            List<PackageWeighting> result = new ArrayList<>( tempWaybillWeightings.size());
            result.addAll(tempWaybillWeightings);
            return result;
        }else{
            return new ArrayList<>(0);
        }
    }



    /**
     * 纯配外单重量判断逻辑
     *
     * 扫描是包裹：先判断包裹是否称重，否则判断运单是否称重
     * 扫描是运单：先判断运单是否称重，否则判断所有的包裹是否称重
     * @param waybillCode
     * @param packageCode
     * @param quantity
     * @return true 有重量流水，false 无重量流水
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.PackageWeighting.PackageWeightingServiceImpl.weightValidateFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean weightValidateFlow(String waybillCode, String packageCode, Integer quantity) {
        try {

              //1.是包裹维度判断
              if(WaybillUtil.isPackageCode(packageCode)) {
                  if(logger.isInfoEnabled()){
                      logger.info("包裹维度查询重量流水,waybillCode:{},packageCode:{}",waybillCode,packageCode);
                  }
                  List<PackageWeighting> packageWeightings = findPackageWeightFlow(waybillCode, packageCode, BusinessTypeEnum.getAllCode());
                  if(CollectionUtils.isNotEmpty(packageWeightings)){
                     return true;
                  }
                  logger.warn("包裹维度查询包裹称重流水为空,看运单是否称重,waybillCode:{},packageCode:{}",waybillCode,packageCode);
                  //包裹没称过重，看运单的
                  List<PackageWeighting> waybillWeightings = findWaybillWeightFlow(waybillCode, BusinessTypeEnum.getAllCode());
                  if(CollectionUtils.isNotEmpty(waybillWeightings)){
                      return true;
                  }
              }else {
                  if(logger.isInfoEnabled()){
                      logger.info("运单维度查询重量流水,waybillCode:{},packageCode:{}",waybillCode,packageCode);
                  }
                  //2.运单维度判断
                  List<PackageWeighting> waybillWeightings = findWaybillWeightFlow(waybillCode, BusinessTypeEnum.getAllCode());
                  if(CollectionUtils.isNotEmpty(waybillWeightings)){
                      return true;
                  }
                  // 运单没有包裹需要判断是否称重
                  if(quantity == null || quantity <= 0){
                      logger.warn("当前运单没有包裹数量waybillCode:{},packageCode:{}",waybillCode,packageCode);
                      return false;
                  }
                  logger.warn("运单维度查询运单称重流水为空,看全部包裹是否称重,waybillCode:{},packageCode:{}",waybillCode,packageCode);
                  List<PackageWeighting> waybillAllPackageWeightings = findAllPackageWeightFlow(waybillCode, BusinessTypeEnum.getAllCode());
                  if(CollectionUtils.isEmpty(waybillAllPackageWeightings)){
                      logger.warn("PackageWeightingServiceImpl-->weightValidateFlow 运单包裹均没有称重：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                      return false;
                  }
                  Set<String>  allPageCodes = new HashSet<>();
                  //运单中但凡有一个包裹漏称重就返回; 剔除运单号-和重复的包裹号
                  for (PackageWeighting  packageWeight: waybillAllPackageWeightings){
                      if(!waybillCode.equals(packageWeight.getPackageCode())){
                          allPageCodes.add(packageWeight.getPackageCode());
                      }
                  }
                  if(allPageCodes.size() == quantity){
                      return true;
                  }
                  logger.warn("PackageWeightingServiceImpl-->weightValidateFlow 运单包裹未完全称重：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
              }
        } catch (Exception e) {
            logger.error("PackageWeightingServiceImpl-->weightValidateFlow 查询称重失败：waybillCode=" + waybillCode + ",packageCode=" + packageCode, e);
            return false;//没有称重
        }
        return  false;
    }
}
