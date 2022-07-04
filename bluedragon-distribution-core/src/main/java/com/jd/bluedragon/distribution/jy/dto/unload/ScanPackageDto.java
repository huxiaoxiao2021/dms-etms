package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class ScanPackageDto extends BaseReq implements Serializable {
    private static final long serialVersionUID = 1954736364227854974L;
    /**
     * 卸车任务
     */
    private String bizId;
    /**
     * 扫描的包裹/箱号
     */
    private String scanCode;
}
