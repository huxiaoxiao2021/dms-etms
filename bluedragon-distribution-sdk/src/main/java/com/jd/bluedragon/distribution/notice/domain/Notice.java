package com.jd.bluedragon.distribution.notice.domain;

import com.jd.bluedragon.distribution.notice.utils.NoticeLevelEnum;
import com.jd.bluedragon.distribution.notice.utils.NoticeTypeEnum;

import java.util.Date;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName Notice
 * @date 2019/4/16
 */
public class Notice {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 主题
     */
    private String theme;

    /**
     * 级别 普通、重要、严重
     * {@link NoticeLevelEnum}
     */
    private Integer level;

    /**
     * 类型：通知、说明、警告、处罚等
     * {@link NoticeTypeEnum}
     */
    private Integer type;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 是否展示 1-展示 0-不展示
     */
    private Integer isDisplay;

    /**
     * 是否置顶显示 1-置顶 0-不置顶
     */
    private Integer isTopDisplay;

    /**
     * 文字内容
     */
    private String content;

    /**
     * 创建人ERP
     */
    private String createUser;

    /**
     * 更新人ERP
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除标志,1-删除,0-正常
     */
    private Integer isDelete;

    /**
     * 文件类型中文文本
     */
    private String typeText;

    /**
     * 级别文本
     */
    private String levelText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(Integer isDisplay) {
        this.isDisplay = isDisplay;
    }

    public Integer getIsTopDisplay() {
        return isTopDisplay;
    }

    public void setIsTopDisplay(Integer isTopDisplay) {
        this.isTopDisplay = isTopDisplay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getLevelText() {
        return levelText;
    }

    public void setLevelText(String levelText) {
        this.levelText = levelText;
    }
}
