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
    DAMAGE_EXCEPTION_PACKAGE_BEFORE("damage_exception_package_before","破损包裹修复前图片"),
    DAMAGE_EXCEPTION_PACKAGE_AFTER("damage_exception_package_after","破损包裹修复后图片"),
    CONTRABAND_UPLOAD_EXCEPTION("contraband_upload_exception","违禁品上报图片"),
    DEVICE_SPOT_APPEAL("device_spot_appeal","设备抽检申诉附件")
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
