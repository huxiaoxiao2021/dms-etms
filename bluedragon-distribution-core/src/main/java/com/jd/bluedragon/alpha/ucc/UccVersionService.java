package com.jd.bluedragon.alpha.ucc;

import com.jd.bluedragon.alpha.domain.Version;

import java.util.List;

/**
 * 单独对ISV版本信息的维护
 * Created by wuzuxiang on 2016/8/25.
 */
public interface UccVersionService {


    /**
     * 获取所有的version信息
     * @return
     */
    public List<Version> versionList();

    /**
     * 根据版本编号、版本状态查询版本信息
     * @param versionId 版本编号
     * @param state 版本状态
     * @return
     */
    public List<Version> queryList(String versionId, boolean state)throws Exception;

    /**
     * 根据版本编号查询版本信息
     * @param versionId
     * @return
     */
    public Version queryById(String versionId) throws Exception;

    /**
     * 上传版本文件的的同时，需要处理ucc信息备份
     * @param version 版本编号信息
     */
    public void uploadVersion(Version version);
    /**
     * 根据版本编号批量删除
     * @param versionIdList 版本编号
     */
    public void deleteVersion(List<String> versionIdList);

    /**
     * 修改UCC上的版本信息
     * @param version
     */
    public void modifyVersion(Version version);
    /**
     * 获取版本信息的状态
     * @param versionId 版本号
     * @return 状态为0返回false 状态为1返回true
     */
    public boolean versionState(String versionId);

    /**
     * 更改版本状态
     * @param versionId
     * @param state
     */
    public void changeVersionState(String versionId, boolean state)throws Exception;


}
