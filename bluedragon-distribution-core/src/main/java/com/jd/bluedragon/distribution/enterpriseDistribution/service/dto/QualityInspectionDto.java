package com.jd.bluedragon.distribution.enterpriseDistribution.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 质检报表
 * @Author chenjunyan
 * @Date 2022/6/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityInspectionDto implements Serializable {
    /**
     * 运单号
     */
    private String waybillNo;
    /**
     * 分拣中心编码
     */
    private String sortingNo;
    /**
     * 分拣中心名称
     */
    private String sortingName;
    /**
     * sku总量
     */
    private Integer totalQty;
    /**
     * 已质检总量
     */
    private Integer checkedQty;
    /**
     * 异常原因
     */
    private Integer exceptionReason;
    /**
     * 异常原因名称
     */
    private String exceptionReasonName;
    /**
     * 异常备注
     */
    private String exceptionRemark;
    /**
     * 状态
     */
    private Integer optStatus;
    /**
     * 状态名称
     */
    private String optStatusName;
    /**
     * 增值服务
     */
    private String addValueService;
    /**
     * 增值服务名称
     */
    private String addValueServiceName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private String updateUser;
}
