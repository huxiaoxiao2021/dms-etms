package com.jd.bluedragon.distribution.jy.dto.unload;

import lombok.Data;

import java.io.Serializable;
@Data
public class TaskFlowDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 3984465078328831447L;
    private String bizId;
    private Long endSiteId;
    private String goodsAreaCode;
}
