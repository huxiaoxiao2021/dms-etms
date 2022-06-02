package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SendVehicleInfo
 * @Description
 * @Author wyh
 * @Date 2022/5/19 15:25
 **/
public class SendVehicleInfo implements Serializable {

    private static final long serialVersionUID = -2632482789820479232L;

    private String sendDetailBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 线路名称简称
     */
    private String lineTypeShortName;

    /**
     * 车长
     */
    private String carLengthStr;

    /**
     * 发货目的地个数
     */
    private Integer destCount;

    /**
     * 预计发货时间
     */
    private Date planDepartTime;

    /**
     * 发货目的地
     */
    private Integer endSiteId;

    /**
     * 发货目的地
     */
    private String endSiteName;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机电话
     */
    private String driverPhone;

    /**
     * 自建任务
     */
    private Boolean manualCreated;

    /**
     * 是否已拍照
     */
    private Boolean photo;

}
