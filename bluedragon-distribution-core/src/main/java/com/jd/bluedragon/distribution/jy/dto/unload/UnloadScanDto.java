package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;

/**
 * @ClassName UnloadScanDto
 * @Description
 * @Author wyh
 * @Date 2022/4/9 14:50
 **/
public class UnloadScanDto extends JyUnloadEntity {

    private static final long serialVersionUID = -7741208780465759386L;

    /**
     * 任务组号
     */
    private String groupCode;

    /**
     * 任务主键
     */
    private String taskId;
    /**
     * 1 分拣 2转运
     */
    private int taskType;
    /**
     * 补扫标识
     */
    private boolean supplementary;

    /**
     * 是否是首次扫描
     */
    private boolean firstScan;

    /**
     * 是否是子阶段首次扫描
     */
    private boolean stageFirstScan;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public boolean getSupplementary() {
        return supplementary;
    }

    public void setSupplementary(boolean supplementary) {
        this.supplementary = supplementary;
    }

    public boolean isFirstScan() {
        return firstScan;
    }

    public void setFirstScan(boolean firstScan) {
        this.firstScan = firstScan;
    }

    public boolean isStageFirstScan() {
        return stageFirstScan;
    }

    public void setStageFirstScan(boolean stageFirstScan) {
        this.stageFirstScan = stageFirstScan;
    }
}
