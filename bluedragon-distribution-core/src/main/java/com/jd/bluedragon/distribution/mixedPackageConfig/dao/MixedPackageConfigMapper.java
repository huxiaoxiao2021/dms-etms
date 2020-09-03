package com.jd.bluedragon.distribution.mixedPackageConfig.dao;

import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;

import java.util.List;

public interface MixedPackageConfigMapper {

   Integer queryConfings(MixedPackageConfig mixedPackageConfig);

   Integer queryMixedPackageConfigCountByRequest(MixedPackageConfigRequest mixedPackageConfigRequest);

   List<MixedPackageConfig> queryMixedPackageConfigs(MixedPackageConfigRequest mixedPackageConfigRequest);

   Integer updateConfigYNById(MixedPackageConfig mixedPackageConfig);

   List<MixedPackageConfig> querySelectedConfigs(MixedPackageConfigRequest mixedPackageConfigRequest);

   Integer saveConfigs(List<MixedPackageConfig> mixedPackageConfigList);

   Integer updateConfigs(MixedPackageConfigRequest mixedPackageConfigRequest);

   Integer queryMixedPackageConfigCount(MixedPackageConfig mixedPackageConfig);

   List<MixedPackageConfig> queryConfigsForPrint(MixedPackageConfig mixedPackageConfig);

}
