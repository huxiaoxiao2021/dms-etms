package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionPackageTypeDto;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;

import java.util.List;

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
    void dealExpDamageInfoByAbnormalReportOutCall(QcReportOutCallJmqDto qcReportJmqDto);

    /**
     * 获取待处理和新增的破损异常数量
     *
     * @return
     */
    JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(Integer positionCode);

    /**
     * 读取破损异常为已读
     *
     * @param positionCode
     * @return
     */
    JdCResponse<Boolean> readToProcessDamage(Integer positionCode);


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
    JdCResponse<List<JyExceptionPackageTypeDto>> getJyExceptionPackageTypeList();

    void writeToProcessDamage(Integer positionCode, String bizId);
}
