package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.operation.workbench.config.dto.ClientAutoRefreshConfig;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/7/24 15:28
 * @Description
 */
public class InventoryTaskRes implements Serializable {

    private static final long serialVersionUID = -6051001368608203945L;
    /**
     * 找货任务信息
     */
    private InventoryTaskDto inventoryTaskDto;
    /**
     * PDA设计刷新间隔配置
     * PDA做默认值配置，服务端配置优先级高于PDA默认配置（重大活动期间统一修改刷新间隔）
     */
    private ClientAutoRefreshConfig clientAutoRefreshConfig;

    public InventoryTaskDto getInventoryTaskDto() {
        return inventoryTaskDto;
    }

    public void setInventoryTaskDto(InventoryTaskDto inventoryTaskDto) {
        this.inventoryTaskDto = inventoryTaskDto;
    }

    public ClientAutoRefreshConfig getClientAutoRefreshConfig() {
        return clientAutoRefreshConfig;
    }

    public void setClientAutoRefreshConfig(ClientAutoRefreshConfig clientAutoRefreshConfig) {
        this.clientAutoRefreshConfig = clientAutoRefreshConfig;
    }
}
