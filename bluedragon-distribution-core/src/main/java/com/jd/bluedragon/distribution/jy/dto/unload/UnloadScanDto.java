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

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

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
}
