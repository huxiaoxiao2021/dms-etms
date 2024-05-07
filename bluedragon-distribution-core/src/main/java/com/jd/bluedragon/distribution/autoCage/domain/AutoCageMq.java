package com.jd.bluedragon.distribution.autoCage.domain;

import com.jd.bluedragon.common.dto.base.request.OperatorData;
import lombok.Data;

import java.util.Date;

/**
 * 自动化装笼
 */
@Data
public class AutoCageMq {
    /**
     * 分拣机编码
     */
    private String machineCode;
    /**
     * 板号
     */
    private String boardCode;

    /**
     * 包裹号或箱号
     */
    private String barcode;
    /**
     * 操作时间
     */
    private Date operatorTime;
    /**
     * 操作人erp
     */
    private String operatorErp;
    /**
     * 操作人姓名
     */
    private String operatorName;
    /**
     * 操作人所属场地
     */
    private Integer siteCode;

    /**
     * 笼车箱号
     */
    private String cageBoxCode;

    /**
     * 操作信息对象
     */
    private OperatorData operatorData;

    private String destination;
    private Integer destinationId;
}
