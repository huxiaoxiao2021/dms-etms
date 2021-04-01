package com.jd.bluedragon.distribution.notice.cache;

import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.utils.JsonHelper;

import java.io.Serializable;

/**
 * 未读通知全局缓存
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-12 16:12:38 周五
 */
public class LastNewNoticeGlobalDto extends NoticeH5Dto implements Serializable {
    private static final long serialVersionUID = 3793761964375672311L;

    /**
     * 缓存时间
     */
    private Long cacheTime;

    public Long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Long cacheTime) {
        this.cacheTime = cacheTime;
    }

    @Override
    public String toString() {
        return JsonHelper.toJson(this);
    }
}
