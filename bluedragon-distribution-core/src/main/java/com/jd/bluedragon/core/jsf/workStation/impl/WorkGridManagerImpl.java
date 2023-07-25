package com.jd.bluedragon.core.jsf.workStation.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkGridQuery;
import com.jdl.basic.api.service.workStation.WorkGridJsfService;
import com.jdl.basic.common.utils.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 15:12
 * @Description: 三定场地网格工序管理
 */
@Slf4j
@Service
public class WorkGridManagerImpl implements WorkGridManager {

    @Autowired
    private WorkGridJsfService workGridJsfService;
    
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.workGridJsfService.";

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "queryByWorkGridKey",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<WorkGrid> queryByWorkGridKey(String workGridKey) {
        Result<WorkGrid> result = new Result<>();
        result.toFail("获取三定场地网格数据失败");
        try {
            log.info("三定场地网格管理 queryByWorkGridKey 入参:"+ workGridKey);
            return workGridJsfService.queryByWorkGridKey(workGridKey);
        } catch (Exception e) {
            log.error("获取三定场地网格数据异常 {}",  e.getMessage(),e);
            result.toFail("获取三定场地网格异常!");
        }
       return result;
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "querySiteListForManagerScan",mState={JProEnum.TP,JProEnum.FunctionError})
	@Override
	public List<Integer> querySiteListForManagerScan(WorkGridQuery workGridQuery) {
    	List<Integer> result= new ArrayList<>();
        try {
            log.info("三定场地网格管理 querySiteListForManagerScan param:"+ JSON.toJSONString(workGridQuery));
            return workGridJsfService.querySiteListForManagerScan(workGridQuery);
        } catch (Exception e) {
            log.error("获取三定场地列表数据异常 {}",  e.getMessage(),e);
        }
        return result;
	}
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "queryListForManagerSiteScan",mState={JProEnum.TP,JProEnum.FunctionError})
	@Override
	public List<WorkGrid> queryListForManagerSiteScan(WorkGridQuery workGridQuery) {
    	List<WorkGrid> result= new ArrayList<>();
        try {
            log.info("三定场地网格管理 queryListForManagerSiteScan param:"+ JSON.toJSONString(workGridQuery));
            return workGridJsfService.queryListForManagerSiteScan(workGridQuery);
        } catch (Exception e) {
            log.error("获取三定场地网格列表数据异常 {}",  e.getMessage(),e);
        }
        return result;
	}
}
