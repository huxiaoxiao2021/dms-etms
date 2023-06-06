package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发货取消扫描
 * @Author zhengchengfa
 * @Date 2023/6/5 19:37
 * @Description
 */
@Data
public class JySendCancelScanDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String operatorErp;
    private Integer updateUserCode;
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
     * com.jd.bluedragon.common.dto.base.JyPostEnum
     */
    private String jyPostType;

    /**
     * com.jd.bluedragon.distribution.jy.service.send.JyWarehouseSendVehicleServiceImpl#OPERATE_SOURCE_PDA
     * com.jd.bluedragon.distribution.jy.service.send.JyWarehouseSendVehicleServiceImpl#OPERATE_SOURCE_MQ
     */
    private String bizSource;

    /**
     * 不齐运单全选操作取消
     */
    private Boolean buQiAllSelectFlag;
    /**
     * bizId任务下所有collectionCode
     */
    private List<String> collectionCodes;

}
