package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;

public class MixScanTaskDetailDto implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 是否是自建任务 true：自建
     */
    private Boolean manualCreatedFlag;

    /**
     * 发货明细流向ID
     */
    private String sendDetailBizId;
}
