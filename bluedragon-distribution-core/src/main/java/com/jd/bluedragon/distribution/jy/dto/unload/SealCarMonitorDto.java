package com.jd.bluedragon.distribution.jy.dto.unload;

import lombok.Data;

import java.util.Date;

@Data
public class SealCarMonitorDto {


    // 封车编码
    private String sealCarCode;

    // 任务编号
    private String billCode;

    // 车牌号
    private String vehicleNumber;

    // 车牌号后四位
    private String vehicleNumberLastFour;

    // 运力编码
    private String transportCode;

    // 批次号列表，逗号分隔
    private String sendCodeList;

    // 封签号列表，逗号分隔
    private String sealCodeList;

    // 封车时间
    private Date sealCarTime;

    // 始发站点编号(七位编码)
    private String startSiteCode;

    // 始发站点ID
    private Integer startSiteId;

    // 始发站点名称
    private String startSiteName;

    // 始发站点类型
    private String startSiteType;

    // 始发站点类型名称
    private String startSiteTypeName;

    // 始发机构编号
    private Integer startOrgCode;

    // 始发机构名称
    private String startOrgName;

    // 目的站点编号(七位编码)
    private String endSiteCode;

    // 目的站点ID
    private Integer endSiteId;

    // 目的站点名称
    private String endSiteName;

    // 目的站点类型
    private String endSiteType;

    // 目的站点类型名称
    private String endSiteTypeName;

    // 目的机构编号
    private Integer endOrgCode;

    // 目的机构名称
    private String endOrgName;

    // 解封车时间
    private Date desealCarTime;

    // 检查类型，枚举值检查类型，1-抽检
    private Integer checkType;

    // 线路类型
    private Integer lineSourceType;

    // 线路类型
    private String lineSourceTypeName;

    // 线路类型
    private Integer lineType;

    // 线路类型描述
    private String lineTypeName;

    // 车型
    private String carModel;

    // 委托书号
    private String transBookCode;

    // 预计到达时间
    private Date predictionArriveTime;

    // 实际到达时间
    private Date actualArriveTime;

    // 用于排序的时间，优先取「实际到达时间」，若不存在再取「预计到达时间」
    private Date orderTime;

    // 总件数
    private Long totalCount;

    // 已卸件数
    private Long unloadCount;

    // 本地包裹量
    private Integer localCount;

    // 外埠包裹量
    private Integer externalCount;

    // 车辆状态, 1-待解，2-待卸，3-卸车，4-在途
    private Integer vehicleStatus;

    // 积分，根据明细条件、类型等计算出
    private Double fraction;

    // 排名
    private Integer ranking;

    // 是否有效，1-有效，0-无效
    private Integer yn;

    // 封车状态，10-封车，20-解封, 200-取消封车
    private Integer sealCarStatus;

    // 实际发出时间
    private Date actualSendTime;

    // 车辆卸货状态，0-未卸 1-已卸（只要有一单验货，则视为已卸）
    private Integer unloadState;

    // 时间戳
//    @PropertyMapping(value = SealCarMonitorFieldNameDoc.TS, dateToTimeMillis = true)
//    private Long ts;
}
