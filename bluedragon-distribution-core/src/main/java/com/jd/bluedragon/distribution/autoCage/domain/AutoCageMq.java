package com.jd.bluedragon.distribution.autoCage.domain;

import com.jd.bluedragon.common.dto.base.request.OperatorData;
import lombok.Data;

import java.util.Date;

/**
 * 自动化装笼
 */
@Data
public class AutoCageMq extends BaseAutoCageMq{


    /**
     * 包裹号或箱号
     */
    private String barcode;
    /**
     * 操作时间
     */
    private Date deviceOperatorTime;

}
