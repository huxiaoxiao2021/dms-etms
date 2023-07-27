package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description:异常-破损 service
 */
public interface JyDamageExceptionService {

    /**
     * 根据质控异常提报mq 处理破损异常
     * @param qcReportJmqDto
     * @return
     */
    void dealExpDamageInfoByAbnormalReportOutCall(QcReportOutCallJmqDto qcReportJmqDto);
}
