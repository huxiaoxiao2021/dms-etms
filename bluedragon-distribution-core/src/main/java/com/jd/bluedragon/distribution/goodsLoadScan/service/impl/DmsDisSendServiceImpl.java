package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.service.DmsDisSendService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.merchant.api.common.dto.BaseEntity;
import com.jd.merchant.api.pack.dto.LoadScanDto;
import com.jd.merchant.api.pack.dto.LoadScanReqDto;
import com.jd.merchant.api.pack.ws.LoadScanPackageDetailWS;
import com.jd.ql.dms.report.LoadScanPackageDetailService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("dmsDisSendService")
public class DmsDisSendServiceImpl implements DmsDisSendService {

    private final Logger logger = LoggerFactory.getLogger(DmsDisSendServiceImpl.class);

    @Resource
    private LoadScanPackageDetailWS loadScanPackageDetailWs;

    @Resource
    private LoadScanPackageDetailService loadScanPackageDetailService;

    @Resource
    private DmsConfigManager dmsConfigManager;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.DmsDisSendServiceImpl.getLoadScanListByWaybillCode",mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<LoadScanDto> getLoadScanListByWaybillCode(List<LoadScanDto> scanDtoList, Integer currentSiteId) {
        BaseEntity<List<LoadScanDto>> baseEntity;
        try {
            // 根据包裹号查找运单号
            if (isUseNewInventory(String.valueOf(currentSiteId))) {
                logger.info("DmsDisSendServiceImpl走新es");
                baseEntity = loadScanPackageDetailWs.findLoadScanList(scanDtoList, currentSiteId);
            } else {
                logger.info("DmsDisSendServiceImpl走旧es");
                String jsonParam = JsonHelper.toJson(scanDtoList);
                List<com.jd.ql.dms.report.domain.LoadScanDto> loadScanDtoList
                        = JsonHelper.jsonToList(jsonParam, com.jd.ql.dms.report.domain.LoadScanDto.class);
                com.jd.ql.dms.report.domain.BaseEntity<List<com.jd.ql.dms.report.domain.LoadScanDto>> result
                        = loadScanPackageDetailService.findLoadScanList(loadScanDtoList, currentSiteId);
                String jsonResult = JsonHelper.toJson(result);
                baseEntity = JsonHelper.fromJsonUseGson(jsonResult, new TypeToken<BaseEntity<List<LoadScanDto>>>(){}.getType());
            }
        } catch (Exception e) {
            logger.error("根据运单号列表去ES查询运单明细接口发生异常，currentSiteId={},e=", currentSiteId,  e);
            return new ArrayList<>();
        }
        if (baseEntity == null) {
            logger.warn("根据运单号列表去ES查询运单明细接口返回空currentSiteId={}", currentSiteId);
            return new ArrayList<>();
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            logger.error("根据运单号列表去ES查询运单明细接口失败currentSiteId={}", currentSiteId);
            return new ArrayList<>();
        }
        return baseEntity.getData();
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.DmsDisSendServiceImpl.getLoadScanByWaybillAndPackageCode",mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public LoadScanDto getLoadScanByWaybillAndPackageCode(LoadScanDto loadScanDto) {
        BaseEntity<LoadScanDto> baseEntity;
        try {
            if (isUseNewInventory(String.valueOf(loadScanDto.getCreateSiteId()))) {
                baseEntity = loadScanPackageDetailWs.findLoadScan(loadScanDto);
            } else {
                String jsonParam = JsonHelper.toJson(loadScanDto);
                com.jd.ql.dms.report.domain.LoadScanDto scanDto = JsonHelper.fromJson(jsonParam, com.jd.ql.dms.report.domain.LoadScanDto.class);
                com.jd.ql.dms.report.domain.BaseEntity<com.jd.ql.dms.report.domain.LoadScanDto> result
                        = loadScanPackageDetailService.findLoadScan(scanDto);
                String jsonResult = JsonHelper.toJson(result);
                baseEntity = JsonHelper.fromJsonUseGson(jsonResult, new TypeToken<BaseEntity<LoadScanDto>>(){}.getType());
            }
        } catch (Exception e) {
            logger.error("根据包裹号和运单号去ES查询包裹流向发生异常，packageCode={},waybillCode={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode());
            return null;
        }

        if (baseEntity == null) {
            logger.warn("根据运单号和包裹号去ES查询流向返回空，packageCode={},waybillCode={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode());
            return null;
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            logger.error("根据运单号和包裹号去ES查询流向接口失败，packageCode={},waybillCode={},code={}",
                    loadScanDto.getPackageCode(), loadScanDto.getWayBillCode(), baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.DmsDisSendServiceImpl.getUnloadPackageCodesByWaybillCode",mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<String> getUnloadPackageCodesByWaybillCode(String waybillCode, Integer createSiteId, List<String> packageCodes) {
        BaseEntity<List<String>> baseEntity;
        try {
            if (isUseNewInventory(String.valueOf(createSiteId))) {
                baseEntity = loadScanPackageDetailWs.findUnloadPackageCodes(waybillCode, createSiteId, packageCodes);
            } else {
                com.jd.ql.dms.report.domain.BaseEntity<List<String>> result
                        = loadScanPackageDetailService.findUnloadPackageCodes(waybillCode, createSiteId, packageCodes);
                String jsonResult = JsonHelper.toJson(result);
                baseEntity = JsonHelper.fromJsonUseGson(jsonResult, new TypeToken<BaseEntity<List<String>>>(){}.getType());
            }
        } catch (Exception e) {
            logger.error("根据已装包裹号列表和运单号去ES查询未装包裹号列表发生异常，waybillCode={},createSiteId={},e=",
                    waybillCode, createSiteId, e);
            return new ArrayList<>();
        }

        if (baseEntity == null) {
            logger.warn("根据已装包裹号列表和运单号去ES查询未装包裹号列表返回空，waybillCode={},createSiteId={}",
                    waybillCode, createSiteId);
            return new ArrayList<>();
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            logger.error("根据已装包裹号列表和运单号去ES查询未装包裹号列表失败，waybillCode={},createSiteId={},code={}",
                    waybillCode, createSiteId, baseEntity.getCode());
            return new ArrayList<>();
        }
        return baseEntity.getData();
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.DmsDisSendServiceImpl.getInspectNoSendWaybillInfo",mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<LoadScanDto>> getInspectNoSendWaybillInfo(LoadCar loadCar, List<String> waybillCodeList) {
        JdCResponse<List<LoadScanDto>> res = new JdCResponse<>();
        LoadScanReqDto loadScanReqDto = new LoadScanReqDto();
        try {
            loadScanReqDto.setCreateSiteId(loadCar.getCreateSiteCode().intValue());
            loadScanReqDto.setNextSiteId(loadCar.getEndSiteCode().intValue());
            Date fromTime = DateHelper.newTimeRangeHoursAgo(new Date(), GoodsLoadScanConstants.WAIT_LOAD_RANGE_FROM_HOURS);
            loadScanReqDto.setFormTime(fromTime.getTime());
            loadScanReqDto.setToTime(System.currentTimeMillis());
            loadScanReqDto.setLoadWaybillCodeList(waybillCodeList);
            BaseEntity<List<LoadScanDto>> jsfRes;
            if (isUseNewInventory(String.valueOf(loadCar.getCreateSiteCode()))) {
                jsfRes = loadScanPackageDetailWs.getWaitLoadWaybillInfo(loadScanReqDto);
            } else {
                String jsonParam = JsonHelper.toJson(loadScanReqDto);
                com.jd.ql.dms.report.domain.LoadScanReqDto scanReqDto = JsonHelper.fromJson(jsonParam, com.jd.ql.dms.report.domain.LoadScanReqDto.class);
                com.jd.ql.dms.report.domain.BaseEntity<List<com.jd.ql.dms.report.domain.LoadScanDto>> result
                        = loadScanPackageDetailService.getWaitLoadWaybillInfo(scanReqDto);
                String jsonResult = JsonHelper.toJson(result);
                jsfRes = JsonHelper.fromJsonUseGson(jsonResult, new TypeToken<BaseEntity<List<LoadScanDto>>>(){}.getType());
            }
            if(jsfRes == null) {
                logger.error("DmsDisSendServiceImpl.getInspectNoSendWaybillInfo--error--装车任务查询待装运单信息失败，参数loadScanReqDto=【{}】", JsonHelper.toJson(loadScanReqDto));
                res.toError("查询库存运单信息失败");
                return res;
            }else if(jsfRes.getCode() != BaseEntity.CODE_SUCCESS) {
                logger.error("DmsDisSendServiceImpl.getInspectNoSendWaybillInfo--fail--装车任务查询待装运单信息失败，参数loadScanReqDto=【{}】", JsonHelper.toJson(loadScanReqDto));
                res.toFail(jsfRes.getMessage());
                return res;
            }
            res.setData(jsfRes.getData());
            res.toSucceed();
            return res;

        }catch (Exception e) {
            logger.error("DmsDisSendServiceImpl.getInspectNoSendWaybillInfo--调用分拣报表查询已验未发jsf异常--，参数=【{}】", JsonHelper.toJson(loadScanReqDto), e);
            res.toFail("JSF调用失败");
            return res;
        }
    }

    private boolean isUseNewInventory(String createSiteCode) {
        String siteCodes = dmsConfigManager.getPropertyConfig().getUseNewInventorySiteCodes();
        logger.info("DmsDisSendServiceImpl判断走哪个es：siteCodes={}", siteCodes);
        if (StringUtils.isBlank(siteCodes)) {
            return false;
        }
        List<String> siteList = Arrays.asList(siteCodes.split(Constants.SEPARATOR_COMMA));
        return siteList.contains(createSiteCode) || siteList.contains("true");
    }

}
