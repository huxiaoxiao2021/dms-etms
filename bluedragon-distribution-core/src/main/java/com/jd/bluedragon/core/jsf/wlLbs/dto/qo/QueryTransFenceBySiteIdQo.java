package com.jd.bluedragon.core.jsf.wlLbs.dto.qo;

import lombok.Data;

import java.io.Serializable;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-11 23:39:28 周日
 */
@Data
public class QueryTransFenceBySiteIdQo implements Serializable {
    private static final long serialVersionUID = 6802614361050860533L;

    private Integer siteId;

    /**
     * 查询人erp，接口有校验
     */
    private String erp;
}
