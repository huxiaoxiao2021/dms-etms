package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.List;

public class ComBoardAggDto implements Serializable {
    private static final long serialVersionUID = -7931113868979801874L;
    private List<ComBoardDto> comBoardDtoList;
    /**
     * 流向下已扫包裹数量
     */
    private Integer haveScanCount;
    /**
     * 流向下多扫包裹数量
     */
    private Integer extraScanCount;

}
