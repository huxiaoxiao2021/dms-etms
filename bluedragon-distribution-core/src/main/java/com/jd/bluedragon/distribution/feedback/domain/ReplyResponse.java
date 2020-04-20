package com.jd.bluedragon.distribution.feedback.domain;

import java.util.Date;
import java.util.List;

/**
 * @author liuao
 */
public class ReplyResponse {

    private String content;

    private String userAccount;

    private Date createTime;

    private List<String> imgs;
    /**
     * 0:回复。1:补充反馈
     */
    private Integer type;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
