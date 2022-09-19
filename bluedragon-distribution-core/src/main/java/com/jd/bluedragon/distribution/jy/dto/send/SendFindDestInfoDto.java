package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

/**
 * 发货目的地信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-09-14 12:39:11 周三
 */
public class SendFindDestInfoDto implements Serializable {
    private static final long serialVersionUID = 1100482453064665706L;

    private Long routerNextSiteId;

    private Long matchSendDestId;

    public Long getRouterNextSiteId() {
        return routerNextSiteId;
    }

    public void setRouterNextSiteId(Long routerNextSiteId) {
        this.routerNextSiteId = routerNextSiteId;
    }

    public Long getMatchSendDestId() {
        return matchSendDestId;
    }

    public void setMatchSendDestId(Long matchSendDestId) {
        this.matchSendDestId = matchSendDestId;
    }
}
