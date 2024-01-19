package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptDisposeRecord;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.service.exception.JyBusinessInterceptExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.Waybill;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import org.springframework.stereotype.Service;

/**
 * 拦截异常任务相关实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-14 17:30:22 周日
 */
@Service
public class JyBusinessInterceptExceptionServiceImpl extends JyExceptionStrategy implements JyBusinessInterceptExceptionService {

    /**
     * @return
     */
    @Override
    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.INTERCEPT.getCode();
    }

    /**
     * 异常上报
     *
     * @param exceptionEntity
     * @param req
     * @param position
     * @param source
     * @param bizId
     * @return
     */
    @Override
    public JdCResponse<Object> uploadScan(JyBizTaskExceptionEntity exceptionEntity, ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source, String bizId) {
        return null;
    }

    /**
     * PDA选择不同的异常类型、破损类型、修复类型进行判断
     *
     * @param req
     * @param waybill
     * @return
     */
    @Override
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req, Waybill waybill) {
        return null;
    }

    /**
     * 增加拦截异常任务
     *
     * @param businessInterceptReport 拦截记录
     * @return 处理结果
     * @time 2024-01-14 17:37:50 周日
     */
    @Override
    public Result<Boolean> addInterceptTask(BusinessInterceptReport businessInterceptReport) {
        return null;
    }

    /**
     * 处理异常任务
     *
     * @param businessInterceptDisposeRecord
     * @return 处理结果
     * @author fanggang7
     * @time 2024-01-14 17:38:09 周日
     */
    @Override
    public Result<Boolean> disposeInterceptTask(BusinessInterceptDisposeRecord businessInterceptDisposeRecord) {
        return null;
    }
}
