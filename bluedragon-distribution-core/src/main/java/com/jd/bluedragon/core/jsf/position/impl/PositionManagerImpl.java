package com.jd.bluedragon.core.jsf.position.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.response.JDResponse;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.common.utils.Result;
import com.jdl.basic.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/10 16:42
 * @Description:
 */
@Slf4j
@Service
public class PositionManagerImpl implements PositionManager {

    @Autowired
    private PositionQueryJsfService basicPositionQueryJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "PositionManagerImpl.queryOneByPositionCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<PositionDetailRecord> queryOneByPositionCode(String positionCode) {
        Result<PositionDetailRecord> result = new Result<>();
        result.toFail("获取岗位码信息数据失败");
        try {
            log.info("queryOneByPositionCode--获取基础服务数据");
            return basicPositionQueryJsfService.queryOneByPositionCode(positionCode);
        } catch (Exception e) {
            log.error("获取场地网格数据列表数据异常 {}",  e.getMessage(),e);
            result.toFail("获取场地网格数据异常!");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "PositionManagerImpl.queryPositionWithIsMatchAppFunc",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<PositionData> queryPositionWithIsMatchAppFunc(String positionCode) {
        Result<PositionData> result = new Result<>();
        result.toFail("获取岗位码信息数据失败");
        try {
            log.info("queryPositionWithIsMatchAppFunc--获取基础服务数据");
            return basicPositionQueryJsfService.queryPositionWithIsMatchAppFunc(positionCode);
        } catch (Exception e) {
            log.error("获取场地网格数据列表数据异常 {}",  e.getMessage(),e);
            result.toFail("获取场地网格数据异常!");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "PositionManagerImpl.queryPositionInfo",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<PositionData> queryPositionInfo(String positionCode) {
        Result<PositionData> result = new Result<>();
        result.toFail("获取岗位码信息数据失败");
        try {
            log.info("queryPositionInfo--获取基础服务数据");
            return basicPositionQueryJsfService.queryPositionInfo(positionCode);
        } catch (Exception e) {
            log.error("获取岗位码信息列表数据异常 {}",  e.getMessage(),e);
            result.toFail("获取岗位码信息数据异常!");
        }
        return result;
    }

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "PositionManagerImpl.queryPositionByGridKey",mState={JProEnum.TP,JProEnum.FunctionError})	
	public Result<PositionData> queryPositionByGridKey(String gridKey) {
        Result<PositionData> result = new Result<>();
        result.toFail("获取岗位码信息数据失败");
        try {
            log.info("queryPositionByGridKey--获取基础服务数据");
            return basicPositionQueryJsfService.queryPositionByGridKey(gridKey);
        } catch (Exception e) {
            log.error("获取岗位码信息列表数据异常 {}",  e.getMessage(),e);
            result.toFail("获取岗位码信息数据异常!");
        }
        return result;
	}

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "PositionManagerImpl.queryPositionCodeByRefGridKey",mState={JProEnum.TP,JProEnum.FunctionError})
    public String queryPositionCodeByRefGridKey(String refGridKey) {
        try {
            log.info("queryPositionInfo--获取基础服务数据");
            Result<String> result = basicPositionQueryJsfService.queryPositionCodeByRefGridKey(refGridKey);
            if (!result.isSuccess() || StringUtils.isEmpty(result.getData())) {
                return null;
            }
            return result.getData();
        } catch (Exception e) {
            log.error("根据业务主键查询岗位码异常 {}",  e.getMessage(),e);
            return null;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "PositionManagerImpl.basicPositionQueryJsfService",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<String> queryWorkGridKeyByPositionCode(String positionCode){
        return basicPositionQueryJsfService.queryWorkGridKeyByPositionCode(positionCode);
    }


}
