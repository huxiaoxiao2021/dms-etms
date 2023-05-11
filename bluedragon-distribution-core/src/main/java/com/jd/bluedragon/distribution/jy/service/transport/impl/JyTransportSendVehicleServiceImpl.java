package com.jd.bluedragon.distribution.jy.service.transport.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.dock.service.DockService;
import com.jd.bluedragon.distribution.external.service.TransportCommonService;
import com.jd.bluedragon.distribution.jy.constants.JyCacheKeyConstants;
import com.jd.bluedragon.distribution.jy.transport.api.JyTransportSendVehicleService;
import com.jd.bluedragon.distribution.jy.transport.dto.*;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.common.utils.JsonHelper;
import com.jdl.basic.common.utils.PageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 拣运运输车辆服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-10 17:56:46 周三
 */
@Slf4j
@Service("jyTransportSendVehicleService")
public class JyTransportSendVehicleServiceImpl implements JyTransportSendVehicleService {

    @Autowired
    private WorkStationGridManager workStationGridManager;

    @Autowired
    private DockService dockService;

    @Autowired
    private TransportCommonService transportCommonService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 验证运输发货车辆到达月台合法性
     *
     * @param requestDto 入参
     * @return 校验结果
     * @author fanggang7
     * @time 2023-05-08 21:05:43 周一
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JyTransportSendVehicleServiceImpl.validateVehicleArriveDock", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<VehicleArriveDockResponseDto> validateVehicleArriveDock(VehicleArriveDockRequestDto requestDto) {
        log.info("JyTransportSendVehicleServiceImpl.validateVehicleArriveDock param {}", JSON.toJSONString(requestDto));
        Result<VehicleArriveDockResponseDto> result = Result.success();
        final VehicleArriveDockResponseDto vehicleArriveDockResponseDto = new VehicleArriveDockResponseDto();
        result.setData(vehicleArriveDockResponseDto);
        try {
            final Result<Void> checkResult = this.checkParam4validateVehicleArriveDock(requestDto);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            final BaseStaffSiteOrgDto siteStartInfo = baseMajorManager.getBaseSiteByDmsCode(requestDto.getBeginNodeCode());
            if (siteStartInfo == null) {
                return result.toFail(String.format("未查询到始发场地编码为%s的场地数据", requestDto.getBeginNodeCode()));
            }
            final BaseStaffSiteOrgDto siteEndInfo = baseMajorManager.getBaseSiteByDmsCode(requestDto.getEndNodeCode());
            if (siteEndInfo == null) {
                return result.toFail(String.format("未查询到目的场地编码为%s的场地数据", requestDto.getEndNodeCode()));
            }
            final String validateStrCacheVal = redisClientOfJy.get(this.getValidateStrCacheKey(requestDto.getValidateStr()));
            if(validateStrCacheVal == null){
                vehicleArriveDockResponseDto.setLegal(false);
                vehicleArriveDockResponseDto.setValidateMsg("二维码已过期");
                return result;
            }
            // 从缓存取数据
            final VehicleArriveDockDataCacheDto vehicleArriveDockDataCacheDto = JSON.parseObject(validateStrCacheVal, VehicleArriveDockDataCacheDto.class);
            if (!Objects.equals(siteStartInfo.getSiteCode(), vehicleArriveDockDataCacheDto.getSiteId())) {
                vehicleArriveDockResponseDto.setLegal(false);
                vehicleArriveDockResponseDto.setValidateMsg("校验失败，场地不一致！");
                return result;
            }
            vehicleArriveDockResponseDto.setLegal(true);
            vehicleArriveDockResponseDto.setDockCode(vehicleArriveDockDataCacheDto.getDockCode());
            vehicleArriveDockResponseDto.setSiteId(vehicleArriveDockDataCacheDto.getSiteId());
            vehicleArriveDockResponseDto.setSiteCode(vehicleArriveDockDataCacheDto.getSiteCode());
            vehicleArriveDockResponseDto.setSiteName(vehicleArriveDockDataCacheDto.getSiteName());
            vehicleArriveDockResponseDto.setWorkAreaCode(vehicleArriveDockDataCacheDto.getWorkAreaCode());
            vehicleArriveDockResponseDto.setWorkAreaName(vehicleArriveDockDataCacheDto.getWorkAreaName());
            vehicleArriveDockResponseDto.setWorkGridNo(vehicleArriveDockDataCacheDto.getWorkGridNo());
            vehicleArriveDockResponseDto.setWorkGridName(vehicleArriveDockDataCacheDto.getWorkGridName());
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.validateVehicleArriveDock exception {}", JSON.toJSONString(requestDto), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4validateVehicleArriveDock(VehicleArriveDockRequestDto requestDto) {
        Result<Void> result = Result.success();

        if (StringUtils.isEmpty(requestDto.getValidateStr())) {
            return result.toFail("参数错误，validateStr不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getVehicleNumber())) {
            return result.toFail("参数错误，vehicleNumber不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getTransWorkItemCode())) {
            return result.toFail("参数错误，transWorkItemCode不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getBeginNodeCode())) {
            return result.toFail("参数错误，beginNodeCode不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getEndNodeCode())) {
            return result.toFail("参数错误，endNodeCode不能为空");
        }
        if (requestDto.getCarrierType() == null) {
            return result.toFail("参数错误，carrierType不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getUserCode())) {
            return result.toFail("参数错误，userCode不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getUserName())) {
            return result.toFail("参数错误，userName不能为空");
        }
        return result;
    }

    /**
     * 获取运输车辆基础数据
     *
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 10:55:11 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JyTransportSendVehicleServiceImpl.getVehicleArriveDockBaseData", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<VehicleArriveDockBaseDataDto> getVehicleArriveDockBaseData(VehicleArriveDockBaseDataQo qo) {
        log.info("JyTransportSendVehicleServiceImpl.getVehicleArriveDockBaseData param {}", JSON.toJSONString(qo));
        Result<VehicleArriveDockBaseDataDto> result = Result.success();
        try {
            final VehicleArriveDockBaseDataDto vehicleArriveDockBaseDataDto = new VehicleArriveDockBaseDataDto();
            result.setData(vehicleArriveDockBaseDataDto);


            List<DockInfoEntity> dockList = new ArrayList<>();
            vehicleArriveDockBaseDataDto.setDockList(dockList);
            vehicleArriveDockBaseDataDto.setTimeMillSeconds(System.currentTimeMillis());
            vehicleArriveDockBaseDataDto.setTimeFormatStr(DateUtil.FORMAT_DATE_TIME);
            vehicleArriveDockBaseDataDto.setTimeStr(DateUtil.format(new Date(vehicleArriveDockBaseDataDto.getTimeMillSeconds()), DateUtil.FORMAT_DATE_TIME));

            final BaseStaffSiteOrgDto baseSiteInfo = baseMajorManager.getBaseSiteBySiteId(qo.getStartSiteId());
            if (baseSiteInfo == null) {
                return result.toFail(String.format("未查询到场地编码为%s的场地数据", qo.getStartSiteCode()));
            }
            vehicleArriveDockBaseDataDto.setSiteId(baseSiteInfo.getSiteCode());
            vehicleArriveDockBaseDataDto.setSiteName(baseSiteInfo.getSiteName());
            // 查询场地所有月台
            final InvokeResult<List<DockInfoEntity>> dockListResult = transportCommonService.listAllDockInfoBySiteCode(baseSiteInfo.getSiteCode());
            if (dockListResult == null || !dockListResult.codeSuccess()) {
                return result.toFail(String.format("查询场地的月台数据失败，场地编码%s", qo.getStartSiteCode()));
            }
            final List<DockInfoEntity> dockListData = dockListResult.getData();
            if (CollectionUtils.isNotEmpty(dockListData)) {
                dockList.addAll(dockListData);
            }
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.getVehicleArriveDockBaseData exception {}", JSON.toJSONString(qo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 获取运输车辆靠台数据
     *
     * @param qo 查询入参
     * @return 查询结果
     * @author fanggang7
     * @time 2023-05-09 11:04:49 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JyTransportSendVehicleServiceImpl.getVehicleArriveDockData", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<VehicleArriveDockDataDto> getVehicleArriveDockData(VehicleArriveDockDataQo qo) {
        log.info("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData param {}", JSON.toJSONString(qo));
        Result<VehicleArriveDockDataDto> result = Result.success();
        try {
            // todo 增加刷新校验
            final VehicleArriveDockDataDto vehicleArriveDockDataDto = new VehicleArriveDockDataDto();
            vehicleArriveDockDataDto.setSiteId(qo.getStartSiteId());
            vehicleArriveDockDataDto.setTimeMillSeconds(System.currentTimeMillis());
            vehicleArriveDockDataDto.setTimeFormatStr(DateUtil.FORMAT_DATE_TIME);
            vehicleArriveDockDataDto.setTimeStr(DateUtil.format(new Date(vehicleArriveDockDataDto.getTimeMillSeconds()), DateUtil.FORMAT_DATE_TIME));
            vehicleArriveDockDataDto.setDockCode(qo.getDockCode());
            vehicleArriveDockDataDto.setValidateStrRefreshIntervalTime(uccPropertyConfiguration.getJyTransportSendVehicleValidateDockFreshTime());

            result.setData(vehicleArriveDockDataDto);

            final BaseStaffSiteOrgDto baseSiteInfo = baseMajorManager.getBaseSiteBySiteId(qo.getStartSiteId());
            if (baseSiteInfo == null) {
                return result.toFail(String.format("未查询到场地编码为%s的场地数据", qo.getStartSiteId()));
            }

            // 根据月台号查询月台信息
            DockInfoEntity dockInfoEntity = new DockInfoEntity();
            dockInfoEntity.setDockCode(qo.getDockCode());
            dockInfoEntity.setSiteCode(qo.getStartSiteId());
            final Response<DockInfoEntity> dockInfoResponse = dockService.queryDockInfoByDockCode(dockInfoEntity);
            if (dockInfoResponse == null || !dockInfoResponse.isSucceed()) {
                log.warn("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData queryDockInfoByDockCode fail {}, 月台号{}，场地ID{}", JsonHelper.toJSONString(dockInfoResponse), qo.getDockCode(), qo.getStartSiteId());
                return result.toFail(String.format("查询场地的月台数据失败，月台号%s，场地ID%s", qo.getDockCode(), qo.getStartSiteId()));
            }

            // 根据月台号查询作业区信息
            final WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
            workStationGridQuery.setDockCode(qo.getDockCode());
            workStationGridQuery.setSiteCode(qo.getStartSiteId());
            final com.jdl.basic.common.utils.Result<Long> workStationGridResult = workStationGridManager.queryCount(workStationGridQuery);
            if (workStationGridResult == null || !workStationGridResult.isSuccess()) {
                log.warn("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData queryCount fail {},月台号{}，场地ID{}", JsonHelper.toJSONString(workStationGridResult), qo.getDockCode(), qo.getStartSiteId());
                return result.toFail(String.format("查询场地的月台作业区数据失败，月台号%s，场地ID%s", qo.getDockCode(), qo.getStartSiteId()));
            }
            workStationGridQuery.setPageSize(1);
            final com.jdl.basic.common.utils.Result<PageDto<WorkStationGrid>> workStationGridPageResult = workStationGridManager.queryPageList(workStationGridQuery);
            if (workStationGridPageResult == null || !workStationGridPageResult.isSuccess()) {
                log.warn("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData queryCount fail {},月台号{}，场地ID{}", JsonHelper.toJSONString(workStationGridPageResult), qo.getDockCode(), qo.getStartSiteId());
                return result.toFail(String.format("查询场地的月台作业区数据失败，月台号%s，场地ID%s", qo.getDockCode(), qo.getStartSiteId()));
            }
            final PageDto<WorkStationGrid> workStationGridPageData = workStationGridPageResult.getData();
            if (workStationGridPageData != null && CollectionUtils.isNotEmpty(workStationGridPageData.getResult())) {
                final WorkStationGrid workStationGrid = workStationGridPageData.getResult().get(0);
                vehicleArriveDockDataDto.setSiteCode(baseSiteInfo.getDmsSiteCode());
                vehicleArriveDockDataDto.setSiteName(baseSiteInfo.getSiteName());
                vehicleArriveDockDataDto.setWorkGridNo(workStationGrid.getGridNo());
                vehicleArriveDockDataDto.setWorkGridName(workStationGrid.getWorkName());
                vehicleArriveDockDataDto.setWorkAreaCode(workStationGrid.getAreaCode());
                vehicleArriveDockDataDto.setWorkAreaName(workStationGrid.getGridName());
            }
            // 生成验证字符串
            final String uuidStr = UUID.randomUUID().toString().toUpperCase();
            String validateOriginStr = String.format("%s__%s__%s__%s__%s", vehicleArriveDockDataDto.getSiteId(),
                    vehicleArriveDockDataDto.getWorkAreaCode(), vehicleArriveDockDataDto.getWorkGridNo(), vehicleArriveDockDataDto.getDockCode(), uuidStr);
            final String validateStr = this.getValidateStrCacheKey(Md5Helper.encode(validateOriginStr));
            vehicleArriveDockDataDto.setValidateStr(validateStr);

            final VehicleArriveDockDataCacheDto vehicleArriveDockDataCacheDto = new VehicleArriveDockDataCacheDto();
            BeanCopyUtil.copy(vehicleArriveDockDataDto, vehicleArriveDockDataCacheDto);
            vehicleArriveDockDataCacheDto.setCreateTimeMillSeconds(System.currentTimeMillis());
            // 保存10分钟缓存
            redisClientOfJy.setEx(validateStr, JsonHelper.toJSONString(vehicleArriveDockDataCacheDto), (int)(uccPropertyConfiguration.getJyTransportSendVehicleValidateDockFreshTime() * uccPropertyConfiguration.getJyTransportSendVehicleValidateDockAllowFreshTimes()), JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR_TIME_UINT);

            // 将上几次验证字符缓存删除
            /*int validateLastGenerateStrExpired = uccPropertyConfiguration.getJyTransportSendVehicleValidateDockFreshTime() * uccPropertyConfiguration.getJyTransportSendVehicleValidateDockAllowFreshTimes() + 5;
            List<String> lastGenerateValidateStrArr = new ArrayList<>();
            final String validateLastGenerateVal = redisClientOfJy.get(JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_LAST_GENERATE_STR);
            if (StringUtils.isNotEmpty(validateLastGenerateVal)) {
                final List<String> validateLastGenerateArrExist = JSON.parseArray(validateLastGenerateVal, String.class);
                // 如果上次验证码缓存总数已超过允许的刷新个数，则删除队首
                if(validateLastGenerateArrExist.size() >= uccPropertyConfiguration.getJyTransportSendVehicleValidateDockAllowFreshTimes()){
                    final String validateStrDiscarded = validateLastGenerateArrExist.get(0);
                    redisClientOfJy.del(validateStrDiscarded);
                    validateLastGenerateArrExist.remove(0);
                }
                lastGenerateValidateStrArr.addAll(validateLastGenerateArrExist);
            }
            // 将新的验证码追加到队尾
            lastGenerateValidateStrArr.add(validateStr);
            // 存到缓存
            redisClientOfJy.setEx(JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_LAST_GENERATE_STR, JsonHelper.toJSONString(lastGenerateValidateStrArr), validateLastGenerateStrExpired, JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR_TIME_UINT);*/
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData exception {}", JSON.toJSONString(qo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private String getValidateStrCacheKey(String validateStr) {
        return String.format(JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR, validateStr);
    }
}
