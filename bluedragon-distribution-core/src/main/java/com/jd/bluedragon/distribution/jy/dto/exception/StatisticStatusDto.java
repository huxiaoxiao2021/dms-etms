package com.jd.bluedragon.distribution.jy.dto.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
@Data
public class StatisticStatusDto implements Serializable {

    /**
     * 状态
     */
    private String status;
    /**
     * 数量
     */
    private Integer count;

}
