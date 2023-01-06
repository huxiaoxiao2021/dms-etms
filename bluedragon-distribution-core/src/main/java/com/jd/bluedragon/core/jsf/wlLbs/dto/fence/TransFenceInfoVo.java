
package com.jd.bluedragon.core.jsf.wlLbs.dto.fence;

import com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb.Geometry;
import lombok.Data;

import java.io.Serializable;

@Data
public class TransFenceInfoVo implements Serializable {

    private static final long serialVersionUID = 4648171005118791498L;

    private String areaShape;
   
    private String effectiveBeginTime;
   
    private String effectiveEndTime;
   
    private String fenceType;
   
    private String fenceWkt;

    private Geometry geometry;

    private Double lat;
   
    private Double lng;
   
    private String nodeCode;
   
    private String updateTime;
   
    private String updateUserCode;
   
    private String updateUserName;
   
    private Long yn;

}
