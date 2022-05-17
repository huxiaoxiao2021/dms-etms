package com.jd.bluedragon.common.dto.board.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class VirtualBoardDto implements Serializable {
    private static final long serialVersionUID = -7653571957294904842L;

    private List<VirtualBoardResultDto> virtualBoardResultDtoList;

    /**
     * 模式： 1单流向模式   2：多流向模式   初始化：null(默认多流向模式)
     */
    private Integer flowFlag;

    public List<VirtualBoardResultDto> getVirtualBoardResultDtoList() {
        return virtualBoardResultDtoList;
    }

    public void setVirtualBoardResultDtoList(List<VirtualBoardResultDto> virtualBoardResultDtoList) {
        this.virtualBoardResultDtoList = virtualBoardResultDtoList;
    }

    public Integer getFlowFlag() {
        return flowFlag;
    }

    public void setFlowFlag(Integer flowFlag) {
        this.flowFlag = flowFlag;
    }
}
