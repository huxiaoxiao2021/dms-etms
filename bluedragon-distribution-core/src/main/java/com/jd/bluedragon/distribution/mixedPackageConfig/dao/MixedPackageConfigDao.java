package com.jd.bluedragon.distribution.mixedPackageConfig.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;

import java.util.List;

/**
 * 混集包配置DAO
 *
 * @author: hujiping
 * @date: 2020/10/28 17:41
 */
public class MixedPackageConfigDao extends BaseDao<MixedPackageConfig> {

    public static final String namespace = MixedPackageConfigDao.class.getName();

    public Integer queryConfigs(MixedPackageConfig mixedPackageConfig) {
        return this.getSqlSession().selectOne(namespace + ".queryConfigs",mixedPackageConfig);
    }

    public Integer queryMixedPackageConfigCountByRequest(MixedPackageConfigRequest request){
        return this.getSqlSession().selectOne(namespace + ".queryMixedPackageConfigCountByRequest",request);
    }

    public List<MixedPackageConfig> queryMixedPackageConfigs(MixedPackageConfigRequest request){
        return this.getSqlSession().selectList(namespace + ".queryMixedPackageConfigs",request);
    }

    public Integer updateConfigYNById(MixedPackageConfig mixedPackageConfig){
        return this.getSqlSession().update(namespace + ".updateConfigYNById",mixedPackageConfig);
    }

    public List<MixedPackageConfig> querySelectedConfigs(MixedPackageConfigRequest request){
        return this.getSqlSession().selectList(namespace + ".querySelectedConfigs",request);
    }

    public Integer saveConfigs(List<MixedPackageConfig> mixedPackageConfigList){
        return this.getSqlSession().insert(namespace + ".saveConfigs",mixedPackageConfigList);
    }

    public Integer updateConfigs(MixedPackageConfigRequest request){
        return this.getSqlSession().update(namespace + ".updateConfigs",request);
    }

    public Integer queryMixedPackageConfigCount(MixedPackageConfig mixedPackageConfig){
        return this.getSqlSession().selectOne(namespace + ".queryMixedPackageConfigCount",mixedPackageConfig);
    }

    public List<MixedPackageConfig> queryConfigsForPrint(MixedPackageConfig mixedPackageConfig){
        return this.getSqlSession().selectList(namespace + ".queryConfigsForPrint",mixedPackageConfig);
    }

    public Integer queryMixedSiteByReceiveCode(MixedPackageConfigRequest mixedPackageConfig) {
        return this.getSqlSession().selectOne(namespace + ".queryMixedSiteByReceiveCode",mixedPackageConfig);
    }

    public Integer queryMixedSiteByMixedSiteCode(MixedPackageConfigRequest mixedPackageConfig) {
        return this.getSqlSession().selectOne(namespace + ".queryMixedSiteByMixedSiteCode",mixedPackageConfig);
    }
}
