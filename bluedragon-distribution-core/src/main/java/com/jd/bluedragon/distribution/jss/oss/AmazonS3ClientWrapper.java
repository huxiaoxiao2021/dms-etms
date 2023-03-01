package com.jd.bluedragon.distribution.jss.oss;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import com.jd.bluedragon.Constants;
import com.jd.dms.wb.report.util.DateHelper;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @program: ql-dms-automatic
 * @description: 京东云 AmazonS3Client 包装类，
 *  oss管理端http://x.devops.jdcloud.com/》对象存储》dms.automatic》volumepicture
 *  帮助手册：https://cf.jd.com/pages/viewpage.action?pageId=657388095
 *          https://joyspace.jd.com/pages/2HhIewwcARtjbtG32AuG
 *          https://docs.jdcloud.com/cn/object-storage-service/signature-in-url-1
 *  接口文档：https://docs.jdcloud.com/cn/object-storage-service/api/compatibility-api-overview?content=API
 *  中文文档参考：https://doc.yun.unionpay.com/tcloud/Storage/CloudStoragePrivate/87609/432845/javaifd
 * @author: xumigen
 * @create: 2023-02-17 13:35
 **/
@Slf4j
public class AmazonS3ClientWrapper implements InitializingBean {

    /**
     * oss 客户端
     */
    private AmazonS3 amazonS3;

    private String accessKey;

    private String secretKey;

    private String signingRegion;

    private String endpoint;
    private int socketTimeout;
    private int connectionTimeout;

    /**
     * oss 最大返回object数量
     */
    private static final int MAX_KESYS = 1000;

    /**
     * 默认url有效时间
     */
    private static int defaultUrlExpirationDay = 90;


