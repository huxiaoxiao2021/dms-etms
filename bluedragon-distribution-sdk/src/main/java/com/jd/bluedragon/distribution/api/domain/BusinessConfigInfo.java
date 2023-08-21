package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 业务配置信息
 *
 * @author hujiping
 * @date 2022/10/12 10:59 AM
 */
public class BusinessConfigInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 拣运降级配置
    private List<JyDemotionConfigInfo> jyDemotionConfigList;

    /**
     * 是否可以使用模拟器
     */
    private boolean useSimulatorFlag;

    public List<JyDemotionConfigInfo> getJyDemotionConfigList() {
        return jyDemotionConfigList;
    }

    public void setJyDemotionConfigList(List<JyDemotionConfigInfo> jyDemotionConfigList) {
        this.jyDemotionConfigList = jyDemotionConfigList;
    }

    public boolean isUseSimulatorFlag() {
        return useSimulatorFlag;
    }

    public void setUseSimulatorFlag(boolean useSimulatorFlag) {
        this.useSimulatorFlag = useSimulatorFlag;
    }
}
