package com.jd.bluedragon.distribution.jy.service.config.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkStationManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.api.JyWorkMapFuncConfigJsfService;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigDetailVO;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigEntity;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncQuery;
import com.jd.bluedragon.distribution.jy.service.config.JyWorkMapFuncConfigService;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.workStation.WorkStation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 拣运功能配置对外jsf服务
 *
 * @author hujiping
 * @date 2022/4/6 6:11 PM
 */
@Service("jyWorkMapFuncConfigJsfService")
public class JyWorkMapFuncConfigJsfServiceImpl implements JyWorkMapFuncConfigJsfService {

    private static final Logger logger = LoggerFactory.getLogger(JyWorkMapFuncConfigJsfServiceImpl.class);

    @Autowired
    private JyWorkMapFuncConfigService jyWorkMapFuncConfigService;

//    @Autowired
//    private WorkStationService workStationService;

    @Autowired
    private WorkStationManager workStationManager;

    @Override
    public Result<Integer> addWorkMapFunConfig(JyWorkMapFuncConfigDetailVO record) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JyWorkMapFuncConfigJsfService.addWorkMapFunConfig",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<Integer> result = checkParams(record);
        if(!result.isSuccess()){
            return result;
        }
        try {
            WorkStation workStation = new WorkStation();
            workStation.setAreaCode(record.getAreaCode());
            workStation.setWorkCode(record.getWorkCode());
            com.jdl.basic.common.utils.Result<WorkStation> workStationResult = workStationManager.queryByBusinessKey(workStation);
            if(workStationResult == null || workStationResult.getData() == null){
                result.toFail(String.format("根据作业区:%s和网格:%s未查询到数据!", record.getAreaCode(), record.getWorkCode()));
                return result;
            }
            JyWorkMapFuncConfigEntity entity = new JyWorkMapFuncConfigEntity();
            entity.setRefWorkKey(workStationResult.getData().getBusinessKey());
            entity.setFuncCode(record.getFuncCode());
            entity.setCreateUser(record.getCreateUser());
            entity.setUpdateUser(record.getUpdateUser());
            if(CollectionUtils.isNotEmpty(jyWorkMapFuncConfigService.queryByCondition(entity))){
                result.toFail("此配置已存在，请勿重复添加!");
                return result;
            }
            result.setData(jyWorkMapFuncConfigService.addWorkMapFunConfig(entity));
        }catch (Exception e){
            logger.error("新增数据:{}异常!", JsonHelper.toJson(record), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    private Result<Integer> checkParams(JyWorkMapFuncConfigDetailVO record) {
        Result<Integer> result = new Result<Integer>();
        if(record == null){
            result.toFail("入参不能为空!");
            return result;
        }
        if(StringUtils.isEmpty(record.getFuncCode())){
            result.toFail("功能编码不能为空!");
            return result;
        }
        if(StringUtils.isEmpty(record.getAreaCode())){
            result.toFail("作业区不能为空!");
            return result;
        }
        if(StringUtils.isEmpty(record.getWorkCode())){
            result.toFail("网格不能为空!");
            return result;
        }
        result.toSuccess();
        return result;
    }

    @Override
    public Result<Integer> updateById(JyWorkMapFuncConfigDetailVO detailVO) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JyWorkMapFuncConfigJsfService.updateById",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<Integer> result = checkParams(detailVO);
        if(!result.isSuccess()){
            return result;
        }
        try {
            WorkStation workStation = new WorkStation();
            workStation.setAreaCode(detailVO.getAreaCode());
            workStation.setWorkCode(detailVO.getWorkCode());
            com.jdl.basic.common.utils.Result<WorkStation> workStationResult = workStationManager.queryByBusinessKey(workStation);
            if(workStationResult == null || workStationResult.getData() == null){
                result.toFail(String.format("根据作业区:%s和网格:%s未查询到数据!", detailVO.getAreaCode(), detailVO.getWorkCode()));
                return result;
            }
            JyWorkMapFuncConfigEntity entity = new JyWorkMapFuncConfigEntity();
            entity.setId(detailVO.getId());
            entity.setRefWorkKey(workStationResult.getData().getBusinessKey());
            entity.setFuncCode(detailVO.getFuncCode());
            entity.setUpdateUser(detailVO.getUpdateUser());
            result.setData(jyWorkMapFuncConfigService.updateById(entity));
        }catch (Exception e){
            logger.error("根据id:{}更新数据异常!", detailVO.getId(), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public Result<Integer> deleteById(JyWorkMapFuncConfigEntity entity) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JyWorkMapFuncConfigJsfService.deleteById",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<Integer> result = new Result<Integer>();
        result.toFail();
        try {
            result.toSuccess();
            result.setData(jyWorkMapFuncConfigService.deleteById(entity));
        }catch (Exception e){
            logger.error("根据id:{}更新数据异常!", entity.getId(), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }

    @Override
    public Result<PageDto<JyWorkMapFuncConfigDetailVO>> queryPageList(JyWorkMapFuncQuery query) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JyWorkMapFuncConfigJsfService.queryPageList",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        Result<PageDto<JyWorkMapFuncConfigDetailVO>> result = new Result<PageDto<JyWorkMapFuncConfigDetailVO>>();
        PageDto<JyWorkMapFuncConfigDetailVO> pageDto = new PageDto<JyWorkMapFuncConfigDetailVO>();
        result.toSuccess();
        try {
            PageDto<JyWorkMapFuncConfigEntity> queryResult = jyWorkMapFuncConfigService.queryPageList(query);
            if(CollectionUtils.isEmpty(queryResult.getResult())){
                pageDto.setResult(new ArrayList<JyWorkMapFuncConfigDetailVO>());
                pageDto.setTotalRow(0);
                result.setData(pageDto);
                return result;
            }
            List<JyWorkMapFuncConfigDetailVO> list = new ArrayList<>();
            for (JyWorkMapFuncConfigEntity entity : queryResult.getResult()) {
                JyWorkMapFuncConfigDetailVO detailVO = new JyWorkMapFuncConfigDetailVO();
                detailVO.setId(entity.getId());
                detailVO.setFuncCode(entity.getFuncCode());
                detailVO.setRefWorkKey(entity.getRefWorkKey());
                detailVO.setCreateUser(entity.getCreateUser());
                detailVO.setCreateTime(entity.getCreateTime());
                detailVO.setUpdateUser(entity.getUpdateUser());
                detailVO.setUpdateTime(entity.getUpdateTime());
                com.jdl.basic.common.utils.Result<WorkStation> workStationResult = workStationManager.queryByRealBusinessKey(entity.getRefWorkKey());
                if(workStationResult != null && workStationResult.getData() != null){
                    detailVO.setAreaCode(workStationResult.getData().getAreaCode());
                    detailVO.setAreaName(workStationResult.getData().getAreaName());
                    detailVO.setWorkCode(workStationResult.getData().getWorkCode());
                    detailVO.setWorkName(workStationResult.getData().getWorkName());
                }
                list.add(detailVO);
            }
            pageDto.setTotalRow(queryResult.getTotalRow());
            pageDto.setResult(list);
            result.setData(pageDto);
        }catch (Exception e){
            logger.error("根据条件:{}分页查询数据异常!", JsonHelper.toJson(query), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return result;
    }
}
