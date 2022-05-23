package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName BaseSendVehicleDetail
 * @Description
 * @Author wyh
 * @Date 2022/5/18 17:36
 **/
public class BaseSendVehicleDetail implements Serializable {

    /**
     * 预计发货时间
     */
    private Date planDepartTime;

    /**
     * 目的场地
     */
    private Integer endSiteId;

    /**
     * 目的场地名称
     */
    private String endSiteName;

}
