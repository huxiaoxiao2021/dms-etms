package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;

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
     * @param siteCode
     * @return
     */
    JdCResponse<Boolean> readToProcessDamage(Integer positionCode);
}
