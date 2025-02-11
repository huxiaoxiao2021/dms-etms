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
     * @param bucket 全路径
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
     * 上传文件
     * 
     * @param bucket
     * @param bytes
     * @param fileName 文件名（可带文件夹）
     * @return
     */
    String uploadFileWithName(String bucket, byte[] bytes, String fileName);

    /**
     * 上传并获取外网url
     * @param bucket
     * @param bytes
     * @param extName
     * @return
     */
    String uploadFileAndGetOutUrl(String bucket, byte[] bytes, String extName);


}
