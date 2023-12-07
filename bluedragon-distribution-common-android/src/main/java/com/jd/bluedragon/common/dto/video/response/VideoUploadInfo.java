package com.jd.bluedragon.common.dto.video.response;

import java.io.Serializable;

public class VideoUploadInfo implements Serializable {

    private static final long serialVersionUID = -8714291776122424013L;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 上传地址http
     */
    private String uploadUrl;

    /**
     * 上传地址https
     */
    private String uploadUrlHttps;

    /**
     * 播放地址
     */
    private String playUrl;

    /**
     * 分区
     */
    private String region;

    /**
     * 断点
     */
    private String endpoint;

    /**
     * 存储对象桶
     */
    private String bucket;

    /**
     * 具体对象路径名
     */
    private String objectKey;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 临时token，端上用这个作为文件续传的token标识
     */
    private String sessionToken;

    /**
     * 到期时间
     */
    private String expiration;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadUrlHttps() {
        return uploadUrlHttps;
    }

    public void setUploadUrlHttps(String uploadUrlHttps) {
        this.uploadUrlHttps = uploadUrlHttps;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
