package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/8 17:47
 * @Description
 */
public class AviationSendTaskQueryRes implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    private List<AviationSendTaskDto> aviationSendTaskDtoList;

    public List<AviationSendTaskDto> getAviationSendTaskDtoList() {
        return aviationSendTaskDtoList;
    }

    public void setAviationSendTaskDtoList(List<AviationSendTaskDto> aviationSendTaskDtoList) {
        this.aviationSendTaskDtoList = aviationSendTaskDtoList;
    }
}
