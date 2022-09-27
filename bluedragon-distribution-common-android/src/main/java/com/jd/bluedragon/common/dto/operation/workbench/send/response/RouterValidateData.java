package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;

/**
 * 路由校验结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-09-13 10:28:38 周二
 */
public class RouterValidateData implements Serializable {

    private static final long serialVersionUID = 3218184302108634648L;

    /**
     * 发货目的地
     */
    private Long sendDestId;

    /**
     * 发货目的地名称
     */
    private String sendDestName;

    /**
     * 路由下一站
     */
    private Long routerNextSiteId;

    /**
     * 路由下一站名称
     */
    private String routerNextSiteName;

    public Long getSendDestId() {
        return sendDestId;
    }

    public void setSendDestId(Long sendDestId) {
        this.sendDestId = sendDestId;
    }

    public String getSendDestName() {
        return sendDestName;
    }

    public void setSendDestName(String sendDestName) {
        this.sendDestName = sendDestName;
    }

    public Long getRouterNextSiteId() {
        return routerNextSiteId;
    }

    public void setRouterNextSiteId(Long routerNextSiteId) {
        this.routerNextSiteId = routerNextSiteId;
    }

    public String getRouterNextSiteName() {
        return routerNextSiteName;
    }

    public void setRouterNextSiteName(String routerNextSiteName) {
        this.routerNextSiteName = routerNextSiteName;
    }
}
