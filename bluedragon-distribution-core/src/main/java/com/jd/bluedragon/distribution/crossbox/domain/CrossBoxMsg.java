package com.jd.bluedragon.distribution.crossbox.domain;

import lombok.Data;

@Data
public class CrossBoxMsg {
    /**
     * 站点code
     */
    private int siteCode;
    /**
     * j-one系统名称
     */
    private String sysName;
    /**
     * ts
     */
    private long ts;
}
