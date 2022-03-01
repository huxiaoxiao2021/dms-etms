package com.jd.bluedragon.distribution.position.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.api.PositionQueryJsfService;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 岗位查询对外jsf服务
 *
 * @author hujiping
 * @date 2022/2/26 8:50 PM
 */
@Service("positionQueryJsfService")
public class PositionQueryJsfServiceImpl implements PositionQueryJsfService {

    private static final Logger logger = LoggerFactory.getLogger(PositionQueryJsfServiceImpl.class);

    @Autowired
    private PositionRecordService positionRecordService;

    @Override
    public Result<PageDto<PositionDetailRecord>> queryPageList(PositionQuery query) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.PositionQueryJsfService.queryPageList",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<PageDto<PositionDetailRecord>> result = new Result<PageDto<PositionDetailRecord>>();
        result.toFail();
        try {
            return positionRecordService.queryPageList(query);
        }catch (Exception e){
            logger.error("根据条件:{}分页查询异常!", JsonHelper.toJson(query), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public Result<Long> queryCountByCondition(PositionQuery query) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.PositionQueryJsfService.queryCountByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<Long> result = new Result<Long>();
        result.toFail();
        try {
            return positionRecordService.queryCountByCondition(query);
        }catch (Exception e){
            logger.error("根据条件:{}查询总数异常!", JsonHelper.toJson(query), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public Result<Boolean> updateByPositionCode(PositionRecord positionRecord) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.PositionQueryJsfService.updateByPositionCode",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<Boolean> result = new Result<Boolean>();
        result.toFail();
        try {
            return positionRecordService.updateByPositionCode(positionRecord);
        }catch (Exception e){
            logger.error("根据岗位编码:{}更新异常!", positionRecord.getPositionCode(), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }
}
