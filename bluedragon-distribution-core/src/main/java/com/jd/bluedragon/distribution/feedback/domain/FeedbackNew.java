package com.jd.bluedragon.distribution.feedback.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName Feedback
 * @date 2019/5/27
 */
public class FeedbackNew {
    /**
     * 接入方标识
     */
    public Long appId;

    /**
     * 反馈类型id
     */
    public Long type;

    /**
     * 内容
     */
    public String content;

    public String userAccount;

    public String userName;

    private List<MultipartFile> imgs;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<MultipartFile> getImgs() {
        return imgs;
    }

    public void setImgs(List<MultipartFile> imgs) {
        this.imgs = imgs;
    }
}
