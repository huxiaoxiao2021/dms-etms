package com.jd.bluedragon.distribution.feedback.domain;

import java.util.Date;
import java.util.List;

/**
 * @author liuao
 */
public class FeedBackResponse {
    private Integer status;

    private String statusName;

    private Integer typeId;

    private String typeName;

    private List<String> attachmentList;

    private String userAccount;

    private String userName;

    private Date createTime;

    private String content;

    private List<ReplyResponse> replys;
    private String viewDataJsonStr;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<String> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<String> attachmentList) {
        this.attachmentList = attachmentList;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<ReplyResponse> getReplys() {
        return replys;
    }

    public void setReplys(List<ReplyResponse> replys) {
        this.replys = replys;
    }

    public String getViewDataJsonStr() {
        return viewDataJsonStr;
    }

    public void setViewDataJsonStr(String viewDataJsonStr) {
        this.viewDataJsonStr = viewDataJsonStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
