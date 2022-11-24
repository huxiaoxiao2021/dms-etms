package com.jd.bluedragon.distribution.jy.dto.comboard;

import lombok.Data;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-23 11:56
 */
@Data
public class JyBizTaskComboardReq {
    /**
     * 始发站点
     */
    private Integer startSiteId;

    /**
     * 目的地站点
     */
    private List<Integer> endSiteCodeList;
}
