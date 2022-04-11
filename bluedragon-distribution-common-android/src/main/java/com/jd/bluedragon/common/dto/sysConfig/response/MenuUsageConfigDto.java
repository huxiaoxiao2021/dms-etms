package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;

/**
 * 安卓功能是否可使用配置
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 17:24:19 周一
 */
public class MenuUsageConfigDto implements Serializable {
    private static final long serialVersionUID = 4698812631300000677L;

    private MenuUsageConditionConfigDto condition;

    private MenuUsageProcessDto process;

    public MenuUsageConditionConfigDto getCondition() {
        return condition;
    }

    public void setCondition(MenuUsageConditionConfigDto condition) {
        this.condition = condition;
    }

    public MenuUsageProcessDto getProcess() {
        return process;
    }

    public void setProcess(MenuUsageProcessDto process) {
        this.process = process;
    }

}
