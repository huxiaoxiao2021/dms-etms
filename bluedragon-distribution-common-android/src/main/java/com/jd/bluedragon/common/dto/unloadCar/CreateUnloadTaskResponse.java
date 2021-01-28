package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 创建任务返回结果
 * @author: wuming
 * @create: 2020-12-29 18:35
 */
public class CreateUnloadTaskResponse  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long unloadCarId;

    private String sealCarCode;

    public CreateUnloadTaskResponse() {
    }

    public Long getUnloadCarId() {
        return unloadCarId;
    }

    public void setUnloadCarId(Long unloadCarId) {
        this.unloadCarId = unloadCarId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }
}
