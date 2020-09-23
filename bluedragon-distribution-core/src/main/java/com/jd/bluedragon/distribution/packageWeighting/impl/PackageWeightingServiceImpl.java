package com.jd.bluedragon.distribution.packageWeighting.impl;


import com.jd.bluedragon.common.router.CacheTablePartition;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.packageWeighting.dao.PackageWeightingDao;
import com.jd.bluedragon.distribution.packageWeighting.domain.BusinessTypeEnum;
import com.jd.bluedragon.distribution.packageWeighting.domain.PackageWeighting;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jinjingcheng on 2018/4/22.
 */
@Service("packageWeightingService")
public class PackageWeightingServiceImpl implements PackageWeightingService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PackageWeightingDao packageWeightingDao;

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
    public boolean weightVolumeValidate(String waybillCode, String packageCode) {

        List<PackageWeighting> packageWeightings;
        try {
            if (WaybillUtil.isEconomicNet(waybillCode)) {
                packageWeightings = findWeightVolume(waybillCode, packageCode,
                        Arrays.asList(BusinessTypeEnum.DMS.getCode(),BusinessTypeEnum.TOTAL.getCode()));
            } else {
                packageWeightings = findWeightVolume(waybillCode, packageCode, BusinessTypeEnum.getAllCode());
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
                        if (packageWeighting.getWeight() == null || packageWeighting.getWeight().doubleValue() <= 0) {
                            hasWVflag = false;
                        }
                    } else if (packageWeighting.getWeight() == null || packageWeighting.getWeight().doubleValue() <= 0 || packageWeighting.getVolume() == null || packageWeighting.getVolume().doubleValue() <= 0) {
                        logger.warn("PackageWeightingServiceImpl-->weightVolumeValidate包裹重量体积没有：waybillCode=" + waybillCode + ",packageCode=" + packageCode);
                        hasWVflag = false;
                    }
                    packageCount++;
                } else {
                    //如果是快运称重维度，没重量量方就直接返回false(经济网只校验重量，不校验体积)
                    if (WaybillUtil.isEconomicNet(waybillCode)) {
                        if (packageWeighting.getWeight() == null || packageWeighting.getWeight().doubleValue() <= 0) {
                            return false;
                        }
                        return true;
                    } else if (packageWeighting.getWeight() == null || packageWeighting.getWeight().doubleValue() <= 0 || packageWeighting.getVolume() == null || packageWeighting.getVolume().doubleValue() <= 0) {
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
}
