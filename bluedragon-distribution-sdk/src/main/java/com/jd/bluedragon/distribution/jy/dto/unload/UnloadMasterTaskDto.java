package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class UnloadMasterTaskDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;
    /**
     *
     */
    private String bizId;
    /**
     *
     */
    private String sealCarCode;
    /**
     *
     */
    private String vehicleNumber;
    /**
     *
     */
    private String startSiteName;
    private Long startSiteId;
    /**
     *
     */
    private String endSiteName;
    private Long endSiteId;
    /**
     *
     */
    private String railwayPfNo;
    /**
     *
     */
    private Date desealCarTime;
    /**
     *
     */
    private Date unloadFinishTime;
    /**
     *
     */
    private Date unloadStartTime;
    /**
     *
     */
    private Integer vehicleStatus;
    /**
     *
     */
    private Integer manualCreatedFlag;

    /**
     *
     */
    private Integer unloadType;

    private String createUserErp;
    private String createUserName;
    private String updateUserErp;
    private String updateUserName;
    private Date createTime;
    private Date updateTime;

}
