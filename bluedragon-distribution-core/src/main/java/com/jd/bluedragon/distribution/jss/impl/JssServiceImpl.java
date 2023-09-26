package com.jd.bluedragon.distribution.jss.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

/**
 * @author lixin39
 * @Description JSS2文件上传下载服务
 * @ClassName JssServiceImpl
 * @date 2019/4/17
 */
@Service
public class JssServiceImpl implements JssService {

    private final Logger log = LoggerFactory.getLogger(JssServiceImpl.class);

    @Autowired
    @Qualifier("dmswebAmazonS3ClientWrapper")
    private AmazonS3ClientWrapper dmswebAmazonS3ClientWrapper;


    @Override
    public void uploadFile(String bucket, String keyName, long length, InputStream inputStream) throws JssStorageException {
        try {
            dmswebAmazonS3ClientWrapper.putObject(inputStream,bucket,keyName,length);
        } catch (Exception e) {
            throw new JssStorageException("[JSS存储服务]上传文件异常", e);
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ioe) {
                log.error("[JSS存储服务]关闭输入流再异常：", ioe);
            }
        }
    }

    @Override
    public InputStream downloadFile(String bucket, String keyName) throws JssStorageException {
        S3Object s3Object = dmswebAmazonS3ClientWrapper.getObject(bucket,keyName);
        if(s3Object != null){
            return s3Object.getObjectContent();
        }
        return null;
    }

    @Override
    public void deleteFile(String bucket, String keyName) throws JssStorageException {
        try {
            dmswebAmazonS3ClientWrapper.deleteObject(bucket,keyName);
        } catch (Exception e) {
            throw new JssStorageException("[JSS存储服务]删除文件异常", e);
        }
    }

    @Override
    public boolean exist(String bucket, String keyName) throws JssStorageException {
        if(dmswebAmazonS3ClientWrapper.isExists(bucket,keyName)){
            return true;
        }
        return false;
    }

    @Override
    public String uploadImage(String bucket, byte[] bytes) {
        return uploadFile(bucket, bytes, "jpg");
    }

    @Override
    public String uploadFile(String bucket, byte[] bytes, String extName) {
        if (bytes == null||bytes.length==0) {
            log.info("上传的数据为空");
            return null;
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        try {
            String key = UUID.randomUUID().toString() + "." + extName;
            return dmswebAmazonS3ClientWrapper.putObjectThenGetUrl(inStream,bucket,key,bytes.length,365);
        } catch (Exception e) {
            log.error("异常上行处理异常:", e);
        }
        return null;
    }

    @Override
    public String uploadFileWithName(String bucket, byte[] bytes, String fileName) {
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        try {
            if (bytes.length==0) {
                log.info("上传的数据为空");
                return null;
            }
            return dmswebAmazonS3ClientWrapper.putObjectThenGetUrl(inStream, bucket, fileName, bytes.length,365); 
        }catch (Exception e){
            log.error("异常上行处理异常:", e);
        }
        return null;
    }


    @Override
    public String uploadFileAndGetOutUrl(String bucket, byte[] bytes, String extName) {
        if (bytes == null||bytes.length==0) {
            log.info("上传的数据为空");
            return null;
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        try {
            String key = UUID.randomUUID().toString() + "." + extName;
            return dmswebAmazonS3ClientWrapper.putObjectThenGetOutNetUrl(inStream,bucket,key,bytes.length,365);

        } catch (Exception e) {
            log.error("异常上行处理异常extName[{}]bucket[{}]",extName,bucket,e);
        }
        return null;
    }

}
