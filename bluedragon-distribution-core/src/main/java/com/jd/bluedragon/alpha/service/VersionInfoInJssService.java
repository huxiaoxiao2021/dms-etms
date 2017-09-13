package com.jd.bluedragon.alpha.service;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/30.
 */
@Component
public interface VersionInfoInJssService {

    /**
     * 获取所有的版本编号
     */
    public List<String> allVersionIdInJss() throws Exception;

    /**
     * 上传新版本
     */
    public Integer uploadNewVersion(String versionId, long length, InputStream inputStream);

    /**
     * 修改对应的版本
     */
    public Integer modifyVersion(String versionId, long length, InputStream inputStream);

    /**
     * 删除对应版本
     */
    public Integer deleteVersionByVersionId(List<String> versionIdList);

    /**
     * 获取相应版本号的下载地址
     */
    public URL downloadVersion(String versionId)throws MalformedURLException;


}
