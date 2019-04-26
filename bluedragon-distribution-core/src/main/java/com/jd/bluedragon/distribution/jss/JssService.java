package com.jd.bluedragon.distribution.jss;

import com.jd.bluedragon.distribution.exception.jss.JssStorageException;

import java.io.InputStream;
import java.net.URI;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName JssService
 * @date 2019/4/17
 */
public interface JssService {

    /**
     * JSS服务文件上传
     *
     * @param bucket
     * @param keyName
     * @param length
     * @param inputStream
     */
    void uploadFile(String bucket, String keyName, long length, InputStream inputStream) throws JssStorageException;

    /**
     * JSS服务文件下载文件
     *
     * @param bucket
     * @param keyName
     * @return
     */
    InputStream downloadFile(String bucket, String keyName) throws JssStorageException;

    /**
     * 删除附件
     *
     * @param bucket
     * @param keyName
     * @return
     * @throws JssStorageException
     */
    void deleteFile(String bucket, String keyName) throws JssStorageException;

    /**
     * 文件是否已经存在
     *
     * @param bucket
     * @param keyName
     * @return
     */
    boolean exist(String bucket, String keyName) throws JssStorageException;

    /**
     * 根据keyName获取下载链接
     *
     * @param bucket
     * @param keyName
     * @param timeout
     * @return
     * @throws JssStorageException
     */
    URI getURI(String bucket, String keyName, int timeout) throws JssStorageException;

}
