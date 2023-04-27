package com.jd.bluedragon.distribution.jy.service.task.autoclose.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自动关闭任务上下文
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-21 00:01:25 周二
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AutoCloseTaskContextDto extends AutoCloseTaskPo {

    private static final long serialVersionUID = 7883058889268903416L;

    private String bizId;

    private Long operateTime;

    /**
     * 操作人
     */
    private String operateUserErp;

    /**
     * 操作人名称
     */
    private String operateUserName;


}
