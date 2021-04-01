package com.jd.bluedragon.distribution.notice.cache;

import java.io.Serializable;

/**
 * 通知变更信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-14 22:44:02 周日
 */
public class NoticeChangeInfoDto implements Serializable {
    private static final long serialVersionUID = -3756666690906554406L;

    private Long cacheTime;

    public NoticeChangeInfoDto() {
    }

    public NoticeChangeInfoDto(Long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public Long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Long cacheTime) {
        this.cacheTime = cacheTime;
    }

    @Override
    public String toString() {
        return "NoticeChangeInfoDto{" +
                "cacheTime=" + cacheTime +
                '}';
    }
}
