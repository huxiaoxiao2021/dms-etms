package com.jd.bluedragon.distribution.qualityControl.domain;

import lombok.Data;

/**
 * @author liwenji
 * @description 异常提报MQ
 * @date 2023-04-23 17:03
 */
@Data
public class abnormalReportRecordMQ {

    //运单号
    private String abnormalDocumentNum;

    //包裹号
    private String packageNumber;

    //异常一级ID
    private Long abnormalFirstId;

    //异常一级名称
    private String abnormalFirstName;


    //异常二级ID
    private Long abnormalSecondId;

    //异常二级名称
    private String abnormalSecondName;

    //异常三级ID
    private Long abnormalThirdId;

    //异常三级名称
    private String abnormalThirdName;

    //上报部门编号
    private String createDept;

    //上报时间
    private Long createTime;

    //上报人erp
    private String createUser;
    
    // 上报网格
    private String createGridCode;
}
