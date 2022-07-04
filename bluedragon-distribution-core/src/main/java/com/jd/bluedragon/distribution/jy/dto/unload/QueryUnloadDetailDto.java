package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class QueryUnloadDetailDto implements Serializable {
    private static final long serialVersionUID = -4224506737591286259L;
    /**
     * 货物分类
     */
    private Integer goodsType;
    /**
     * 扫描异常类型 待扫 多扫 拦截
     */
    private Integer expType;

}
