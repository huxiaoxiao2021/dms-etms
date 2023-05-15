package com.jd.bluedragon.common.dto.operation.workbench.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 拣运app-滞留任务类型
 *
 * @author hujiping
 * @date 2023/3/28 11:01 AM
 */
public enum JyBizStrandTaskTypeEnum {

    TRANS_REJECT_SYSTEM(1, "运输驳回系统自建"),
    ARTIFICIAL(2, "人工创建"),
    ;

    public static final List<Integer> taskCodeList;

    private final int code;

    private final String desc;

    JyBizStrandTaskTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    static {
        taskCodeList = new ArrayList<Integer>(2);
        taskCodeList.add(TRANS_REJECT_SYSTEM.getCode());
        taskCodeList.add(ARTIFICIAL.getCode());
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return desc;
    }
    
}
