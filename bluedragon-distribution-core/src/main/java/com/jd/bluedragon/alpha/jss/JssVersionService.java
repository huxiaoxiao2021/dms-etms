package com.jd.bluedragon.alpha.jss;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
public interface JssVersionService {

    /**
     * 获取所有的版本编号
     */
    public List<String> getVersionId();

    /**
     * 上传文件夹
     * @param keyName
     * @param length 流的大小
     * @param inputStream 上传流
     */
    public void addVersion(String keyName, long length, InputStream inputStream);

    /**
     * 批量删除版本信息
     * @param
     */
    public void deleteVersion(List<String> versionIdList)throws Exception;

    /**
     * 获取对应版本的下载地址
     * 抛出异常
     */
    public URI downloadVersion(String versionId)throws MalformedURLException ;


}
