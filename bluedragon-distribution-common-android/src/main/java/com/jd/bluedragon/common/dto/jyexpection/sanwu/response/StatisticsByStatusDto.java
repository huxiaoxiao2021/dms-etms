package com.jd.bluedragon.common.dto.jyexpection.sanwu.response;

import java.io.Serializable;

/**
 * 按处理状态统计
 */
public class StatisticsByStatusDto implements Serializable {

    // 处理状态
    private Integer status;

    // 统计数量
    private Integer count;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
