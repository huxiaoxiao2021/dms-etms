package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UnloadVehicleTaskRespDto implements Serializable {
    private static final long serialVersionUID = 8081617853216976050L;
    //待卸车数量
    private Integer toUnloadCount;
    //卸车中数量
    private Integer unloadingCount;
    //已完成卸车数量
    private Integer unloadedCount;

    private List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList;

}
