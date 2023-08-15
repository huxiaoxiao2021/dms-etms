package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 拣运-附件业务类型枚举
 *
 * @author wuyoude
 * @date 2023/5/30 2:26 PM
 */
public enum JyAttachmentBizTypeEnum {

	TASK_STRAND_REPORT("task_strand_report", "任务-滞留上报"),
	TASK_WORK_GRID_MANAGER("task_work_grid_manager", "任务-线上管理化"),
    ;

    private final String code;

    private final String name;

    JyAttachmentBizTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    
}
