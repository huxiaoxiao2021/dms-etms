package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.spot.SpotCheckResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.enums.SpotCheckTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.*;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报表服务实现类
 *
 * @author hujiping
 * @date 2021/8/10 6:01 下午
 */
@Service("reportExternalManager")
public class ReportExternalManagerImpl implements ReportExternalManager {

    private static final Logger logger = LoggerFactory.getLogger(ReportExternalManagerImpl.class);

    @Autowired
    private ReportExternalService reportExternalService;

    @Override
    public Boolean insertOrUpdateForWeightVolume(WeightVolumeCollectDto weightVolumeCollectDto) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.ReportExternalManager.insertOrUpdateForWeightVolume",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            BaseEntity<String> baseEntity = reportExternalService.insertOrUpdateForWeightVolume(weightVolumeCollectDto);
            return baseEntity != null && baseEntity.isSuccess();
        }catch (Exception e){
            logger.error("运单号:{}的抽检数据落库异常!", weightVolumeCollectDto.getWaybillCode(), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return false;
    }

    @Override
    public Integer countByParam(WeightVolumeQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.ReportExternalManager.countByParam", Constants.UMP_APP_NAME_DMSWEB,false,true);
        int count = Constants.NUMBER_ZERO;
        try {
            BaseEntity<Long> baseEntity = reportExternalService.countByParam(condition);
            if(baseEntity != null && baseEntity.getData() != null){
                count = Integer.parseInt(String.valueOf(baseEntity.getData()));
            }
        }catch (Exception e){
            logger.error("根据条件查询抽检数量异常, 查询条件:{}", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return count;
    }

    @Override
    public List<String> getSpotCheckPackageCodesByCondition(WeightVolumeQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.ReportExternalManager.getSpotCheckPackageCodesByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        List<String> list = Lists.newArrayList();
        try {
            Pager<WeightVolumeQueryCondition> pagerCondition = new Pager<WeightVolumeQueryCondition>();
            pagerCondition.setPageNo(Constants.CONSTANT_NUMBER_ONE);
            pagerCondition.setPageSize(1000);
            pagerCondition.setSearchVo(condition);
            BaseEntity<Pager<String>> baseEntity = reportExternalService.getSpotCheckPackageCodesByCondition(pagerCondition);
            if(baseEntity != null && baseEntity.getData() != null){
                list = baseEntity.getData().getData();
            }
        }catch (Exception e){
            logger.error("根据条件:{}查询已抽检包裹异常!", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return list;
    }

    @Override
    public List<WeightVolumeCollectDto> queryByCondition(WeightVolumeQueryCondition condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.ReportExternalManager.queryByCondition",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        List<WeightVolumeCollectDto> list = Lists.newArrayList();
        try {
            BaseEntity<List<WeightVolumeCollectDto>> baseEntity = reportExternalService.getByParamForWeightVolume(condition);
            if(baseEntity != null && CollectionUtils.isNotEmpty(baseEntity.getData())){
                return baseEntity.getData();
            }
        }catch (Exception e){
            logger.error("根据条件:{}查询已抽检数据异常!", JsonHelper.toJson(condition), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return list;
    }

    @Override
    public JdResult<SpotCheckResponse> checkIsNeedSpotCheck(List<WaitSpotCheckQueryCondition> condition) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.ReportExternalManager.checkIsNeedSpotCheck",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        JdResult<SpotCheckResponse> result = new JdResult<SpotCheckResponse>();
        result.toSuccess();
        result.setData(new SpotCheckResponse());
        try {
            BaseEntity<Integer> baseEntity = reportExternalService.checkAndGetSpotCheckType(condition);
            if(baseEntity != null 
            		&& baseEntity.getData() != null
            		&& SpotCheckTypeEnum.containsCode(baseEntity.getData())){
            	result.getData().setNeedCheck(Boolean.TRUE);
            	result.getData().setSpotCheckType(baseEntity.getData());
            }
        }catch (Exception e){
            logger.error("根据条件:{}校验是否需要抽检异常!", JsonHelper.toJson(condition), e);
            result.toError("校验是否需要抽检异常!");
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }
}
