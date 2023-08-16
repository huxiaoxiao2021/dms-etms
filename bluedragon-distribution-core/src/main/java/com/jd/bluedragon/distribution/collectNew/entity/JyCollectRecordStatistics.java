package com.jd.bluedragon.distribution.collectNew.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/5/30 17:48
 * @Description
 */

@Data
public class JyCollectRecordStatistics {

    private String aggCode;

    /**
     * 应扫总数
     */
    private Integer shouldCollectTotalNum;

    /**
     * 实扫总数
     */
    private Integer realCollectTotalNum;


}
