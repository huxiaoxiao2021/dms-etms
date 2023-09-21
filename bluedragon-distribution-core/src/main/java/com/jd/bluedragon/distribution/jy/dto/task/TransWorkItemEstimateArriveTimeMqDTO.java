package com.jd.bluedragon.distribution.jy.dto.task;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: ql-dms-distribution
 * @description: 运输封车任务预计到达时间 mq消息
 * @author: ext.tiyong1
 * @create: 2023/8/3 周四 15:41
 **/
@Data
public class TransWorkItemEstimateArriveTimeMqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 预计到达时间
     */
    private Date estimateArriveTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUserCode;

}