package com.jd.bluedragon.distribution.version.service;

import com.jd.bd.dms.automatic.sdk.modules.dmslocalserverinfo.entity.VipInfoJsfEntity;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;

import java.util.List;

public interface ClientConfigService {

    /**
     * 查询所有的配置信息
     *
     * @return
     */
    List<ClientConfig> getAll();

    /**
     * 依据ID查询配置信息
     *
     * @param id
     * @return
     */
    ClientConfig getById(Long id);

    /**
     * 依据分拣中心编号查询配置信息
     *
     * @param siteCode
     * @return
     */
    List<ClientConfig> getBySiteCode(String siteCode);

    /**
     * 依据分拣中心编号和应用程序类型查询该分拣中心的可用版本和下载地址
     *
     * @param versionEntity
     * @return
     */
    VersionEntity getVersionEntity(VersionEntity versionEntity);

    /**
     * 依据应用程序类型查询所有的配置信息
     *
     * @param programType
     * @return
     */
    List<ClientConfig> getByProgramType(Integer programType);

    /**
     * 是否存在配置信息
     *
     * @param clientConfig
     * @return
     */
    boolean exists(ClientConfig clientConfig);

    /**
     * 添加配置信息
     *
     * @param clientConfig
     * @return
     */
    boolean add(ClientConfig clientConfig);

    /**
     * 修改配置信息
     *
     * @param clientConfig
     * @return
     */
    boolean update(ClientConfig clientConfig);

    /**
     * 删除配置信息
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 根据分拣中心ID获取本地服务器VIP列表
     *
     * @param dmsId
     * @return
     */
    List<VipInfoJsfEntity> getVipListByDmsId(Integer dmsId);

    /**
     * 获取所有分拣中心本地服务器VIP列表
     *
     * @return
     */
    List<VipInfoJsfEntity> getAllVipList();
}
