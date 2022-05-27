package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SendDestDetail
 * @Description
 * @Author wyh
 * @Date 2022/5/19 16:54
 **/
public class SendDestDetail implements Serializable {

    private static final long serialVersionUID = 3687255270847724701L;

    /**
     * 发货目的地
     */
    private Integer endSiteId;

    /**
     * 发货目的地
     */
    private String endSiteName;

    /**
     * 待扫包裹数
     */
    private Long toScanPackCount;

    /**
     * 已扫包裹数
     */
    private Long scannedPackCount;

    /**
     * 预计发货时间
     */
    private Date planDepartTime;
}
