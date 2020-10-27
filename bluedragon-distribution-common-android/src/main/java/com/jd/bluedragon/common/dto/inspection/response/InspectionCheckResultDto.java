package com.jd.bluedragon.common.dto.inspection.response;

import java.io.Serializable;

/**
 * @author lijie
 * @date 2020/6/18 16:12
 */
public class InspectionCheckResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private InspectionResultDto inspectionResultDto;

    private ConsumableRecordResponseDto consumableRecordResponseDto;

    public InspectionResultDto getInspectionResultDto() {
        return inspectionResultDto;
    }

    public void setInspectionResultDto(InspectionResultDto inspectionResultDto) {
        this.inspectionResultDto = inspectionResultDto;
    }

    public ConsumableRecordResponseDto getConsumableRecordResponseDto() {
        return consumableRecordResponseDto;
    }

    public void setConsumableRecordResponseDto(ConsumableRecordResponseDto consumableRecordResponseDto) {
        this.consumableRecordResponseDto = consumableRecordResponseDto;
    }
}
