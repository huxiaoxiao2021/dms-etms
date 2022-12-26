package com.jd.bluedragon.distribution.router.domain.dto;


import com.google.common.collect.Lists;
import com.jd.jddl.executor.function.scalar.filter.In;

import java.util.List;

public class RouteNextDto {

    /**
     * 空对象
     */
    public static final RouteNextDto NONE = new RouteNextDto();

    /**
     * 首个下一网点
     */
    private final Integer firstNextSiteId;

    /**
     * 路由规划网点是否 包含当前网点
     */
    private final boolean routExistCurrentSite;

    /**
     * 后续所有网点
     * 如：3333|44444|5555|6666。当前网点是3333，那么此属性值为44444、5555、6666
     *
     */
    private final List<Integer> nextSiteIdList;

    /**
     * 收个上一网点
     */
    private final Integer firstLastSiteId;

    public RouteNextDto(Integer firstNextSiteId, boolean routExistCurrentSite, List<Integer> nextSiteIdList, Integer firstLastSiteId) {
        this.firstNextSiteId = firstNextSiteId;
        this.routExistCurrentSite = routExistCurrentSite;
        this.nextSiteIdList = nextSiteIdList;
        this.firstLastSiteId = firstLastSiteId;
    }

    private RouteNextDto() {
        this.firstNextSiteId = null;
        this.nextSiteIdList = null;
        this.firstLastSiteId = null;
        this.routExistCurrentSite = Boolean.FALSE;
    }

    public Integer getFirstNextSiteId() {
        return firstNextSiteId;
    }

    public List<Integer> getNextSiteIdList() {
        return nextSiteIdList;
    }

    public boolean isRoutExistCurrentSite() {
        return routExistCurrentSite;
    }

    public Integer getFirstLastSiteId() {
        return firstLastSiteId;
    }
}
