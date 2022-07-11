package com.jd.bluedragon.distribution.jy.dto.unload;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UnloadVehicleTaskDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -4856090554484728089L;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 线路类型编码
     */
    private Integer lineType;
    /**
     * 线路类型名称
     */
    private String lineTypeName;
    private String tagsSign;
    /**
     * 始发场地ID（上游场地）
     */
    private Long startSiteId;
    private String startSiteName;
    /**
     * 目的场地ID（下游场地）
     */
    private Long endSiteId;
    private String endSiteName;
    /**
     * 进度时间
     */
    private String processTime;

    /**
     * 卸车进度
     */
    private String processPercent;
    /**
     * 组板数量
     */
    private Integer comBoardCount;
    /**
     * 拦截数量
     */
    private Integer interceptCount;
    /**
     * 多扫数量
     */
    private Integer extraScanCount;


    /**
     * 应扫包裹数量
     */
    private String shouldScanPackageCount;
    /**
     * 应扫运单数量
     */
    private String shouldScanWaybillCount;

    /**
     * 待卸包裹数量
     */
    private String toUnloadPackageCount;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    private Date actualArriveTime;

    /**
     * JyBizTaskUnloadStatusEnum
     */
    private Integer vehicleStatus;
    /**
     * 类型：1人工 2流水线'
     */
    private Integer unloadType;
    /**
     * 月台号
     */
    private String railwayPfNo;
}
