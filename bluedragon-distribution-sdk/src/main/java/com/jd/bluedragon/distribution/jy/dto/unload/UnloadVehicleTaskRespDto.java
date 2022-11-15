package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
import java.util.List;
public class UnloadVehicleTaskRespDto implements Serializable {
    private static final long serialVersionUID = 8081617853216976050L;
    //待卸车数量
    private int waitUnloadCount;
    //卸车中数量
    private int unloadingCount;
    //已完成卸车数量
    private int unloadedCount;

    private List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList;

    private List<LineTypeStatisDto> lineTypeStatisDtoList;

    public List<LineTypeStatisDto> getLineTypeStatisDtoList() {
        return lineTypeStatisDtoList;
    }

    public void setLineTypeStatisDtoList(List<LineTypeStatisDto> lineTypeStatisDtoList) {
        this.lineTypeStatisDtoList = lineTypeStatisDtoList;
    }

    public int getWaitUnloadCount() {
        return waitUnloadCount;
    }

    public void setWaitUnloadCount(int waitUnloadCount) {
        this.waitUnloadCount = waitUnloadCount;
    }

    public int getUnloadingCount() {
        return unloadingCount;
    }

    public void setUnloadingCount(int unloadingCount) {
        this.unloadingCount = unloadingCount;
    }

    public int getUnloadedCount() {
        return unloadedCount;
    }

    public void setUnloadedCount(int unloadedCount) {
        this.unloadedCount = unloadedCount;
    }

    public List<UnloadVehicleTaskDto> getUnloadVehicleTaskDtoList() {
        return unloadVehicleTaskDtoList;
    }

    public void setUnloadVehicleTaskDtoList(List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList) {
        this.unloadVehicleTaskDtoList = unloadVehicleTaskDtoList;
    }
}
