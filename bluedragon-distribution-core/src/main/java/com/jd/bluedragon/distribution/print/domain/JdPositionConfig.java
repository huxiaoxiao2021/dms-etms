package com.jd.bluedragon.distribution.print.domain;

import java.util.List;

/**
 * @author pengchong28
 * @description 弃件暂存岗位配置
 * @date 2024/4/10
 */
public class JdPositionConfig {
    /**
     * 弃件暂存拦截开关：true 拦截  false：不拦截
     */
    private boolean discardedStorageCheckSwitch = false;

    /**
     * 支持弃件暂存岗位编码集合
     */
    private List<String> discardedStoragePositionCodes;

    public boolean isDiscardedStorageCheckSwitch() {
        return discardedStorageCheckSwitch;
    }

    public void setDiscardedStorageCheckSwitch(boolean discardedStorageCheckSwitch) {
        this.discardedStorageCheckSwitch = discardedStorageCheckSwitch;
    }

    public List<String> getDiscardedStoragePositionCodes() {
        return discardedStoragePositionCodes;
    }

    public void setDiscardedStoragePositionCodes(List<String> discardedStoragePositionCodes) {
        this.discardedStoragePositionCodes = discardedStoragePositionCodes;
    }
}
