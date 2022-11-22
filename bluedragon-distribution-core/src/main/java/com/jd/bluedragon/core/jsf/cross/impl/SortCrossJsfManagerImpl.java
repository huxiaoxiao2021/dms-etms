package com.jd.bluedragon.core.jsf.cross.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.cross.CrossDataJsfResp;
import com.jdl.basic.api.domain.cross.CrossPageQuery;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfResp;
import com.jdl.basic.api.domain.cross.TableTrolleyQuery;
import com.jdl.basic.api.service.cross.SortCrossJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liwenji
 * @date 2022-11-16 9:17
 */
@Service
@Slf4j
public class SortCrossJsfManagerImpl implements SortCrossJsfManager {
    
    @Autowired
    private SortCrossJsfService sortCrossJsfService;
    
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "SortCrossJsfManagerImpl.queryCrossDataByDmsCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public CrossDataJsfResp queryCrossDataByDmsCode(CrossPageQuery query) {
        try {
            Result<CrossDataJsfResp> result = sortCrossJsfService.queryCrossDataByDmsCode(query);
            if (result != null && result.isSuccess()){
                return result.getData();
            }
        }catch (Exception e) {
            log.error("分页获取场地：{}滑道信息错误 {}",query.getDmsId(),e);
        }
        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "SortCrossJsfManagerImpl.queryTableTrolleyListByCrossCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public TableTrolleyJsfResp queryTableTrolleyListByCrossCode(TableTrolleyQuery query) {
        try {
            Result<TableTrolleyJsfResp> result = sortCrossJsfService.queryTableTrolleyListByCrossCode(query);
            if (result != null && result.isSuccess()){
                return result.getData();
            }
        }catch (Exception e) {
            log.error("分页获取滑道下的笼车信息错误 {}",JsonHelper.toJson(query),e);
        }
        return null;    
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "SortCrossJsfManagerImpl.queryTableTrolleyListByDmsId",mState={JProEnum.TP,JProEnum.FunctionError})
    public TableTrolleyJsfResp queryTableTrolleyListByDmsId(TableTrolleyQuery query) {
        try {
            Result<TableTrolleyJsfResp> result = sortCrossJsfService.queryTableTrolleyListByDmsId(query);
            if (result != null && result.isSuccess()){
                return result.getData();
            }
        }catch (Exception e) {
            log.error("分页获取场地：{}笼车信息错误 {}",query.getDmsId(),e);
        }
        return null;    
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "SortCrossJsfManagerImpl.queryCTTByStartEndSiteCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public TableTrolleyJsfResp queryCTTByStartEndSiteCode(TableTrolleyQuery query) {
        try {
            Result<TableTrolleyJsfResp> result = sortCrossJsfService.queryCTTByStartEndSiteCode(query);
            if (result != null && result.isSuccess()){
                return result.getData();
            }
        }catch (Exception e) {
            log.error("根据始发和目的地站点查询滑道笼车信息失败：{}", JsonHelper.toJson(query),e);
        }
        return null;     
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "SortCrossJsfManagerImpl.queryCTTByCTTCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public TableTrolleyJsfResp queryCTTByCTTCode(TableTrolleyQuery query) {
        try {
            Result<TableTrolleyJsfResp> result = sortCrossJsfService.queryCTTByCTTCode(query);
            if (result != null && result.isSuccess()){
                return result.getData();
            }
        }catch (Exception e) {
            log.error("根据滑道笼车信息获取流向信息失败：{}",JsonHelper.toJson(query),e);
        }
        return null;     
    }
}
