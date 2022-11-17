package com.jd.bluedragon.core.jsf.cross.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
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
            log.error("分页获取场地：{}滑道信息错误 {}",query.getDmsId(),e);
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
            log.error("分页获取场地：{}滑道信息错误 {}",query.getDmsId(),e);
        }
        return null;    
    }
}
