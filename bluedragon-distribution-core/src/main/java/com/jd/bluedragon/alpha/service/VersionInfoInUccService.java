package com.jd.bluedragon.alpha.service;

import com.jd.bluedragon.alpha.domain.Version;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
@Component
public interface VersionInfoInUccService {

    /**
     * 查询所有版本信息
     * @return
     */
    public List<Version> versionList();

    /**
     * 根据版本ID查询版本信息
     * @param versionId
     * @return
     */
    public Version queryById(String versionId) throws Exception;

    /**
     * 根据版本编号、版本状态查询版本信息
     * @param versionId 版本编号
     * @params state 版本状态
     * @return
     */
    public List<Version> queryList(String versionId,boolean state)throws Exception;

    /**
     * 上传版本文件的的同时，执行ucc信息备份
     * @param version 版本信息
     */
    public Integer uploadVersion(Version version);

    /**
     * 根据版本编号批量删除
     * @param versionIdList 版本编号
     */
    public Integer deleteVersion(List<String> versionIdList);

    /**
     * 更新版本信息
     * @params
     */
    public Integer modifyVersion(Version version);

    /**
     * 获取版本的状态
     * @param versionId 版本编号
     * @return 启用返回true 停用返回false
     */
    public boolean versionState(String versionId);

    /**
     * 更改版本的状态
     * @param verisonId 版本编号
     * @param state 要更改的状态
     * @return
     */
    public Integer changeVersionState(String verisonId,boolean state);


}
