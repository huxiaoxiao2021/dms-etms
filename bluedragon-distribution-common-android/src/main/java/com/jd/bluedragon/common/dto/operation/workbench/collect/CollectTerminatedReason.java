package com.jd.bluedragon.common.dto.operation.workbench.collect;

import java.io.Serializable;

/**
 * 揽收终止原因
 *
 * @author hujiping
 * @date 2024/3/4 3:15 PM
 */
public class CollectTerminatedReason implements Serializable {
    
    private static final long serialVersionUID = -6517453505273500046L;

    /**
     * 揽收终止原因编码
     */
    private String terminatedReasonCode;

    /**
     * 揽收终止原因名称
     */
    private String terminatedReasonName;

    public String getTerminatedReasonCode() {
        return terminatedReasonCode;
    }

    public void setTerminatedReasonCode(String terminatedReasonCode) {
        this.terminatedReasonCode = terminatedReasonCode;
    }

    public String getTerminatedReasonName() {
        return terminatedReasonName;
    }

    public void setTerminatedReasonName(String terminatedReasonName) {
        this.terminatedReasonName = terminatedReasonName;
    }
}
