package com.jd.bluedragon.common.dto.station;

/**
 * @author liwenji
 * @description 模拟器校验
 * @date 2023-07-28 10:50
 */
public class SimulatorCheckResp {

    /**
     * 是否可以使用模拟器
     */
    private Boolean useSimulator;

    public Boolean getUseSimulator() {
        return useSimulator;
    }

    public void setUseSimulator(Boolean useSimulator) {
        this.useSimulator = useSimulator;
    }
}
