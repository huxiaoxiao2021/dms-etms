package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpContrabandReq;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;

import java.util.Map;

/**
 * @Author: ext.xuwenrui
 * @Date: 2023/8/15 10:18
 * @Description: 违禁品上报
 */

public interface JyContrabandExceptionService {
    JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req);

    /**
     * 处理违禁品上报数据
     * @param dto
     * @throws InterruptedException
     */
    void  dealContrabandUploadData(JyExceptionContrabandDto dto) throws InterruptedException;


    /**
     * 根据起始时间和结束时间获取损数据数
     *
     * @param startTime 起始时间，格式为字符串
     * @param endTime 结束时间，格式为字符串
     * @return 损坏次数，返回一个整数值
     */
    int getDamageCountByTime(String startTime, String endTime);
}
