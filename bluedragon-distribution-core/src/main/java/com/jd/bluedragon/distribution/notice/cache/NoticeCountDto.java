package com.jd.bluedragon.distribution.notice.cache;

import java.io.Serializable;

/**
 * 通知计数对象
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-14 22:36:55 周日
 */
public class NoticeCountDto implements Serializable {

    private static final long serialVersionUID = -6977654162589155465L;

    private Long count;

    private Long cacheTime;

    public NoticeCountDto() {
    }

    public NoticeCountDto(Long count, Long cacheTime) {
        this.count = count;
        this.cacheTime = cacheTime;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Long cacheTime) {
        this.cacheTime = cacheTime;
    }

    @Override
    public String toString() {
        return "NoticeCountDto{" +
                "count=" + count +
                ", cacheTime=" + cacheTime +
                '}';
    }
}
