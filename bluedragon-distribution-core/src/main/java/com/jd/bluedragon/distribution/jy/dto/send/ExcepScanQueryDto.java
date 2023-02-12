package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class ExcepScanQueryDto {
    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    public ExcepScanQueryDto(String sendVehicleBizId) {
        this.sendVehicleBizId =sendVehicleBizId;
    }
}
