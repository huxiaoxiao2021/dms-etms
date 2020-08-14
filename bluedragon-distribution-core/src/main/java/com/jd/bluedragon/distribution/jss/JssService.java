package com.jd.bluedragon.distribution.jss;

import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.jss.JingdongStorageService;


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

    /**
     * 公有bucket文件URL
     *
     * @param bucket
     * @param keyName
     * @return
     */
    String getPublicBucketUrl(String bucket, String keyName);

    /**
     * 上传图片
     *
     * @param bucket
     * @param bytes
     * @return
     */
    String uploadImage(String bucket, byte[] bytes);


    /**
     * 上传文件，
     * @param bucket
     * @param bytes
     * @param extName 扩展名
     * @return
     */
    String uploadFile(String bucket, byte[] bytes, String extName);



    /**
     * 是否存在bucket
     *
     * @param bucket
     * @return
     */
     boolean hasBucket(String bucket);

    /**
     * 创建bucket
     *
     * @param bucket
     * @return
     */
     void createBucket(String bucket);

}
