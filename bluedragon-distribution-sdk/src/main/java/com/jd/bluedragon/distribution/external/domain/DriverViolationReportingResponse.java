package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 组板任务司机违规举报resp
 * @date 2024/4/16
 */
public class DriverViolationReportingResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private JyDriverViolationReportingDto dto;

    public JyDriverViolationReportingDto getDto() {
        return dto;
    }

    public void setDto(JyDriverViolationReportingDto dto) {
        this.dto = dto;
    }
}
