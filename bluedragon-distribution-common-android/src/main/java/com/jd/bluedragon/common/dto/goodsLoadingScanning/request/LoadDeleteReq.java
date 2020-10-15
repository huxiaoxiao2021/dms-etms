package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 删除任务请求参数
 * @author: wuming
 * @create: 2020-10-14 21:38
 */
public class LoadDeleteReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 删除的任务Id
     */
    private Long id;

    /**
     * 操作人erp
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;
}
