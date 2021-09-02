package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
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
import org.springframework.beans.BeanUtils;
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
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public List<LoadScanDto> getLoadScanListByWaybillCode(List<LoadScanDto> scanDtoList, Integer currentSiteId) {
        BaseEntity<List<LoadScanDto>> baseEntity;
        try {
            // 根据包裹号查找运单号
            if (isUseNewInventory(String.valueOf(currentSiteId))) {
                baseEntity = loadScanPackageDetailWs.findLoadScanList(scanDtoList, currentSiteId);
            } else {
                List<com.jd.ql.dms.report.domain.LoadScanDto> loadScanDtoList = new ArrayList<>();
                BeanUtils.copyProperties(scanDtoList, loadScanDtoList);
                com.jd.ql.dms.report.domain.BaseEntity<List<com.jd.ql.dms.report.domain.LoadScanDto>> result
                        = loadScanPackageDetailService.findLoadScanList(loadScanDtoList, currentSiteId);
                baseEntity = new BaseEntity<>();
                BeanUtils.copyProperties(result, baseEntity);
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

    @Override
    public LoadScanDto getLoadScanByWaybillAndPackageCode(LoadScanDto loadScanDto) {
        BaseEntity<LoadScanDto> baseEntity;
        try {
            if (isUseNewInventory(String.valueOf(loadScanDto.getCreateSiteId()))) {
                baseEntity = loadScanPackageDetailWs.findLoadScan(loadScanDto);
            } else {
                com.jd.ql.dms.report.domain.LoadScanDto scanDto = new com.jd.ql.dms.report.domain.LoadScanDto();
                BeanUtils.copyProperties(loadScanDto, scanDto);
                com.jd.ql.dms.report.domain.BaseEntity<com.jd.ql.dms.report.domain.LoadScanDto> result
                        = loadScanPackageDetailService.findLoadScan(scanDto);
                baseEntity = new BaseEntity<>();
                BeanUtils.copyProperties(result, baseEntity);
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

    @Override
    public List<String> getUnloadPackageCodesByWaybillCode(String waybillCode, Integer createSiteId, List<String> packageCodes) {
        BaseEntity<List<String>> baseEntity;
        try {
            if (isUseNewInventory(String.valueOf(createSiteId))) {
                baseEntity = loadScanPackageDetailWs.findUnloadPackageCodes(waybillCode, createSiteId, packageCodes);
            } else {
                com.jd.ql.dms.report.domain.BaseEntity<List<String>> result
                        = loadScanPackageDetailService.findUnloadPackageCodes(waybillCode, createSiteId, packageCodes);
                baseEntity = new BaseEntity<>();
                BeanUtils.copyProperties(result, baseEntity);
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

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo",mState = {JProEnum.TP, JProEnum.FunctionError})
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
                com.jd.ql.dms.report.domain.LoadScanReqDto scanReqDto = new com.jd.ql.dms.report.domain.LoadScanReqDto();
                BeanUtils.copyProperties(loadScanReqDto, scanReqDto);
                com.jd.ql.dms.report.domain.BaseEntity<List<com.jd.ql.dms.report.domain.LoadScanDto>> result
                        = loadScanPackageDetailService.getWaitLoadWaybillInfo(scanReqDto);
                jsfRes = new BaseEntity<>();
                BeanUtils.copyProperties(result, jsfRes);
            }
            if(jsfRes == null) {
                logger.error("LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo--error--装车任务查询待装运单信息失败，参数loadScanReqDto=【{}】", JsonHelper.toJson(loadScanReqDto));
                res.toError("查询库存运单信息失败");
                return res;
            }else if(jsfRes.getCode() != BaseEntity.CODE_SUCCESS) {
                logger.error("LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo--fail--装车任务查询待装运单信息失败，参数loadScanReqDto=【{}】", JsonHelper.toJson(loadScanReqDto));
                res.toFail(jsfRes.getMessage());
                return res;
            }
            res.setData(jsfRes.getData());
            res.toSucceed();
            return res;

        }catch (Exception e) {
            logger.error("LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo--调用分拣报表查询已验未发jsf异常--，参数=【{}】", JsonHelper.toJson(loadScanReqDto), e);
            res.toFail("JSF调用失败");
            return res;
        }
    }

    private boolean isUseNewInventory(String createSiteCode) {
        String siteCodes = uccPropertyConfiguration.getUseNewInventorySiteCodes();
        if (StringUtils.isBlank(siteCodes)) {
            return false;
        }
        List<String> siteList = Arrays.asList(siteCodes.split(Constants.SEPARATOR_COMMA));
        return siteList.contains(createSiteCode) || siteList.contains("true");
    }

}
