package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.Date;

public class VehicleDetailTaskDto implements Serializable {
    private static final long serialVersionUID = -846608204511149551L;
    private String bizId;
    private String transWorkItemCode;
    private Integer vehicleStatus;
    private Long  startSiteId;
    private String startSiteName;
    private Long  endSiteId;
    private String endSiteName;
    private Date planDepartTime;

}
