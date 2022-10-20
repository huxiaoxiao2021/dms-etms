package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;

/**
 * 拣运app降级配置
 *
 * @author hujiping
 * @date 2022/10/12 4:09 PM
 */
public class JyDemotionConfigInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 功能key
    private String demotionFuncKey;
    // 降级提示语
    private String demotionHintMsg;
    // 是否降级
    private Boolean demotionSwitch;

    public String getDemotionFuncKey() {
        return demotionFuncKey;
    }

    public void setDemotionFuncKey(String demotionFuncKey) {
        this.demotionFuncKey = demotionFuncKey;
    }

    public String getDemotionHintMsg() {
        return demotionHintMsg;
    }

    public void setDemotionHintMsg(String demotionHintMsg) {
        this.demotionHintMsg = demotionHintMsg;
    }

    public Boolean getDemotionSwitch() {
        return demotionSwitch;
    }

    public void setDemotionSwitch(Boolean demotionSwitch) {
        this.demotionSwitch = demotionSwitch;
    }
}
