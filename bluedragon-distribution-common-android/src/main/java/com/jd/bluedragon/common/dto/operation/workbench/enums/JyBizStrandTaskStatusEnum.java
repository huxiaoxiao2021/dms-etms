package com.jd.bluedragon.common.dto.operation.workbench.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 拣运app-滞留任务状态
 *
 * @author hujiping
 * @date 2023/3/28 11:01 AM
 */
public enum JyBizStrandTaskStatusEnum {

    OVER_TIME(1, "超时"),
    CANCEL(2, "已取消"),
    COMPLETE(3, "已完成"),
    TODO(4, "待处理"),
    HANDLING(5, "处理中"),
    ;

    // 未完成任务
    public static final List<Integer> unCompleteStatus;
    // 已完成任务
    public static final List<Integer> completeStatus;

    private final int code;

    private final String desc;

    JyBizStrandTaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    static {
        unCompleteStatus = new ArrayList<Integer>(2);
        unCompleteStatus.add(TODO.getCode());
        unCompleteStatus.add(HANDLING.getCode());
        completeStatus = new ArrayList<Integer>(2);
        completeStatus.add(COMPLETE.getCode());
        completeStatus.add(OVER_TIME.getCode());
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return desc;
    }
    
}
