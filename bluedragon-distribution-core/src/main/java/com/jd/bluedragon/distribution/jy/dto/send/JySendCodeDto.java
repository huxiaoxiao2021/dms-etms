package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.io.Serializable;

@Data
public class JySendCodeDto implements Serializable {
    private static final long serialVersionUID = 7753170373197145796L;

    private String sendVehicleBizId;
    private String updateUserErp;
    private String updateUserName;
}
