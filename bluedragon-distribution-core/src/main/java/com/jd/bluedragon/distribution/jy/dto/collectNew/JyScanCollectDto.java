package com.jd.bluedragon.distribution.jy.dto.collectNew;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 17:29
 * @Description
 */
@Data
public class JyScanCollectDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String operatorErp;
    private String operatorName;
    private Integer operateSiteId;
    private String operateSiteName;
    private Long operateTime;

    /**
     * 扫描号
     */
    private String barCode;
    /**
     * 扫描号类型
     * com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum
     */
    private String barCodeType;
    /**
     * 扫描任务BizId
     */
    private String mainTaskBizId;
    /**
     * 二级子任务bizId
     */
    private String detailTaskBizId;
    /**
     * 岗位类型
     * com.jd.bluedragon.distribution.jy.constants.JyPostEnum
     */
    private String jyPostType;



}
