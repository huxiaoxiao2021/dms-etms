package com.jd.bluedragon.distribution.jy.service.exception;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.bluedragon.distribution.jy.dto.exception.JyExpTaskMessage;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskStatusEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;

import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:18
 * @Description: 异常策略类
 */
public abstract class JyExceptionStrategy {

    public abstract Integer getExceptionType();

    public abstract  JdCResponse<Object> uploadScan(JyBizTaskExceptionEntity exceptionEntity, ExpUploadScanReq req, PositionDetailRecord position
            , JyExpSourceEnum source,  String bizId);


}
