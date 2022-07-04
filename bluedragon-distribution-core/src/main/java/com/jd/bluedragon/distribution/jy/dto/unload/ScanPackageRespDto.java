package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class ScanPackageRespDto implements Serializable {
    private static final long serialVersionUID = -6963372061306635997L;
    private String bizId;
    /**
     * 货区编码
     */
    private String goodsAreaCode;
    /**
     * 板号
     */
    private String boardCode;
    private String endSiteName;
    private Long endSiteId;

    /**
     * 当前板已扫数量
     */
    private Integer currentScanCount;
}
