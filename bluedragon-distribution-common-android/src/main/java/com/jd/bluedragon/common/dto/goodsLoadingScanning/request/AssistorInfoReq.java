package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 协助人信息
 * @author: wuming
 * @create: 2020-10-14 16:09
 */
public class AssistorInfoReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 协助人erp
     */
    private String operateUserErp;

    /**
     * 协助人姓名
     */
    private String operateUserName;
}
