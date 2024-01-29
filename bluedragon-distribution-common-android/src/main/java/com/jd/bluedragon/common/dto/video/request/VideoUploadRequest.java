package com.jd.bluedragon.common.dto.video.request;

import java.io.Serializable;

public class VideoUploadRequest implements Serializable {

    private static final long serialVersionUID = -3998257905558238095L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 视频标签
     */
    private String videoTag;

    /**
     * 视频描述
     */
    private String videoDesc;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 文件大小-字节数
     */
    private Long fileSize;

    /**
     * 上传人ERP
     */
    private String operateErp;

    /**
     * 端口
     */
    private String ipPort;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 音频ID
     */
    private String audioId;

    /**
     * 断点续传是否开启：1-开启
     */
    private String breakPoint;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoTag() {
        return videoTag;
    }

    public void setVideoTag(String videoTag) {
        this.videoTag = videoTag;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public String getIpPort() {
        return ipPort;
    }

    public void setIpPort(String ipPort) {
        this.ipPort = ipPort;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getBreakPoint() {
        return breakPoint;
    }

    public void setBreakPoint(String breakPoint) {
        this.breakPoint = breakPoint;
    }
}
