package com.jd.bluedragon.distribution.jy.service.transport.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.dock.service.DockService;
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
import java.util.concurrent.TimeUnit;

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
            final String validateStrGenerateCacheKey = this.getValidateGenerateCacheKey(siteStartInfo.getSiteCode(), requestDto.getValidateStr());
            final String validateStrCacheVal = redisClientOfJy.get(validateStrGenerateCacheKey);
            log.info("JyTransportSendVehicleServiceImpl.validateVehicleArriveDock param {}, val {}", validateStrCacheVal, requestDto.getValidateStr());
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
            final String validateLastGenerateCacheKey = this.getValidateLastGenerateCacheKey(vehicleArriveDockDataCacheDto.getSiteId(), vehicleArriveDockDataCacheDto.getCreateUserCode());
            final String validateLastGenerateVal = redisClientOfJy.get(validateLastGenerateCacheKey);
            log.info("JyTransportSendVehicleServiceImpl.validateVehicleArriveDock param {}, validateLastGenerateVal {}", validateStrCacheVal, requestDto.getValidateStr());
            if (StringUtils.isNotEmpty(validateLastGenerateVal)) {
                final List<String> validateLastGenerateArrExist = JSON.parseArray(validateLastGenerateVal, String.class);
                if (!validateLastGenerateArrExist.contains(requestDto.getValidateStr())) {
                    vehicleArriveDockResponseDto.setLegal(false);
                    vehicleArriveDockResponseDto.setValidateMsg("二维码已过期");
                    return result;
                }
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
            vehicleArriveDockBaseDataDto.setValidateStrRefreshIntervalTime(uccPropertyConfiguration.getJyTransportSendVehicleValidateDockRefreshTime());

            final BaseStaffSiteOrgDto baseSiteInfo = baseMajorManager.getBaseSiteBySiteId(qo.getStartSiteId());
            if (baseSiteInfo == null) {
                return result.toFail(String.format("未查询到场地编码为%s的场地数据", qo.getStartSiteCode()));
            }
            vehicleArriveDockBaseDataDto.setSiteId(baseSiteInfo.getSiteCode());
            vehicleArriveDockBaseDataDto.setSiteName(baseSiteInfo.getSiteName());
            // 查询场地所有月台
            final Response<List<DockInfoEntity>> dockListResult = dockService.listAllDockInfoBySiteCode(baseSiteInfo.getSiteCode());
            if (dockListResult == null || !dockListResult.isSucceed()) {
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
            final Result<Void> checkResult = this.checkParam4getVehicleArriveDockData(qo);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            // 限制频繁刷新
            final OperateUser operateUser = qo.getOperateUser();
            String validateLastGenerateTimeCacheKey = this.getValidateLastGenerateTimeCacheKey(qo.getStartSiteId(), operateUser.getUserCode());
            final String validateLastGenerateTimeCacheVal = redisClientOfJy.get(validateLastGenerateTimeCacheKey);
            if (validateLastGenerateTimeCacheVal != null) {
                long validateLastGenerateTimeCacheMillSeconds = Long.parseLong(validateLastGenerateTimeCacheVal);
                // 限制1个用户3秒内钟刷新一次
                if (System.currentTimeMillis() < (validateLastGenerateTimeCacheMillSeconds + 1000L)) {
                    return result.toFail("请勿频繁刷新！");
                }
            }

            final VehicleArriveDockDataDto vehicleArriveDockDataDto = new VehicleArriveDockDataDto();
            vehicleArriveDockDataDto.setSiteId(qo.getStartSiteId());
            vehicleArriveDockDataDto.setTimeMillSeconds(System.currentTimeMillis());
            vehicleArriveDockDataDto.setTimeFormatStr(DateUtil.FORMAT_DATE_TIME);
            vehicleArriveDockDataDto.setTimeStr(DateUtil.format(new Date(vehicleArriveDockDataDto.getTimeMillSeconds()), DateUtil.FORMAT_DATE_TIME));
            vehicleArriveDockDataDto.setDockCode(qo.getDockCode());
            vehicleArriveDockDataDto.setValidateStrRefreshIntervalTime(uccPropertyConfiguration.getJyTransportSendVehicleValidateDockRefreshTime());

            result.setData(vehicleArriveDockDataDto);

            final BaseStaffSiteOrgDto baseSiteInfo = baseMajorManager.getBaseSiteBySiteId(qo.getStartSiteId());
            if (baseSiteInfo == null) {
                return result.toFail(String.format("未查询到场地编码为%s的场地数据", qo.getStartSiteId()));
            }

            vehicleArriveDockDataDto.setSiteCode(baseSiteInfo.getDmsSiteCode());
            vehicleArriveDockDataDto.setSiteName(baseSiteInfo.getSiteName());

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
            workStationGridQuery.setPageSize(1);
            final com.jdl.basic.common.utils.Result<PageDto<WorkStationGrid>> workStationGridPageResult = workStationGridManager.queryPageList(workStationGridQuery);
            if (workStationGridPageResult == null || !workStationGridPageResult.isSuccess()) {
                log.warn("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData queryCount fail {},月台号{}，场地ID{}", JsonHelper.toJSONString(workStationGridPageResult), qo.getDockCode(), qo.getStartSiteId());
                return result.toFail(String.format("查询场地的月台作业区数据失败，月台号%s，场地ID%s", qo.getDockCode(), qo.getStartSiteId()));
            }

            final PageDto<WorkStationGrid> workStationGridPageData = workStationGridPageResult.getData();
            if (workStationGridPageData != null && CollectionUtils.isNotEmpty(workStationGridPageData.getResult())) {
                final WorkStationGrid workStationGrid = workStationGridPageData.getResult().get(0);
                vehicleArriveDockDataDto.setWorkGridNo(workStationGrid.getGridNo());
                vehicleArriveDockDataDto.setWorkGridName(workStationGrid.getWorkName());
                vehicleArriveDockDataDto.setWorkAreaCode(workStationGrid.getAreaCode());
                vehicleArriveDockDataDto.setWorkAreaName(workStationGrid.getGridName());
            } else {
                return result;
            }

            // 生成验证字符串
            final String uuidStr = UUID.randomUUID().toString().toUpperCase();
            String validateOriginStr = String.format("%s__%s__%s__%s__%s", vehicleArriveDockDataDto.getSiteId(),
                    vehicleArriveDockDataDto.getWorkAreaCode(), vehicleArriveDockDataDto.getWorkGridNo(), vehicleArriveDockDataDto.getDockCode(), uuidStr);
            String validateStr = Md5Helper.encode(validateOriginStr);
            vehicleArriveDockDataDto.setValidateStr(validateStr);
            log.info("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData generate validateStr {} param {}", validateStr, JSON.toJSONString(qo));

            final VehicleArriveDockDataCacheDto vehicleArriveDockDataCacheDto = new VehicleArriveDockDataCacheDto();
            BeanCopyUtil.copy(vehicleArriveDockDataDto, vehicleArriveDockDataCacheDto);
            vehicleArriveDockDataCacheDto.setCreateTimeMillSeconds(System.currentTimeMillis());
            vehicleArriveDockDataCacheDto.setCreateUserCode(operateUser.getUserCode());
            // 保存10分钟缓存，验证码的信息
            final String validateStrGenerateCacheKey = this.getValidateGenerateCacheKey(qo.getStartSiteId(), validateStr);
            redisClientOfJy.setEx(validateStrGenerateCacheKey, JsonHelper.toJSONString(vehicleArriveDockDataCacheDto), (int)(uccPropertyConfiguration.getJyTransportSendVehicleValidateDockRefreshTime() * uccPropertyConfiguration.getJyTransportSendVehicleValidateDockAllowRefreshTimes()), JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR_TIME_UINT);

            // 将上几次验证字符缓存删除，来验证有效性
            int validateLastGenerateStrExpired = uccPropertyConfiguration.getJyTransportSendVehicleValidateDockRefreshTime() * uccPropertyConfiguration.getJyTransportSendVehicleValidateDockAllowRefreshTimes() + 5;
            List<String> lastGenerateValidateStrArr = new ArrayList<>();
            final String validateLastGenerateCacheKey = this.getValidateLastGenerateCacheKey(baseSiteInfo.getSiteCode(), operateUser.getUserCode());
            final String validateLastGenerateVal = redisClientOfJy.get(validateLastGenerateCacheKey);
            if (StringUtils.isNotEmpty(validateLastGenerateVal)) {
                final List<String> validateLastGenerateArrExist = JSON.parseArray(validateLastGenerateVal, String.class);
                // 如果上次验证码缓存总数已超过允许的刷新个数，则删除队首
                if(validateLastGenerateArrExist.size() >= uccPropertyConfiguration.getJyTransportSendVehicleValidateDockAllowRefreshTimes()){
                    final String validateStrDiscarded = validateLastGenerateArrExist.get(0);
                    redisClientOfJy.del(validateStrDiscarded);
                    validateLastGenerateArrExist.remove(0);
                }
                lastGenerateValidateStrArr.addAll(validateLastGenerateArrExist);
            }
            // 将新的验证码追加到队尾
            lastGenerateValidateStrArr.add(validateStr);
            // 存到缓存
            redisClientOfJy.setEx(validateLastGenerateCacheKey, JsonHelper.toJSONString(lastGenerateValidateStrArr), validateLastGenerateStrExpired, JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR_TIME_UINT);
            redisClientOfJy.setEx(validateLastGenerateTimeCacheKey, String.valueOf(System.currentTimeMillis()), 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("JyTransportSendVehicleServiceImpl.getVehicleArriveDockData exception {}", JSON.toJSONString(qo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4getVehicleArriveDockData(VehicleArriveDockDataQo qo) {
        Result<Void> result = Result.success();

        if (StringUtils.isEmpty(qo.getDockCode())) {
            return result.toFail("参数错误，dockCode不能为空");
        }
        if (qo.getStartSiteId() == null) {
            return result.toFail("参数错误，startSiteId不能为空");
        }
        final OperateUser operateUser = qo.getOperateUser();
        if (operateUser == null) {
            return result.toFail("参数错误，operateUser不能为空");
        }
        if (StringUtils.isEmpty(operateUser.getUserCode())) {
            return result.toFail("参数错误，operateUser.userCode不能为空");
        }
        if (StringUtils.isEmpty(operateUser.getUserName())) {
            return result.toFail("参数错误，operateUser.userName不能为空");
        }
        return result;
    }

    private String getValidateGenerateCacheKey(Integer siteId, String validateStr) {
        return String.format(JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR, siteId, validateStr);
    }
    private String getValidateLastGenerateCacheKey(Integer siteId, String userCode) {
        return String.format(JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_LAST_GENERATE_STR, siteId, userCode);
    }
    private String getValidateLastGenerateTimeCacheKey(Integer siteId, String userCode) {
        return String.format(JyCacheKeyConstants.JY_TRANSPORT_SEND_VEHICLE_VALIDATE_LAST_GENERATE_TIME_KEY, siteId, userCode);
    }
}
