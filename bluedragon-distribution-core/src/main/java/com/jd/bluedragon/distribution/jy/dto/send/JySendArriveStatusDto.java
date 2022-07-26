package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JySendArriveStatusDto implements Serializable{
    /**
     * 操作场地
     */
    private Long operateSiteId;
    /**
     * 操作人erp
     */
    private String operateUserErp;
    /**
     * 操作人姓名
     */
    private String operateUserName;
    /**
     * 派车单号
     */
    private String transWorkCode;
    /**
     * 车辆是否到达 0-未到 1-已到
     */
    private Integer vehicleArrived;
    /**
     * 拍照时间
     */
    private Long operateTime;
    /**
     * 照片URL链接
     */
    private List<String> imgList;
}