    /**
     * 上传文件后得到外链
     * @param bucketName
     * @param inputStream 文件流
     * @param fileName 文件名称待后缀
     * @param contentType 文件类型
     * @param contentLength 文件大小
     * @return
     */
    public void putObjectAndContentType(String bucketName,InputStream inputStream,String fileName,String contentType,long contentLength){
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.AmazonS3ClientWrapper.putObjectAndContentType",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            if(StringUtils.isNotEmpty(contentType)){
                objectMetadata.setContentType(contentType);
            }
            if(contentLength > 0){
                objectMetadata.setContentLength(contentLength);
            }
            PutObjectResult result = amazonS3.putObject(bucketName,fileName,inputStream,objectMetadata);
            log.info("putObjectAndContentType-完成[{}]",result.getETag());
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    /**
     * 流式上传，不用设置文件类型和文件大小
     * @param bucketName
     * @param inputStream
     * @param fileName
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.putObjectWithFlow")
    public void putObjectWithFlow(String bucketName,InputStream inputStream,String fileName)  {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHttpExpiresDate(new Date());
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
        PutObjectResult result = amazonS3.putObject(request);
        log.info("putObjectWithFlow-完成[{}]",result.getETag());
    }


    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.putObjectThenGetUrl")
    public String putObjectThenGetUrl(String bucketName,InputStream inputStream,String fileName,long contentLength){
       this.putObjectAndContentType(bucketName,inputStream,fileName,null,contentLength);
       return this.getUrl(bucketName,fileName).toString();
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.putObject")
    public void putObject(String bucketName,InputStream inputStream,String fileName,long contentLength){
        this.putObjectAndContentType(bucketName,inputStream,fileName,null,contentLength);
    }

    /**
     *  删除文件
     * @param key 文件名称待后缀
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.deleteObject")
    public void deleteObject(String bucketName,String key){
        amazonS3.deleteObject(bucketName, key);
    }


    /**
     * generatePresignedUrl方法用于生成一个带有签名的S3对象的URL，以授权访问。生成的URL只在指定的时间段内有效，一旦过期，就无法再使用。
     * 它可以用于授权第三方用户访问您的S3对象，而无需共享您的AWS凭证或者将对象设置为公开访问。
     * 与getUrl的区别。getUrl方法主要适用于公开的、可匿名访问的对象，而generatePresignedUrl方法则适用于需要授权访问或者进行操作的对象。
     * @param bucketName
     * @param urlExpirationDay
     * @param fileName
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.generatePresignedUrl")
    public URL generatePresignedUrl(String bucketName,int urlExpirationDay,String fileName){
        if(urlExpirationDay <=0 ){
            urlExpirationDay = defaultUrlExpirationDay;
        }
        log.info("云oss查询图片链接[{}]",fileName);
        if (!isExists(bucketName,fileName)) {
            return null;
        }
        Date expirationTime = com.jd.bluedragon.utils.DateHelper.add(new Date(), Calendar.DAY_OF_YEAR,urlExpirationDay);
        URL url = amazonS3.generatePresignedUrl(bucketName, fileName,expirationTime);
        log.info("云oss查询图片链接[{}]url[{}]",fileName,url.toString());
        return url;
    }

    /**
     * getUrl方法用于获取一个公开的、可匿名访问的S3对象的URL。这个URL可以直接被用于访问对象，无需任何的签名或授权信息。
     * 它可以在任何时候使用，除非您显式地将对象设置为私有访问。
     * 与@method generatePresignedUrl 的区别。getUrl方法主要适用于公开的、可匿名访问的对象，而generatePresignedUrl方法则适用于需要授权访问或者进行操作的对象。
     * @param bucketName
     * @param fileName
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.getUrl")
    public URL getUrl(String bucketName,String fileName){
        log.info("云oss-getUrl查询图片链接[{}]",fileName);
        if (!isExists(bucketName,fileName)) {
            return null;
        }
        URL url = amazonS3.getUrl(bucketName,fileName);
        log.info("云oss-getUrl查询图片链接[{}]url[{}]",fileName,url.toString());
        return url;
    }


    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.isExists")
    public boolean isExists(String bucketName, String fileName) {
        try {
            amazonS3.getObjectMetadata(bucketName, fileName);
        } catch (AmazonS3Exception e) {
            log.error("云oss查询图片链接根据flieName未查询到图片[{}]", fileName,e);
            return false;
        }
        return true;
    }

    /**
     * 根据文件名称前缀获取文件列表 keys
     * @param bucketName
     * @param fileNamePrefix 文件前缀
     * @param maxKeys 最大返回条数
     * @param marker 翻页使用 从哪个文件开始，不翻页或者第一页可以传null
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.listObjects")
    public List<String> listObjects(String bucketName,String fileNamePrefix,int maxKeys,String marker){
        if(StringUtils.isEmpty(fileNamePrefix)){
            return Collections.emptyList();
        }
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setPrefix(fileNamePrefix);
        if(maxKeys > MAX_KESYS){
            listObjectsRequest.setMaxKeys(MAX_KESYS);
        }else{
            listObjectsRequest.setMaxKeys(maxKeys);
        }

        if(StringUtils.isNotEmpty(marker)){
            listObjectsRequest.setMarker(marker);
        }
        ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
        List<String> list = objectListing.getObjectSummaries().stream().map(item -> item.getKey()).collect(Collectors.toList());
        return list;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.listObjectListing")
    public ObjectListing listObjectListing(String bucketName,String fileNamePrefix,int maxKeys,String marker){
        if(StringUtils.isEmpty(fileNamePrefix)){
            return null;
        }
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setPrefix(fileNamePrefix);
        if(maxKeys > MAX_KESYS){
            listObjectsRequest.setMaxKeys(MAX_KESYS);
        }else{
            listObjectsRequest.setMaxKeys(maxKeys);
        }

        if(StringUtils.isNotEmpty(marker)){
            listObjectsRequest.setMarker(marker);
        }
        ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
        return objectListing;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.AmazonS3ClientWrapper.getObject")
    public S3Object getObject(String bucketName,String keyName){
        if (!isExists(bucketName,keyName)) {
            return null;
        }
        S3Object s3Object = amazonS3.getObject(bucketName,keyName);
        if(s3Object != null){
            return s3Object;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        ClientConfiguration config = new ClientConfiguration();
        config.withSignerOverride("S3SignerType");
        config.setConnectionTimeout(connectionTimeout <= 0 ? 50000 : connectionTimeout);
        config.setSocketTimeout(socketTimeout <= 0 ? 50000 : socketTimeout);
        config.setMaxConnections(128);
        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(endpoint, signingRegion);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,secretKey);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonS3  = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfig)
                .withClientConfiguration(config)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(true)
                .build();
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setSigningRegion(String signingRegion) {
        this.signingRegion = signingRegion;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

}
