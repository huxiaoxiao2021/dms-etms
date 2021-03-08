package com.jd.bluedragon.distribution.notice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 通知消息用户操作记录<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2020-09-03 16:13:54 周四
 */
public class NoticeToUser implements Serializable{

    private static final long serialVersionUID = -2264755187937231119L;

    //columns START
    /**
     * 主键ID  db_column: id
     */
    private Long id;
    /**
     * 通知id  db_column: notice_id
     */
    private Long noticeId;
    /**
     * 通知人ERP  db_column: receive_user_erp
     */
    private String receiveUserErp;
    /**
     * 是否已读，0-未读，1-已读  db_column: is_read
     */
    private Byte isRead;
    /**
     * 创建人ERP  db_column: create_user
     */
    private String createUser;
    /**
     * 修改人ERP  db_column: update_user
     */
    private String updateUser;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 更新时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 逻辑删除标志,1-删除,0-正常  db_column: yn
     */
    private Byte yn;
    /**
     * 数据库时间  db_column: ts
     */
    private Date ts;
    //columns END


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getReceiveUserErp() {
        return receiveUserErp;
    }

    public void setReceiveUserErp(String receiveUserErp) {
        this.receiveUserErp = receiveUserErp;
    }

    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getYn() {
        return yn;
    }

    public void setYn(Byte yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "NoticeToUser{" +
                "id=" + id +
                ", noticeId=" + noticeId +
                ", receiveUserErp='" + receiveUserErp + '\'' +
                ", isRead=" + isRead +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", yn=" + yn +
                ", ts=" + ts +
                '}';
    }
}
