package com.jd.bluedragon.distribution.notice.response;

import java.io.Serializable;

/**
 * 最新通知数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-12 15:36:20 周五
 */
public class NoticeLastNewDto implements Serializable {

    private static final long serialVersionUID = 2208672105852625538L;

    /**
     * 定时拉取时间间隔，单位：秒
     */
    private Integer pullIntervalTime;

    /**
     * 未读数
     */
    private Integer unreadCount;

    /**
     * 最新通知数据，可为空
     */
    private NoticeH5Dto lastNewNotice;

    public Integer getPullIntervalTime() {
        return pullIntervalTime;
    }

    public void setPullIntervalTime(Integer pullIntervalTime) {
        this.pullIntervalTime = pullIntervalTime;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public NoticeH5Dto getLastNewNotice() {
        return lastNewNotice;
    }

    public void setLastNewNotice(NoticeH5Dto lastNewNotice) {
        this.lastNewNotice = lastNewNotice;
    }

    @Override
    public String toString() {
        return "NoticeLastNewDto{" +
                "pullIntervalTime=" + pullIntervalTime +
                ", unreadCount=" + unreadCount +
                ", lastNewNotice=" + lastNewNotice +
                '}';
    }
}
