package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;

/**
 * 安卓功能是否可使用配置
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 17:24:19 周一
 */
public class FuncUsageConfigDto implements Serializable {
    private static final long serialVersionUID = 4698812631300000677L;

    private FuncUsageConditionConfigDto condition;

    private FuncUsageProcessDto process;

    /**
     * 生效时间
     */
    private String effectiveTime;

    /**
     * 生效时间格式化
     */
    private String effectiveTimeFormatStr;

    public FuncUsageConditionConfigDto getCondition() {
        return condition;
    }

    public void setCondition(FuncUsageConditionConfigDto condition) {
        this.condition = condition;
    }

    public FuncUsageProcessDto getProcess() {
        return process;
    }

    public void setProcess(FuncUsageProcessDto process) {
        this.process = process;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getEffectiveTimeFormatStr() {
        return effectiveTimeFormatStr;
    }

    public void setEffectiveTimeFormatStr(String effectiveTimeFormatStr) {
        this.effectiveTimeFormatStr = effectiveTimeFormatStr;
    }
}
