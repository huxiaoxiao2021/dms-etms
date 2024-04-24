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

    /**
     * 是否允许运输任务叫号
     */
    private boolean showCallButtonFlag;

    /**
     * 是否显示催派按钮
     */
    private boolean showRemindTransJobFlag;

    /**
     * 静默下载
     * 0-否
     * 1-是
     */
    private Boolean silentUpdate;

    /**
     * 图像清晰度判断阈值
     */
    private String imageClarityThreshold;

    /**
     * 是否启用临时线路切换
     */
    private Boolean routerDynamicLineReplaceLineEnable = false;

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

    public boolean getShowCallButtonFlag() {
        return showCallButtonFlag;
    }

    public void setShowCallButtonFlag(boolean showCallButtonFlag) {
        this.showCallButtonFlag = showCallButtonFlag;
    }

    public boolean getShowRemindTransJobFlag() {
        return showRemindTransJobFlag;
    }

    public void setShowRemindTransJobFlag(boolean showRemindTransJobFlag) {
        this.showRemindTransJobFlag = showRemindTransJobFlag;
    }

    public Boolean getSilentUpdate() {
        return silentUpdate;
    }

    public void setSilentUpdate(Boolean silentUpdate) {
        this.silentUpdate = silentUpdate;
    }

    public String getImageClarityThreshold() {
        return imageClarityThreshold;
    }

    public void setImageClarityThreshold(String imageClarityThreshold) {
        this.imageClarityThreshold = imageClarityThreshold;
    }

    public Boolean getRouterDynamicLineReplaceLineEnable() {
        return routerDynamicLineReplaceLineEnable;
    }

    public void setRouterDynamicLineReplaceLineEnable(Boolean routerDynamicLineReplaceLineEnable) {
        this.routerDynamicLineReplaceLineEnable = routerDynamicLineReplaceLineEnable;
    }
}
