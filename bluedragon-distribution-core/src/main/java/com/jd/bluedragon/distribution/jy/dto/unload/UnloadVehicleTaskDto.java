package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
     * 属性标签
     */
    private List<LabelOption> labelOptionList;
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
    private Integer processPercent;
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
     * 应扫包裹/运单数量(总数)
     */
    private Integer shouldScanCount;

    /**
     * 待扫
     */
    private Integer waitScanCount;

    /**
     * 已扫
     */
    private Integer haveScanCount;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    private Date actualArriveTime;
    private Date unloadStartTime;
    private Date unloadFinishTime;

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
