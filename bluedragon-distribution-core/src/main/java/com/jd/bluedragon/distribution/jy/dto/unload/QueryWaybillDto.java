package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import lombok.Data;

@Data
public class QueryWaybillDto extends BaseReq {
    private String boardCode;
    private String waybillCode;
}
