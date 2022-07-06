package com.jd.bluedragon.distribution.jy.dto.unload;

import lombok.Data;

import java.io.Serializable;
@Data
public class UnloadTaskFlowDto implements Serializable {
    private static final long serialVersionUID = 7911106472951275134L;
    /**
     * 获取编码
     */
    private String goodsAreaCode;
    /**
     * 下游场地名称
     */
    private String endSiteName;
    private Long endSiteId;
    /**
     * 该流向下组板数量
     */
    private Integer comBoardCount;
}
