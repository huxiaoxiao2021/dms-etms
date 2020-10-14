package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;
import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 开始任务接口
 * @author: wuming
 * @create: 2020-10-14 16:05
 */
public class StartTaskReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务Id
     */
    private String id;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 协助人信息
     */
    private List<AssistorInfoReq> assistorInfo;
}
