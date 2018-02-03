package com.jd.bluedragon.distribution.airRailway.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Created by lixin39 on 2017/12/27.
 */
public class SendRegister implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private int id;

    /**
     * 状态,1-已发货,2-已提取
     */
    private Integer status;

    /**
     * 运输类型，1-航空，2-铁路
     */
    private Integer transportType;

    /**
     * 航空单号/铁路单号
     */
    private String orderCode;

    /**
     * 运力名称
     */
    private String transportName;
    /**
     * 铁路站序
     */
    private String siteOrder;

    /**
     * 发货日期
     */
    private Date sendDate;

    /**
     * 航空公司
     */
    private String airlineCompany;

    /**
     * 起飞城市
     */
    private String startCityName;

    /**
     * 起飞城市编号
     */
    private Integer startCityId;

    /**
     * 落地城市
     */
    private String sendCityName;

    /**
     * 落地城市编号
     */
    private Integer endCityId;

    /**
     * 预计起飞时间
     */
    private Date planStartTime;

    /**
     * 预计落地时间
     */
    private Date planEndTime;

    /**
     * 发货件数
     */
    private Integer sendNum;

    /**
     * 计费重量
     */
    private Double chargedWeight;

    /**
     * 发货备注
     */
    private String remark;

    /**
     * 摆渡车型
     */
    private Integer shuttleBusType;

    /**
     * 摆渡车牌号
     */
    private String shuttleBusNum;

    /**
     * 操作人ERP
     */
    private String operatorErp;

    /**
     * 操作部门
     */
    private String operationDept;

    /**
     * 操作时间
     */
    private String operationTime;
    
    
}
