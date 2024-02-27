package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2024/2/1 12:29
 * @Description
 */
public class NewSealVehicleResponseData implements Serializable {

    private static final long serialVersionUID = -7082200092556621496L;

    /**
     * 成功封车的批次号
     */
    private List<String> successSealCarBatchCodes;


    public List<String> getSuccessSealCarBatchCodes() {
        return successSealCarBatchCodes;
    }

    public void setSuccessSealCarBatchCodes(List<String> successSealCarBatchCodes) {
        this.successSealCarBatchCodes = successSealCarBatchCodes;
    }
}
