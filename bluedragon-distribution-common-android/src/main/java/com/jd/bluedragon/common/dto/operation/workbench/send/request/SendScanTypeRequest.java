package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;

/**
 * @author pengchong28
 * @description 发货扫描方式req
 * @date 2024/4/25
 */
public class SendScanTypeRequest implements Serializable {
    /**
     * 扫描方式是否包含 按笼扫描，true-包含
     */
    private Boolean tableTrolleyFlag;

    public Boolean getTableTrolleyFlag() {
        return tableTrolleyFlag;
    }

    public void setTableTrolleyFlag(Boolean tableTrolleyFlag) {
        this.tableTrolleyFlag = tableTrolleyFlag;
    }
}
