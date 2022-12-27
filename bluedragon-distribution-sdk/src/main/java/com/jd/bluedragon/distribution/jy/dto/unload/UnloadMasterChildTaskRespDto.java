package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //卸车主子任务信息
 * @date
 **/
public class UnloadMasterChildTaskRespDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;

    private UnloadMasterTaskDto unloadMasterTaskDto;

    private List<UnloadChildTaskDto> unloadChildTaskDtoList;


    public UnloadMasterTaskDto getUnloadMasterTaskDto() {
        return unloadMasterTaskDto;
    }

    public void setUnloadMasterTaskDto(UnloadMasterTaskDto unloadMasterTaskDto) {
        this.unloadMasterTaskDto = unloadMasterTaskDto;
    }

    public List<UnloadChildTaskDto> getUnloadChildTaskDtoList() {
        return unloadChildTaskDtoList;
    }

    public void setUnloadChildTaskDtoList(List<UnloadChildTaskDto> unloadChildTaskDtoList) {
        this.unloadChildTaskDtoList = unloadChildTaskDtoList;
    }
}
