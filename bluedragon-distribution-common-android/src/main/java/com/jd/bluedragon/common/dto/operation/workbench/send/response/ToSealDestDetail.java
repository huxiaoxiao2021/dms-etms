package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ToSealDestDetail
 * @Description
 * @Author wyh
 * @Date 2022/5/19 18:23
 **/
public class ToSealDestDetail implements Serializable {

    private static final long serialVersionUID = -3526443184706137200L;

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

    /**
     * 状态
     */
    private Integer itemStatus;

    /**
     * 状态描述
     */
    private String itemStatusDesc;
}
