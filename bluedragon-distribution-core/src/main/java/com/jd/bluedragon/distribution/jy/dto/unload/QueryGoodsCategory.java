package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class QueryGoodsCategory extends BaseReq implements Serializable {
    /**
     * 任务或者板号
     */
    private String barCode;
    /**
     * 1 按任务 2按板
     */
    private Integer type;
}
