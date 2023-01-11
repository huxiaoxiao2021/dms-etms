package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发货数据统计表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendAggsEntityQuery implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 任务主键
     */
    private String bizId;

    /**
     * 操作场地ID
     */
    private Long operateSiteId;

    /**
     *  开始时间
     */
    private Date startTime;

    /**
     *  结束时间
     */
    private Date endTime;



}
