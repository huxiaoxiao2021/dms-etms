package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 18:27
 * @Description
 */
public class SendTaskInfoRes implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;


    private List<String> batchCodes;

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }
}
