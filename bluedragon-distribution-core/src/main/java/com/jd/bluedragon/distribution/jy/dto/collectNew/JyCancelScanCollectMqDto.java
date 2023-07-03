package com.jd.bluedragon.distribution.jy.dto.collectNew;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 17:29
 * @Description
 */
@Data
public class JyCancelScanCollectMqDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String operatorErp;
    private String operatorName;
    private Integer operateSiteId;
    private String operateSiteName;
    private Long operateTime;

    /**
     * 实操扫描单据的原始值
     * 后面展开包裹维度时使用下面的packageCode waybillCode
     */
    private String barCode;
    /**
     * barCode 类型
     * com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum
     */
    private String barCodeType;
    /**
     * 扫描任务BizId
     */
    private String mainTaskBizId;
    /**
     * 岗位类型
     * JyFuncCodeEnum
     */
    private String jyPostType;
}
