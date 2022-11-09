package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class BoardResp implements Serializable {
    private static final long serialVersionUID = -4292907209044485598L;
    /**
     * 小组下人员数量
     */
    private Integer groupUserCount;
    private Integer endSiteId;
    private String endSiteName;
    /**
     * 滑道编号
     */
    private String crossCode;
    /**
     * 笼车编号
     */
    private String tableTrolleyCode;
    /**
     * 该板已扫包裹数量
     */
    private Integer packageHaveScanCount;
    /**
     * 该板已扫箱子数量
     */
    private Integer boxHaveScanCount;
}
