package com.jd.bluedragon.distribution.client.domain;

import java.util.List;

/**
 * 客户端菜单配置
 *
 * @author hujiping
 * @date 2021/10/13 5:50 下午
 */
public class ClientNoAuthMenuCodeConfig {

    /**
     * 站点类型
     */
    private Integer siteType;
    /**
     * 子站点类型
     */
    private Integer subType;
    /**
     * 无权限菜单编码
     */
    private List<String> noAuthMenuCodes;

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public List<String> getNoAuthMenuCodes() {
        return noAuthMenuCodes;
    }

    public void setNoAuthMenuCodes(List<String> noAuthMenuCodes) {
        this.noAuthMenuCodes = noAuthMenuCodes;
    }
}
