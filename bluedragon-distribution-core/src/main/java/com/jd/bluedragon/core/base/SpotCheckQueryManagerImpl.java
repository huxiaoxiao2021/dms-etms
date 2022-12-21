package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.SpotCheckQueryService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckScrollResult;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckUpdateRequest;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 抽检查询接口包装服务
 *
 * @author hujiping
 * @date 2021/12/13 3:02 下午
 */
@Service("spotCheckQueryManager")
public class SpotCheckQueryManagerImpl implements SpotCheckQueryManager {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckQueryManagerImpl.class);

    @Autowired
    private SpotCheckQueryService spotCheckQueryService;

    @Override
    public Boolean insertOrUpdateSpotCheck(WeightVolumeSpotCheckDto dto) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.insertOrUpdateSpotCheck",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<Boolean> baseEntity = spotCheckQueryService.insertOrUpdateSpotCheck(dto);
            if(baseEntity == null || baseEntity.getData() == null || !baseEntity.getData()){
                return false;
            }
            return true;
        }catch (Exception e){
            logger.error("新增或修改抽检数据异常!包裹号:{}", dto.getPackageCode(), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return false;
    }

    @Override
    public Pager<WeightVolumeSpotCheckDto> querySpotCheckByPage(Pager<SpotCheckQueryCondition> pager) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.querySpotCheckByPage",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Pager<WeightVolumeSpotCheckDto> pagerResult = new Pager<>();
        try {
            BaseEntity<Pager<WeightVolumeSpotCheckDto>> baseEntity = spotCheckQueryService.querySpotCheckByPage(pager);
            if(baseEntity == null || baseEntity.getData() == null){
                return pagerResult;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("分页查询抽检数据异常!查询条件:{}", JsonHelper.toJson(pager), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return pagerResult;
    }

    @Override
    public SpotCheckScrollResult querySpotCheckByScroll(SpotCheckQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.querySpotCheckByScroll",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<SpotCheckScrollResult> baseEntity = spotCheckQueryService.querySpotCheckByScroll(condition);
            if(baseEntity == null || baseEntity.getData() == null){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("scroll查询抽检数据异常!查询条件:{}", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public Integer querySpotCheckCountByCondition(SpotCheckQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.querySpotCheckCountByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<Integer> baseEntity = spotCheckQueryService.querySpotCheckCountByCondition(condition);
            if(baseEntity == null || baseEntity.getData() == null){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("根据条件查询已抽检数量异常!查询条件:{}", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public List<String> getSpotCheckPackByCondition(SpotCheckQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.getSpotCheckPackByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<List<String>> baseEntity = spotCheckQueryService.getSpotCheckPackByCondition(condition);
            if(baseEntity == null || baseEntity.getData() == null){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("根据条件查询已抽检包裹号异常!查询条件:{}", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public List<WeightVolumeSpotCheckDto> querySpotCheckByCondition(SpotCheckQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.querySpotCheckByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<List<WeightVolumeSpotCheckDto>> baseEntity = spotCheckQueryService.querySpotCheckByCondition(condition);
            if(baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("根据条件查询已抽检数据异常!查询条件:{}", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public List<WeightVolumeSpotCheckDto> queryAllSpotCheckByCondition(SpotCheckQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.queryAllSpotCheckByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<List<WeightVolumeSpotCheckDto>> baseEntity = spotCheckQueryService.queryAllSpotCheckByCondition(condition);
            if(baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("根据条件查询所有抽检数据异常!查询条件:{}", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public Integer batchUpdateMachineStatus(SpotCheckUpdateRequest updateRequest) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.SpotCheckQueryManager.batchUpdateMachineStatus",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<Integer> baseEntity = spotCheckQueryService.batchUpdateMachineStatus(updateRequest);
            if(baseEntity == null || baseEntity.getData() == null){
                return null;
            }
            return baseEntity.getData();
        }catch (Exception e){
            logger.error("根据条件:{}批量更新抽检数据设备状态异常!", JsonHelper.toJson(updateRequest), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
