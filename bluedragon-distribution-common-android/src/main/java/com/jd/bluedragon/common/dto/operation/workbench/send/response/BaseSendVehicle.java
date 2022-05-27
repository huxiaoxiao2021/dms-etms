package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BaseSendVehicle
 * @Description
 * @Author wyh
 * @Date 2022/5/18 18:23
 **/
public class BaseSendVehicle implements Serializable {

    private static final long serialVersionUID = -5005890642092421853L;

    private String vehicleNumber;

    /**
     * 任务标签集合
     */
    private List<LabelOption> tags;
}
