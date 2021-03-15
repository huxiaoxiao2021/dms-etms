package com.jd.bluedragon.distribution.notice.cache;

import java.io.Serializable;

/**
 * 未读通知每个用户缓存
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-12 16:12:38 周五
 */
public class LastNewNoticeForUserDto implements Serializable {

    private static final long serialVersionUID = 3793761964375672311L;

    private Long id;

    /**
     * 是否已拉取
     */
    private Integer isFetched;

    private Long publishTime;

    /**
     * 缓存时间
     */
    private Long cacheTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsFetched() {
        return isFetched;
    }

    public void setIsFetched(Integer isFetched) {
        this.isFetched = isFetched;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public Long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Long cacheTime) {
        this.cacheTime = cacheTime;
    }

    @Override
    public String toString() {
        return "LastNewNoticeForUserDto{" +
                "id=" + id +
                ", isFetched=" + isFetched +
                ", publishTime=" + publishTime +
                ", cacheTime=" + cacheTime +
                '}';
    }
}
