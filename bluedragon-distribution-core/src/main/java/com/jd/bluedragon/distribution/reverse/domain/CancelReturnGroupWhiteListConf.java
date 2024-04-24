package com.jd.bluedragon.distribution.reverse.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *@Author liwenji3
 *@Date 2024/4/16 19:42
 *@Description 取消退货组白名单配置
 */

@Data
public class CancelReturnGroupWhiteListConf implements Serializable {

    private static final long serialVersionUID = -7483540520907425538L;

    /**
     * 场地白名单
     */
    private List<Integer> siteWhiteList;

    /**
     * 异常原因编码
     */
    private List<Integer> abnormalCauseList;
}
