package com.jd.bluedragon.distribution.jy.dto.calibrate;

/**
 * 设备校准提示
 *
 * @author hujiping
 * @date 2022/12/20 2:02 PM
 */
public class JyBizTaskMachineCalibrateMessage {

    public static final String MACHINE_IS_DEAL_HINT = "设备编码:%s正在处理中，请稍后再试!";

    public static final String MACHINE_CALIBRATE_TASK_CREATED_HINT = "校准任务已生成，请重新扫描设备编码!";

    public static final String MACHINE_CALIBRATE_TASK_CLOSED_AND_NOT_OVER_2_HINT = "设备抽检任务已关闭并且未超过2小时，是否强制创建？";

    public static final String MACHINE_CALIBRATE_TASK_NOT_FIND_HINT = "未查询到设备编码:%s的任务数据，请联系分拣小秘!";

    public static final String MACHINE_CALIBRATE_TASK_CREATED_WITH_OTHER_HINT = "此设备任务已被创建,请使用:%s的账号查看!";

    public static final String MACHINE_CALIBRATE_STATUS_ERROR_HINT = "设备:%s的任务状态异常，请联系分拣小秘!";

    public static final String MACHINE_CALIBRATE_REQUEST_ERROR = "请求参数出错，请联系分拣小秘进行处理！";
}
