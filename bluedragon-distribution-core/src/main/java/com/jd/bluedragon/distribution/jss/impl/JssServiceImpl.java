package com.jd.bluedragon.distribution.jss.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.jss.oss.AmazonS3ClientWrapper;
import com.jd.bluedragon.distribution.jss.utils.JssStorageClient;
import com.jd.jss.JingdongStorageService;
import com.jd.jss.http.Scheme;
import com.jd.jss.service.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
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
    private JssStorageClient jssStorageClient;

    @Autowired
    @Qualifier("dmswebAmazonS3ClientWrapper")
    private AmazonS3ClientWrapper dmswebAmazonS3ClientWrapper;

    @Value("#{'${jss.httpsSet}'.split(',')}")
    private Set<String> httpsSet;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public void uploadFile(String bucket, String keyName, long length, InputStream inputStream) throws JssStorageException {
        try {
            if(uccPropertyConfiguration.isCloudOssInsertSwitch()){
                dmswebAmazonS3ClientWrapper.putObject(inputStream,bucket,keyName,length);
                return;
            }
            JingdongStorageService jss = jssStorageClient.getStorageService();
            jss.bucket(bucket).object(keyName).entity(length, inputStream).put();
            log.info("[JSS存储服务]上传文件成功keyName:{},length:{}", keyName, length);
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
        try {
            JingdongStorageService jss = jssStorageClient.getStorageService();
            ObjectService object = jss.bucket(bucket).object(keyName);
            if(object.exist()) {
                log.info("下载文件成功keyName:{}", keyName);
                return object.get().getInputStream();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new JssStorageException("[JSS存储服务]文件下载异常", e);
        }
    }

    @Override
    public void deleteFile(String bucket, String keyName) throws JssStorageException {
        try {
            dmswebAmazonS3ClientWrapper.deleteObject(bucket,keyName);
            JingdongStorageService jss = jssStorageClient.getStorageService();
            jss.bucket(bucket).object(keyName).delete();
        } catch (Exception e) {
            throw new JssStorageException("[JSS存储服务]删除文件异常", e);
        }
    }

    @Override
    public boolean exist(String bucket, String keyName) throws JssStorageException {
        if(dmswebAmazonS3ClientWrapper.isExists(bucket,keyName)){
            return true;
        }
        try {
            JingdongStorageService jss = jssStorageClient.getStorageService();
            return jss.bucket(bucket).object(keyName).exist();
        } catch (Exception e) {
            throw new JssStorageException("[JSS存储服务]调用JSS服务异常", e);
        }
    }

    @Override
    public URI getURI(String bucket, String keyName, int timeout) throws JssStorageException {
        try {
            URL url = dmswebAmazonS3ClientWrapper.getUrl(bucket,keyName);
            if(url != null){
                return URI.create(url.toString());
            }
            JingdongStorageService jss = jssStorageClient.getStorageService();
            URI uri;
            if (httpsSet.contains(jssStorageClient.getEndpoint())){
                uri = jss.bucket(bucket).object(keyName).
                        presignedUrlProtocol(Scheme.HTTPS).generatePresignedUrl(timeout);
            }
            else{
                uri = jss.bucket(bucket).object(keyName).generatePresignedUrl(timeout);
            }
            if(log.isInfoEnabled()){
                log.info("文件[{}]生成的URL为[{}]",keyName,uri);
            }
            return uri;
        } catch (Exception e) {
            throw new JssStorageException("[JSS存储服务]调用JSS服务异常", e);
        }
    }

    @Override
    public String getPublicBucketUrl(String bucket, String keyName) {
        URL wrapperUrl = dmswebAmazonS3ClientWrapper.getUrl(bucket,keyName);
        if(wrapperUrl != null){
            return wrapperUrl.toString();
        }
        String url = "http://" + jssStorageClient.getEndpoint() + "/" + bucket + "/" + keyName;
        if (httpsSet.contains(jssStorageClient.getEndpoint())){
            url = "https://" + jssStorageClient.getEndpoint() + "/" + bucket + "/" + keyName;
        }
        log.info("文件 {} 生成的URL为 {}",keyName,url);
        return url;
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
            if(uccPropertyConfiguration.isCloudOssInsertSwitch()){
                return dmswebAmazonS3ClientWrapper.putObjectThenGetUrl(inStream,bucket,key,bytes.length,365);
            }
            JingdongStorageService jss = jssStorageClient.getStorageService();

            jss.bucket(bucket).object(key).entity(bytes.length, inStream).put();
            inStream.close();
            URI uri;
            if (httpsSet.contains(jssStorageClient.getEndpoint())){
                uri = jss.bucket(bucket).object(key).presignedUrlProtocol(Scheme.HTTPS)
                        .generatePresignedUrl(315360000);
            }
            else{
                uri = jss.bucket(bucket).object(key).generatePresignedUrl(315360000);
            }
            String url = uri.toString();
            log.info("文件 {} 生成的URL为 {}",key,url);
            return url;
        } catch (Exception e) {
            log.error("异常上行处理异常:", e);
        }
        return null;
    }

}
