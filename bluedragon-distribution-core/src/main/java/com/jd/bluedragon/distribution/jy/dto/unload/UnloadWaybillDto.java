package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class UnloadWaybillDto implements Serializable {
    private static final long serialVersionUID = -6483793230924544639L;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 运单下包裹数量
     */
    private Integer packageCount;
}
