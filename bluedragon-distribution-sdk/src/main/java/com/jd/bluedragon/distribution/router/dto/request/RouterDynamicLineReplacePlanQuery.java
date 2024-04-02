package com.jd.bluedragon.distribution.router.dto.request;


import java.io.Serializable;
import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2024-04-01 19:35:04 周一
 */
public class RouterDynamicLineReplacePlanQuery implements Serializable {

    private static final long serialVersionUID = 7340503050913597627L;

    /**
     * 起始场地ID
     */
    private Integer startSiteId;

    /**
     * 原始目的场地ID
     */
    private Integer oldEndSiteId;

    /**
     * 匹配状态列表
     */
    private List<Integer> statusList;
}