package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;

/**
 * @ClassName: AbnormalPictureMq
 * @Description: 异常图片数据
 * @author: hujiping
 * @date: 2019/4/22 20:51
 */
public class AbnormalPictureMq implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 异常id */
    private String abnormalId;
    /** 运单号 */
    private String waybillCode;
    /** 图片链接地址 */
    private String excessPictureAddress;
    /** 上传时间 */
    private Long uploadTime;

    public String getAbnormalId() {
        return abnormalId;
    }

    public void setAbnormalId(String abnormalId) {
        this.abnormalId = abnormalId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getExcessPictureAddress() {
        return excessPictureAddress;
    }

    public void setExcessPictureAddress(String excessPictureAddress) {
        this.excessPictureAddress = excessPictureAddress;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
