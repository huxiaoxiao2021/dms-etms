package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionPackageTypeDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionDamageEnum;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExpCustomerReturnMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExpWaybillDeliveryDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;

import java.util.List;
import java.util.Map;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description:异常-破损 service
 */
public interface JyDamageExceptionService {
    JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req);

    /**
     * 根据质控异常提报mq 处理破损异常
     *
     * @param qcReportJmqDto
     * @return
     */
    void dealExpDamageInfoByAbnormalReportOutCall(QcReportJmqDto qcReportJmqDto);

    /**
     * 获取待处理和新增的破损异常数量
     *
     * @return
     */
    JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(String positionCode);

    /**
     * 读取破损异常为已读
     *
     * @param positionCode
     * @return
     */
    JdCResponse<Boolean> readToProcessDamage(String positionCode);


    /**
     * 获取破损任务详情
     * @param req
     * @return
     */
    JdCResponse<JyExceptionDamageDto> getTaskDetailOfDamage(ExpDamageDetailReq req);

    /**
     *
     * 获取异常包裹类型列表接口
     */
    JdCResponse<List<JyExceptionPackageTypeDto>> getJyExceptionPackageTypeList(String barCode);

    void writeToProcessDamage(String bizId);
    /**
     * 客服下发破损返回结果通知处理
     * @param returnMQ
     */
    void dealCustomerReturnDamageResult(JyExpCustomerReturnMQ returnMQ);

    /**
     * 根据bizIdList 批量查询破损异常列表
     *
     * @param bizIdList
     * @return
     */
    Map<String, JyExceptionDamageDto> getDamageDetailMapByBizIds(List<String> bizIdList, Integer status);

    /**
     * 根据运单妥投状态更新异常任务状态
     * @param waybillCode
     */
    void dealDamageExpTaskStatus(String waybillCode,Integer siteCode);

    /**
     * 处理超48小时客服未反馈破损任务状态
     */
    JdCResponse<Boolean> dealDamageExpTaskOverTwoDags();

    JdCResponse<List<JyExceptionDamageEnum.ConsumableEnum>> getConsumables();
}
