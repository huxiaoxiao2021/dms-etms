package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.goodsLoadScan.service.DmsDisSendService;
import com.jd.ql.dms.report.LoadScanPackageDetailService;
import com.jd.ql.dms.report.domain.LoadScanDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("dmsDisSendService")
public class DmsDisSendServiceImpl implements DmsDisSendService {

    private final Logger logger = LoggerFactory.getLogger(DmsDisSendServiceImpl.class);

    @Resource
    LoadScanPackageDetailService loadScanPackageDetailService;

    @Override
    public List<LoadScanDto> getLoadScanListByWaybillCode(List<LoadScanDto> scanDtoList, Integer currentSiteId) {
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity;
        try {
            // 根据包裹号查找运单号
            baseEntity = loadScanPackageDetailService
                    .findLoadScanList(scanDtoList, currentSiteId);
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
    public List<LoadScanDto> getLoadScanByWaybillCodes(List<String> waybillCodes, Integer currentSiteId, Integer nextSiteId, Integer rows) {
        com.jd.ql.dms.report.domain.BaseEntity<List<LoadScanDto>> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService
                    .findLoadScanPackageDetail(waybillCodes, currentSiteId, nextSiteId, rows);
        } catch (Exception e) {
            logger.error("根据暂存表记录去ES查询运单明细接口发生异常，currentSiteId={},currentSiteId={}",
                    currentSiteId, nextSiteId);
            return new ArrayList<>();
        }

        if (baseEntity == null) {
            logger.warn("根据暂存表记录去ES查询运单明细接口返回空，currentSiteId={},nextSiteId={}", currentSiteId, nextSiteId);
            return new ArrayList<>();
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            logger.error("根据暂存表记录去ES查询运单明细接口失败，currentSiteId={},currentSiteId={},code={}",
                    currentSiteId, nextSiteId, baseEntity.getCode());
            return new ArrayList<>();
        }
        return baseEntity.getData();
    }

    @Override
    public LoadScanDto getLoadScanByWaybillAndPackageCode(LoadScanDto loadScanDto) {
        com.jd.ql.dms.report.domain.BaseEntity<LoadScanDto> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService.findLoadScan(loadScanDto);
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
        com.jd.ql.dms.report.domain.BaseEntity<List<String>> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService.findUnloadPackageCodes(waybillCode, createSiteId, packageCodes);
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

    @Override
    public LoadScanDto getPackageCodesByWaybillCode(String waybillCode, Integer createSiteId) {
        com.jd.ql.dms.report.domain.BaseEntity<LoadScanDto> baseEntity;
        try {
            baseEntity = loadScanPackageDetailService.findPackageCodesByWaybillCode(waybillCode, createSiteId);
        } catch (Exception e) {
            logger.error("根据运单号去ES查询库存包裹号列表发生异常，waybillCode={},createSiteId={},e=",
                    waybillCode, createSiteId, e);
            return null;
        }

        if (baseEntity == null) {
            logger.warn("根据运单号去ES查询库存包裹号列表返回空，waybillCode={},createSiteId={}",
                    waybillCode, createSiteId);
            return null;
        }
        if (!Constants.SUCCESS_CODE.equals(baseEntity.getCode()) || baseEntity.getData() == null) {
            logger.error("根据运单号去ES查询库存包裹号列表失败，waybillCode={},createSiteId={},code={}",
                    waybillCode, createSiteId, baseEntity.getCode());
            return null;
        }
        return baseEntity.getData();
    }
}
