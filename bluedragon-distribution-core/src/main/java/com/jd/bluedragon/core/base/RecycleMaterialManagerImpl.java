package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.sdk.common.dto.ApiResult;
import com.jd.bluedragon.sdk.modules.recyclematerial.RecycleMaterialJsfService;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishReq;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishRes;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.RecycleMaterial;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 循环物资包装类
 *
 * @author hujiping
 * @date 2023/4/12 6:08 PM
 */
@Service("recycleMaterialManager")
public class RecycleMaterialManagerImpl implements RecycleMaterialManager {

    private static final Logger logger = LoggerFactory.getLogger(RecycleMaterialManagerImpl.class);

    @Autowired
    private RecycleMaterialJsfService recycleMaterialJsfService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.RecycleMaterialManager.batchInsertRecycleMaterial",
            mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public ApiResult<Integer> batchInsertRecycleMaterial(List<RecycleMaterial> list) {
        return recycleMaterialJsfService.batchInsertRecycleMaterial(list);
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.RecycleMaterialManager.findByMaterialCode",
            mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public ApiResult<RecycleMaterial> findByMaterialCode(String materialCode) {
        return recycleMaterialJsfService.findByMaterialCode(materialCode);
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.RecycleMaterialManager.disableMaterialByCode",
            mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public ApiResult<RecycleMaterial> disableMaterialByCode(String materialCode, String operateUserErp, Integer operateSiteCode) {
        return recycleMaterialJsfService.disableMaterialByCode(materialCode, operateUserErp, operateSiteCode);
    }

    @Override
    public ApiResult<MaterialAbolishRes> batchAbolishMaterial(MaterialAbolishReq abolishRequest) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.RecycleMaterialManager.batchAbolishMaterial",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return recycleMaterialJsfService.batchAbolishMaterial(abolishRequest);
        }catch (Exception e){
            logger.error("批量作废物资:{}异常!", JsonHelper.toJson(abolishRequest), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public ApiResult<Integer> countMaterialByCondition(Integer siteCode, Integer typeCode) {
        try {
            return recycleMaterialJsfService.countByCondition(siteCode, typeCode);
        }catch (Exception e){
            logger.error("查找周转筐总数：{}", e);
        }
        return null;
    };
}
