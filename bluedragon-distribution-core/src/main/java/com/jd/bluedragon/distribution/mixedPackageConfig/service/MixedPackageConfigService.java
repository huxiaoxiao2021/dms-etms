package com.jd.bluedragon.distribution.mixedPackageConfig.service;


import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;

import java.util.List;

public interface MixedPackageConfigService {

    /**
     * 检查混装箱是否可以通过校验
     * @param createSiteCode    建包分拣中心编码
     * @param reciveSiteCode    目的分拣中心编码
     * @param mixedSiteCode     可混装地区编码
     * @param transportType     运输类型
     * @return 通过true ，不通过false
     */
    boolean checkMixedPackageConfig(Integer createSiteCode, Integer reciveSiteCode, Integer mixedSiteCode, Integer transportType, Integer ruleType);
    /**
     * 跨区混装箱规则查询
     * @param mixedPackageConfigRequest 混装箱查询实体
     *
     *
     * @return  规则列表
     */
    List<MixedPackageConfig> queryMixedPackageConfigs (MixedPackageConfigRequest mixedPackageConfigRequest, Pager pager);
    /**
     * 根据id将规则设置为失效
     * @param id 规则id
     *
     *
     * @return  影响条数
     */
    Integer updateConfigYNById(Integer id, Integer userCode, String userName);

    /**
     * 查询已选的配置规则
     * @param mixedPackageConfigRequest 混装箱查询实体
     *
     *
     * @return  规则列表
     */
    List<MixedPackageConfig> querySelectedConfigs(MixedPackageConfigRequest mixedPackageConfigRequest);

    /**
     * 批量保存规则配置
     * @param request   请求对象
     * @param userCode  用户编码
     * @param userName  用户名称
     * @return
     */
    Integer saveConfigs(MixedPackageConfigRequest request, Integer userCode, String userName);

    /**
     * 删除规则配置
     * @param mixedPackageConfigRequest 请求对象
     * @param userCode 用户编码
     * @param userName  用户名称
     * @return
     */
    Integer updateConfigs(MixedPackageConfigRequest mixedPackageConfigRequest, Integer userCode, String userName);


    List<MixedPackageConfig> queryConfigsForPrint(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType);

    /**
     * 查询集包地
     * @param printQueryRequest
     * @return
     */
    MixedSite queryMixedSiteCodeForPrint(PrintQueryRequest printQueryRequest);
}
