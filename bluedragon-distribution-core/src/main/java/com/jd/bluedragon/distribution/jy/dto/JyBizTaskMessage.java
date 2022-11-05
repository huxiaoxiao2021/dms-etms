package com.jd.bluedragon.distribution.jy.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
@Data
public class JyBizTaskMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 通知类型 0、分配成功通知 1、处理超时通知
     */
    private Integer notifyType;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 分配类型; 1-场地，2-组，3-人员
     */
    private Integer DistributionType;
    /**
     * 分配目标
     */
    private String distributionTarget;
    /**
     * 分配时间
     */
    private Date distributionTime;

}
