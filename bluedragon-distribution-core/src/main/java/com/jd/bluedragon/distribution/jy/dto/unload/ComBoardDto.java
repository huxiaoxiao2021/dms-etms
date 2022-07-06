package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class ComBoardDto implements Serializable {
    private static final long serialVersionUID = 8723860956859377453L;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 板下应扫包裹数量
     */
    private Integer shouldScanCount;
    /**
     * 板下多扫包裹数量
     */
    private Integer shouldNotScanCount;


}
