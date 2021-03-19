package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;

/**
 * 空铁提货
 */
public class ArReceiveVo {

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 操作站点ID
     */
    private Integer createSiteCode;

    /**
     * 包裹/箱号
     */
    private String boxCode;

    /**
     * 1-装箱；2-原包
     */
    private Short boxingType;

    /**
     * 操作时间
     */
    private Date operateTime;


    private Date createTime;

    private Date updateTime;

    private Integer yn;

}
