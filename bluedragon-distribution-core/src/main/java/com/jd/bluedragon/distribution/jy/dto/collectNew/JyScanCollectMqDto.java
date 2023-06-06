package com.jd.bluedragon.distribution.jy.dto.collectNew;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 17:29
 * @Description
 */
@Data
public class JyScanCollectMqDto implements Serializable {

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
     * 单据类型
     * 和 barCodeType 字段区别：
     *     扫描发消息，两个字段值一致
     *     扫描运单消费会拆分成包裹维度，barCodeType 还是waybill codeType是package
     *   barCodeType 只表示原始barCode类型，不会变化
     *   codeType 会跟随消息拆分维度进行改变
     * com.jd.bluedragon.distribution.jy.constants.JyScanCodeTypeEnum
     */
    private String codeType;
    /**
     * 扫描任务BizId
     */
    private String mainTaskBizId;
    /**
     * 二级子任务bizId
     */
    private String detailTaskBizId;
    /**
     * 批次号
     */
    private String sendCode;
    /**
     * 岗位类型
     * com.jd.bluedragon.common.dto.base.JyPostEnum
     */
    private String jyPostType;

    /**
     * 消息传递可能的来源
     * com.jd.bluedragon.distribution.jy.service.collectNew.enums.JyCollectionMqBizSourceEnum
     */
    private String bizSource;


    private String waybillCode;
    private String packageCode;
    private String collectionCode;
    private Integer goodNumber;
}
