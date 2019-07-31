package com.jd.bluedragon.distribution.feedback.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName Feedback
 * @date 2019/5/27
 */
public class Feedback {

    /**
     * APP应用包名(必填)
     */
    private String appPackageName;

    /**
     * 提交用户ERP(必填)
     */
    private String userErp;

    /**
     * 反馈类型(必填)
     */
    private int type;

    /**
     * 反馈内容(必填)
     */
    private String content;

    /**
     * 上传图片
     */
    private List<MultipartFile> images;

    /**
     * 联系方式（手机号 or 邮箱）
     */
    private String contactInfo;

    /**
     * 系统类型(必填)
     */
    private String os;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * 京东物流权限码
     */
    private String roleIndex;

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getRoleIndex() {
        return roleIndex;
    }

    public void setRoleIndex(String roleIndex) {
        this.roleIndex = roleIndex;
    }
}
