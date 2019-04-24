package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @ClassName: ImageParams
 * @Description: 上传图片对象
 * @author: hujiping
 * @date: 2019/4/16 11:21
 */
public class ImageParams implements Serializable {

    private static final long serialVersionUID = 1L;

    private MultipartFile[] image;

    private Long[] operateTime;

    private String machineCode;

    private Integer siteCode;

    public MultipartFile[] getImage() {
        return image;
    }

    public void setImage(MultipartFile[] image) {
        this.image = image;
    }

    public Long[] getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long[] operateTime) {
        this.operateTime = operateTime;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
