package com.jd.bluedragon.distribution.jy.dto.unload;

import lombok.Data;

import java.io.Serializable;
@Data
public class QueryUnloadTaskDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 5362119394569718888L;
    private String vehicleNumber;
    private String packageCode;
}
