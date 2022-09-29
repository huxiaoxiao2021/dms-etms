package com.jd.bluedragon.distribution.sendprint.domain;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.rest.tripartite
 * @ClassName: BaseTripartiteEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/13 18:43
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class TripartiteEntity {

    /**
     * 目的三方列表的ID
     */
    private Long tripartiteId;

    /**
     * 操作人ERP
     */
    private String erpCode;

    /**
     * 操作站点
     */
    private Integer siteCode;

    /**
     * 操作站点名称
     */
    private String siteName;

    /**
     * 操作站点所属分拣中心
     */
    private Integer sortingCenterCode;

    /**
     * 操作站点所属分拣中心名称
     */
    private String sortingCenterName;

    /**
     * 多个目的地的查询条件
     */
    private List<PrintQueryCriteria> list;
}
