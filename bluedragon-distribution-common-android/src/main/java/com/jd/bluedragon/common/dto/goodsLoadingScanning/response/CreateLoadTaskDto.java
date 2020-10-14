package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务创建响应结果
 * @author: wuming
 * @create: 2020-10-14 16:19
 */
public class CreateLoadTaskDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌号
     */
    private String licenseNumber;

    /**
     * 目的场地名称
     */
    private String endSiteName;

}
