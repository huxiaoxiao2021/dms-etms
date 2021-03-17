package com.jd.bluedragon.distribution.notice.response;

import com.jd.bluedragon.distribution.notice.utils.NoticeLevelEnum;
import com.jd.bluedragon.distribution.notice.utils.NoticeTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * h5页面需要的通知对象
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-02-28 09:27:17 周日
 */
public class NoticeH5Dto implements Serializable {
    private static final long serialVersionUID = 804715005557777821L;

    private Long id;

    private String title;

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
     * 是否置顶显示 1-置顶 0-不置顶
     */
    private Integer isTopDisplay;

    /**
     * 文字内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String contentBrief;

    private Long publishTime;

    /**
     * 发布日期年月日格式化形式
     */
    private String publishDateFormative;

    /**
     * 发布时间时分秒格式化形式
     */
    private String publishTimeFormative;

    /**
     * 是否已读 0-未读；1-已读
     */
    private Integer hasRead;

    /**
     * 类型ID
     */
    private Long categoryId;

    /**
     * 类型名称
     */
    private String categoryName;

    /**
     * 附件列表
     */
    private List<NoticeAttachmentH5Dto> noticeAttachmentList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getContentBrief() {
        return contentBrief;
    }

    public void setContentBrief(String contentBrief) {
        this.contentBrief = contentBrief;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishDateFormative() {
        return publishDateFormative;
    }

    public void setPublishDateFormative(String publishDateFormative) {
        this.publishDateFormative = publishDateFormative;
    }

    public String getPublishTimeFormative() {
        return publishTimeFormative;
    }

    public void setPublishTimeFormative(String publishTimeFormative) {
        this.publishTimeFormative = publishTimeFormative;
    }

    public Integer getIsRead() {
        return hasRead;
    }

    public void setIsRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<NoticeAttachmentH5Dto> getNoticeAttachmentList() {
        return noticeAttachmentList;
    }

    public void setNoticeAttachmentList(List<NoticeAttachmentH5Dto> noticeAttachmentList) {
        this.noticeAttachmentList = noticeAttachmentList;
    }

    @Override
    public String toString() {
        return "NoticeH5Dto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", level=" + level +
                ", type=" + type +
                ", isTopDisplay=" + isTopDisplay +
                ", content='" + content + '\'' +
                ", contentBrief='" + contentBrief + '\'' +
                ", publishTime=" + publishTime +
                ", publishDateFormative='" + publishDateFormative + '\'' +
                ", publishTimeFormative='" + publishTimeFormative + '\'' +
                ", hasRead=" + hasRead +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", noticeAttachmentList=" + noticeAttachmentList +
                '}';
    }
}
