package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;

public class RemindTransJobRequest implements Serializable {
    private static final long serialVersionUID = 6740683763062721890L;

    /**
     * 始发网点
     */
    private Integer sourceSiteCode;

    /**
     * 待派车任务号
     */
    private String transJobCode;

    /**
     * 用户erp
     */
    private String userErp;

    /**
     * 用户姓名
     */
    private String userName;
}
