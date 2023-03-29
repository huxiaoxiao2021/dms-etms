package com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageNotScanItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.QueryUnScanDiscardedPackagePo;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.QueryUnSubmitDiscardedListPo;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.SubmitDiscardedPackagePo;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.response.base.ResultCodeConstant;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedWaybillStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.*;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageSiteDepartTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageStorageTempStatusEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteOperateTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteWaybillTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler.DiscardedStorageHandlerStrategy;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.DiscardedPackageStorageTempService;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.vo.DiscardedPackageStorageTempVo;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 快递弃件暂存
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-31 14:45:51 周三
 */
@Service("discardedPackageStorageTempService")
public class DiscardedPackageStorageTempServiceImpl implements DiscardedPackageStorageTempService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscardedPackageStorageTempDao discardedPackageStorageTempDao;

    @Autowired
    private DiscardedWaybillStorageTempDao discardedWaybillStorageTempDao;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private DiscardedStorageHandlerStrategy discardedStorageHandlerStrategy;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Resource
    private CacheService jimdbCacheService;

    private final int maxScanSize = 100;

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.selectCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Response<Long> selectCount(DiscardedPackageStorageTempQo query) {
        log.info("DiscardedPackageStorageTempServiceImpl.selectCount param {}", JsonHelper.toJson(query));
        Response<Long> result = new Response<>();
        result.toSucceed();
        try {
            query.setYn(Constants.YN_YES);
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            long total = discardedPackageStorageTempDao.selectCount(query);
            result.setData(total);
        } catch (Exception e) {
            log.error("DiscardedPackageStorageTempServiceImpl.selectCount exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.selectList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<DiscardedPackageStorageTempVo>> selectList(DiscardedPackageStorageTempQo query) {
        log.info("DiscardedPackageStorageTempServiceImpl.selectList param {}", JsonHelper.toJson(query));
        Response<List<DiscardedPackageStorageTempVo>> result = new Response<>();
        List<DiscardedPackageStorageTempVo> dataList = new ArrayList<>();
        result.setData(dataList);
        result.toSucceed();
        try {
            query.setYn(Constants.YN_YES);
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            final List<DiscardedPackageStorageTempVo> discardedPackageStorageTempVos = this.queryVoList(query);
            if(CollectionUtils.isNotEmpty(discardedPackageStorageTempVos)){
                dataList.addAll(discardedPackageStorageTempVos);
            }
        } catch (Exception e) {
            log.error("DiscardedPackageStorageTempServiceImpl.selectList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    private Response<Void> checkAndSetPram4SelectParam(DiscardedPackageStorageTempQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        if(StringUtils.isBlank(query.getWaybillCode()) && StringUtils.isBlank(query.getPackageCode())){
            if((query.getStorageDaysFrom() == null || query.getStorageDaysTo() == null) &&
                    (StringUtils.isBlank(query.getCreateTimeFromStr()) || StringUtils.isBlank(query.getCreateTimeToStr()))){
                result.toError("参数错误，waybillCode和packageCode为空时，首次扫描时间和存储天数不能同时为空");
                return result;
            }
        }
        if(!StringUtils.isBlank(query.getCreateTimeFromStr())){
            query.setCreateTimeFrom(DateHelper.parseDate(query.getCreateTimeFromStr(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        }
        if(!StringUtils.isBlank(query.getCreateTimeToStr())){
            query.setCreateTimeTo(DateHelper.parseDate(query.getCreateTimeToStr(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        }
        if(query.getStorageDaysFrom() != null){
            try {
                Date currentDateMorning = DateUtil.parseDateByStr(DateUtil.format(new Date(), DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);
                query.setScanTimeTo(DateHelper.addDate(currentDateMorning, -query.getStorageDaysFrom()));
            } catch (Exception e) {
                log.error("DiscardedPackageStorageTempServiceImpl.checkAndSetPram4SelectParam exception ", e);
                result.toError("计算已存储天数异常");
                return result;
            }
        }

        if(query.getStorageDaysTo() != null){
            try {
                Date currentDateMorning = DateUtil.parseDateByStr(DateUtil.format(new Date(), DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);
                query.setScanTimeFrom(DateHelper.addDate(currentDateMorning, -query.getStorageDaysTo() - 1));
            } catch (Exception e) {
                log.error("DiscardedPackageStorageTempServiceImpl.checkAndSetPram4SelectParam exception ", e);
                result.toError("计算已存储天数异常");
                return result;
            }
        }
        if(query.getStorageDaysFrom() != null && query.getStorageDaysTo() != null && (query.getStorageDaysTo() - query.getStorageDaysFrom() > 7)){
            result.toError("已存储天数查询范围不能超过7天");
            return result;
        }

        return result;

    }

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.selectPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<PageDto<DiscardedPackageStorageTempVo>> selectPageList(DiscardedPackageStorageTempQo query) {
        log.info("DiscardedPackageStorageTempServiceImpl.selectPageList param {}", JsonHelper.toJson(query));
        Response<PageDto<DiscardedPackageStorageTempVo>> result = new Response<>();
        result.toSucceed();
        PageDto<DiscardedPackageStorageTempVo> pageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
        List<DiscardedPackageStorageTempVo> dataList = new ArrayList<>();
        try {
            query.setYn(Constants.YN_YES);
            Response<Void> checkResult = this.checkPram4SelectPageList(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            long total = discardedPackageStorageTempDao.selectCount(query);
            pageDto.setTotalRow(new Long(total).intValue());
            if (total > 0) {
                final List<DiscardedPackageStorageTempVo> discardedPackageStorageTempVos = this.queryVoList(query);
                if(CollectionUtils.isNotEmpty(discardedPackageStorageTempVos)){
                    dataList.addAll(discardedPackageStorageTempVos);
                }
            }
            pageDto.setResult(dataList);
        } catch (Exception e) {
            log.error("DiscardedPackageStorageTempServiceImpl.selectPageList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        result.setData(pageDto);
        return result;
    }

    private Response<Void> checkPram4SelectPageList(DiscardedPackageStorageTempQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        if(query.getPageNumber() <= 0){
            result.toError("参数错误，pageNumber必须大于0");
            return result;
        }
        if(query.getPageSize() == null){
            result.toError("参数错误，pageSize不能为空");
            return result;
        }
        return result;

    }

    private List<DiscardedPackageStorageTempVo> queryVoList(DiscardedPackageStorageTempQo query) {
        List<DiscardedPackageStorageTempVo> dataList = new ArrayList<>();
        List<DiscardedPackageStorageTemp> rawDataList = discardedPackageStorageTempDao.selectList(query);
        // 根据去重运单号数据，再去查运单主表
        Set<String> waybillCodeSet = new HashSet<>();
        for (DiscardedPackageStorageTemp discardedPackageStorageTemp : rawDataList) {
            waybillCodeSet.add(discardedPackageStorageTemp.getWaybillCode());
            DiscardedPackageStorageTempVo vo = new DiscardedPackageStorageTempVo();
            BeanUtils.copyProperties(discardedPackageStorageTemp, vo);
            dataList.add(vo);
        }
        DiscardedPackageStorageTempQo queryWaybill = new DiscardedPackageStorageTempQo();
        BeanUtils.copyProperties(query, queryWaybill);
        queryWaybill.setPageNumber(0);
        queryWaybill.setWaybillCodeList(new ArrayList<>(waybillCodeSet));
        final List<DiscardedWaybillStorageTemp> discardedWaybillList = discardedWaybillStorageTempDao.selectList(queryWaybill);
        Map<String, DiscardedWaybillStorageTemp> discardedWaybillStorageTempGBWaybillCodeMap = new HashMap<>();
        for (DiscardedWaybillStorageTemp discardedWaybillStorageTemp : discardedWaybillList) {
            discardedWaybillStorageTempGBWaybillCodeMap.put(discardedWaybillStorageTemp.getWaybillCode(), discardedWaybillStorageTemp);
        }
        Map<Integer, String> discardedPackageStorageTempStatusEnumMap = DiscardedPackageStorageTempStatusEnum.ENUM_MAP;
        Date dateNow = new Date();
        for (DiscardedPackageStorageTempVo vo : dataList) {
            final DiscardedWaybillStorageTemp discardedWaybillStorageTemp = discardedWaybillStorageTempGBWaybillCodeMap.get(vo.getWaybillCode());
            if(discardedWaybillStorageTemp != null){
                vo.setPackageScanTotal(discardedWaybillStorageTemp.getPackageScanTotal());
                vo.setPackageSysTotal(discardedWaybillStorageTemp.getPackageSysTotal());
            }
            
            vo.setFirstScanTimeFormative(DateUtil.formatDateTime(vo.getCreateTime()));
            vo.setLastOperateTimeFormative(DateUtil.formatDateTime(vo.getUpdateTime()));
            vo.setSiteDepartTypeDesc(DiscardedPackageSiteDepartTypeEnum.getEnumNameByCode(vo.getSiteDepartType()));
            String statusStr = discardedPackageStorageTempStatusEnumMap.get(vo.getStatus());
            vo.setStatusStr(statusStr != null ? statusStr : DiscardedPackageStorageTempStatusEnum.UNKNOW.getName());
            vo.setIsCodStr(Objects.equals(vo.getCod(), Constants.YN_YES) ? "是" : "否");
            // 计算已存储天数
            vo.setStorageDays(DateHelper.daysDiff(vo.getCreateTime(), dateNow));
            vo.setOperateTypeDesc(WasteOperateTypeEnum.getNameByCode(vo.getOperateType()));
            vo.setWaybillTypeDesc(WasteWaybillTypeEnum.getNameByCode(vo.getWaybillType()));
        }
        return dataList;
    }

    /**
     * 查询未提交已扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.queryUnSubmitDiscardedList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<DiscardedWaybillScanResultItemDto>> queryUnSubmitDiscardedList(QueryUnSubmitDiscardedListPo paramObj) {
        if(log.isInfoEnabled()){
            log.info("DiscardedPackageStorageTempServiceImpl.queryUnSubmitDiscardedList param: {}", JsonHelper.toJson(paramObj));
        }
        Result<List<DiscardedWaybillScanResultItemDto>> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4QueryUnSubmitDiscardedPackage(paramObj);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 3. 执行业务逻辑
            final List<DiscardedWaybillScanResultItemDto> discardedPackageScanResultItemDtoList = this.selectUnSubmitDiscardedWaybillList(this.genUnSubmitDiscardedListQo(paramObj));
            result.setData(discardedPackageScanResultItemDtoList);

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DiscardedPackageStorageTempServiceImpl.queryUnSubmitDiscardedList exception param {} exception {}", JsonHelper.toJson(paramObj), e.getMessage(), e);
        }
        return result;
    }

    private List<DiscardedWaybillScanResultItemDto> selectUnSubmitDiscardedWaybillList(UnSubmitDiscardedListQo unSubmitDiscardedListQo){
        final List<DiscardedWaybillScanResultItemDto> discardedPackageScanResultItemDtoList = discardedWaybillStorageTempDao.selectUnSubmitDiscardedWaybillList(unSubmitDiscardedListQo);
        for (DiscardedWaybillScanResultItemDto dto : discardedPackageScanResultItemDtoList) {
            dto.setPackageNotScanTotal(dto.getPackageSysTotal() - dto.getPackageScanTotal());
        }
        // 再按未扫个数降序
        Collections.sort(discardedPackageScanResultItemDtoList, new Comparator<DiscardedWaybillScanResultItemDto>() {
            @Override
            public int compare(DiscardedWaybillScanResultItemDto v1, DiscardedWaybillScanResultItemDto v2) {
                return v2.getPackageNotScanTotal().compareTo(v1.getPackageNotScanTotal());
            }
        });
        return discardedPackageScanResultItemDtoList;
    }

    private UnSubmitDiscardedListQo genUnSubmitDiscardedListQo(QueryUnSubmitDiscardedListPo paramObj){
        final UnSubmitDiscardedListQo unSubmitDiscardedPackageQo = new UnSubmitDiscardedListQo();
        unSubmitDiscardedPackageQo.setOperatorErp(paramObj.getOperateUser().getUserCode());
        unSubmitDiscardedPackageQo.setUnSubmitStatus(Constants.YN_NO);
        unSubmitDiscardedPackageQo.setWaybillType(paramObj.getWaybillType());
        unSubmitDiscardedPackageQo.setPageSize(maxScanSize);
        unSubmitDiscardedPackageQo.setPageNumber(1);
        return unSubmitDiscardedPackageQo;
    }

    private UnSubmitDiscardedListQo genUnSubmitDiscardedListQo(ScanDiscardedPackagePo paramObj){
        final UnSubmitDiscardedListQo unSubmitDiscardedPackageQo = new UnSubmitDiscardedListQo();
        unSubmitDiscardedPackageQo.setOperatorErp(paramObj.getOperateUser().getUserCode());
        unSubmitDiscardedPackageQo.setUnSubmitStatus(Constants.YN_NO);
        unSubmitDiscardedPackageQo.setWaybillType(paramObj.getWaybillType());
        unSubmitDiscardedPackageQo.setPageSize(maxScanSize);
        unSubmitDiscardedPackageQo.setPageNumber(1);
        return unSubmitDiscardedPackageQo;
    }

    private Result<Void> checkParam4QueryUnSubmitDiscardedPackage(QueryUnSubmitDiscardedListPo paramObj){
        Result<Void> result = Result.success();

        if(paramObj == null){
            log.warn("checkParam4ScanDiscardedPackage 参数不能为空");
            return result.toFail("弃件暂存请求参数不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getOperateUser() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，operateUser不能为空");
            return result.toFail("参数错误，operateUser不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        /*if(paramObj.getSiteDepartType() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，siteDepartType不能为空");
            return result.toFail("参数错误，siteDepartType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getWaybillType() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，waybillType不能为空");
            return result.toFail("参数错误，waybillType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }*/

        return result;
    }

    /**
     * 弃件暂存扫描
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:43 周四
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.scanDiscardedPackage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<DiscardedWaybillScanResultItemDto>> scanDiscardedPackage(ScanDiscardedPackagePo paramObj) {
        if(log.isInfoEnabled()){
            log.info("DiscardedPackageStorageTempServiceImpl.scanDiscardedPackage param: {}", JsonHelper.toJson(paramObj));
        }
        Result<List<DiscardedWaybillScanResultItemDto>> result = Result.success();

        String keyTemplate = CacheKeyConstants.DISCARDED_STORAGE_OPERATE_SCAN;
        String key = String.format(keyTemplate, WaybillUtil.getWaybillCode(paramObj.getBarCode()));
        try{
            boolean getLock = jimdbCacheService.setNx(key, 1 + "", CacheKeyConstants.DISCARDED_STORAGE_OPERATE_SCAN_TIMEOUT, TimeUnit.SECONDS);
            if(!getLock){
                return result.toFail("操作太快，正在处理中", ResultCodeConstant.CONCURRENT_CONFLICT);
            }
            // 1. 检验参数
            final Result<Void> checkResult = this.checkParam4ScanDiscardedPackage(paramObj);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 校验业务条件
            final Result<Void> checkBusinessResult = this.checkBusinessParam4ScanDiscardedPackage(paramObj);
            if (!checkBusinessResult.isSuccess()) {
                return result.toFail(checkBusinessResult.getMessage(), checkBusinessResult.getCode());
            }

            BaseStaffSiteOrgDto siteDto = baseMajorManager.getBaseSiteBySiteId(paramObj.getOperateUser().getSiteCode());
            if (siteDto == null) {
                log.warn("scanDiscardedPackage，没有查询到操作站点信息 param: {}", JsonHelper.toJson(paramObj));
                return result.toFail("没有查询到操作站点信息");
            }

            String barCode = paramObj.getBarCode();
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
            if (Objects.equals(DiscardedPackageSiteDepartTypeEnum.SORTING.getCode(), paramObj.getSiteDepartType())) {
                if (Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), paramObj.getOperateType())) {
                    if (!waybillTraceManager.isWaybillWaste(waybillCode)) {
                        log.warn("scanDiscardedPackage，不是弃件，请勿操作弃件暂存 param: {}", JsonHelper.toJson(paramObj));
                        return result.toFail("不是弃件，请勿操作弃件暂存");
                    }
                }
            }
            if (Objects.equals(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode(), paramObj.getSiteDepartType())) {
                if (Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), paramObj.getOperateType())) {
                    if (!waybillTraceManager.isOpCodeWaste(barCode)) {
                        log.warn("scanDiscardedPackage，不是弃件，请勿操作弃件暂存 param: {}", JsonHelper.toJson(paramObj));
                        return result.toFail("不是弃件，请勿操作弃件暂存");
                    }
                }
            }

            WChoice choice = new WChoice();
            choice.setQueryWaybillC(true);
            choice.setQueryPackList(true);
            choice.setQueryGoodList(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
            log.info("查询到运单信息。运单号：{}。返回结果：{}", waybillCode, com.jd.bluedragon.utils.JsonHelper.toJson(baseEntity));
            if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()) {
                return result.toFail("查询到运单信息失败:" + baseEntity.getMessage());
            }
            final BigWaybillDto bigWaybillDto = baseEntity.getData();
            if (bigWaybillDto == null || bigWaybillDto.getWaybill() == null) {
                return result.toFail("没有查询到运单信息");
            }
            if (CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
                return result.toFail("没有查询到运单包裹信息");
            }

            if (Objects.equals(WasteOperateTypeEnum.SCRAP.getCode(), paramObj.getOperateType())) {
                String waybillSign = baseEntity.getData().getWaybill().getWaybillSign();
                if(!BusinessUtil.isScrapSortingSite(waybillSign)) {
                    return result.toFail("提交失败，非返分拣报废运单！");
                }
            }

            // 3. 执行业务操作逻辑
            final DiscardedStorageContext context = new DiscardedStorageContext();
            context.setScanDiscardedPackagePo(paramObj);
            context.setCurrentSiteInfo(siteDto);
            context.setBigWaybillDto(bigWaybillDto);
            context.setWaybillCode(waybillCode);
            final Result<Boolean> handleResult = discardedStorageHandlerStrategy.handle(context);
            if(!handleResult.isSuccess()){
                return result.toFail(handleResult.getMessage(), handleResult.getCode());
            }
            // 查询扫描后未提交的数据
            final List<DiscardedWaybillScanResultItemDto> discardedPackageScanResultItemDtoList = this.selectUnSubmitDiscardedWaybillList(this.genUnSubmitDiscardedListQo(paramObj));
            result.setData(discardedPackageScanResultItemDtoList);

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DiscardedPackageStorageTempServiceImpl.scanDiscardedPackage exception param {} exception {}", JsonHelper.toJson(paramObj), e.getMessage(), e);
        } finally {
            jimdbCacheService.del(key);
        }
        return result;
    }

    private Result<Void> checkParam4ScanDiscardedPackage(ScanDiscardedPackagePo paramObj){
        Result<Void> result = Result.success();

        if(paramObj == null){
            log.warn("checkParam4ScanDiscardedPackage 参数不能为空");
            return result.toFail("弃件暂存请求参数不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(StringUtils.isBlank(paramObj.getBarCode())){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，barCode不能为空");
            return result.toFail("参数错误，barCode不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getOperateUser() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，operateUser不能为空");
            return result.toFail("参数错误，operateUser不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getSiteDepartType() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，siteDepartType不能为空");
            return result.toFail("参数错误，siteDepartType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getOperateType() == null){
            // log.warn("checkParam4ScanDiscardedPackage，参数错误，operateType不能为空");
            paramObj.setOperateType(WasteOperateTypeEnum.STORAGE.getCode());
            // return result.toFail("参数错误，operateType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        final List<Integer> wasteOperateTypeList = WasteOperateTypeEnum.ENUM_LIST;
        if (!wasteOperateTypeList.contains(paramObj.getOperateType())) {
            log.warn("checkBusinessParam4ScanDiscardedPackage，参数错误，operateType不正确 param: {}", JsonHelper.toJson(paramObj));
            return result.toFail("参数错误，operateType不正确", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if (Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), paramObj.getOperateType())) {
            if(paramObj.getWaybillType() == null){
                // log.warn("checkParam4ScanDiscardedPackage，参数错误，waybillType不能为空");
                paramObj.setWaybillType(WasteWaybillTypeEnum.PACKAGE.getCode());
                // return result.toFail("参数错误，waybillType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            }
            final List<Integer> wasteWaybillTypeList = WasteWaybillTypeEnum.ENUM_LIST;
            if (!wasteWaybillTypeList.contains(paramObj.getWaybillType())) {
                log.warn("checkBusinessParam4ScanDiscardedPackage，参数错误，waybillType不正确 param: {}", JsonHelper.toJson(paramObj));
                return result.toFail("参数错误，waybillType不正确", ResultCodeConstant.ILLEGAL_ARGUMENT);
            }
        }
        if(paramObj.getOperateTime() == null){
            paramObj.setOperateTime(System.currentTimeMillis());
        }

        return result;
    }

    private Result<Void> checkBusinessParam4ScanDiscardedPackage(ScanDiscardedPackagePo paramObj){
        Result<Void> result = Result.success();
        String barCode = paramObj.getBarCode();
        final List<Integer> discardedPackageSiteDepartTypeList = DiscardedPackageSiteDepartTypeEnum.ENUM_LIST;
        if (!discardedPackageSiteDepartTypeList.contains(paramObj.getSiteDepartType())) {
            log.warn("checkBusinessParam4ScanDiscardedPackage，参数错误，siteDepartType不正确 param: {}", JsonHelper.toJson(paramObj));
            return result.toFail("参数错误，siteDepartType不正确", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        // 分拣 | 转运； 弃件暂存 | 弃件废弃；分拣的弃件只能扫运单号，转运的弃件只能扫包裹号；分拣的弃件废弃只能扫包裹号
        if(Objects.equals(DiscardedPackageSiteDepartTypeEnum.SORTING.getCode(), paramObj.getSiteDepartType())){
            if(Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), paramObj.getOperateType()) && !WaybillUtil.isWaybillCode(barCode)){
                log.warn("checkBusinessParam4ScanDiscardedPackage，参数错误，请扫描格式正确的运单号 param: {}", JsonHelper.toJson(paramObj));
                return result.toFail("参数错误，请扫描格式正确的运单号", ResultCodeConstant.ILLEGAL_ARGUMENT);
            }
            if(Objects.equals(WasteOperateTypeEnum.SCRAP.getCode(), paramObj.getOperateType()) && !WaybillUtil.isPackageCode(barCode)){
                log.warn("checkBusinessParam4ScanDiscardedPackage，参数错误，请扫描格式正确的包裹号 param: {}", JsonHelper.toJson(paramObj));
                return result.toFail("参数错误，请扫描格式正确的包裹号", ResultCodeConstant.ILLEGAL_ARGUMENT);
            }
        }
        if(Objects.equals(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode(), paramObj.getSiteDepartType())){
            if(Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), paramObj.getOperateType()) && !WaybillUtil.isPackageCode(barCode)){
                log.warn("checkBusinessParam4ScanDiscardedPackage，参数错误，请扫描格式正确的包裹号 param: {}", JsonHelper.toJson(paramObj));
                return result.toFail("参数错误，请扫描格式正确的包裹号", ResultCodeConstant.ILLEGAL_ARGUMENT);
            }
        }
        // 校验包裹号是否存在
        if(WaybillUtil.isPackageCode(paramObj.getBarCode())){
            final DeliveryPackageD packageInfo = waybillPackageManager.getPackageInfoByPackageCode(paramObj.getBarCode());
            if(packageInfo == null){
                log.warn("checkBusinessParam4ScanDiscardedPackage，包裹号不存在 param: {}", JsonHelper.toJson(paramObj));
                result.toFail("包裹号不存在！");
                return result;
            }
        }
        // 已有其他类型的操作，则不允许此类型操作
        DiscardedPackageStorageTempQo discardedPackageStorageTempQo = new DiscardedPackageStorageTempQo();
        discardedPackageStorageTempQo.setWaybillCode(WaybillUtil.getWaybillCode(paramObj.getBarCode()));
        discardedPackageStorageTempQo.setYn(Constants.YN_YES);
        final DiscardedPackageStorageTemp discardedPackageStorageTempExist = discardedPackageStorageTempDao.selectOne(discardedPackageStorageTempQo);
        if(discardedPackageStorageTempExist != null && !Objects.equals(paramObj.getOperateType() ,discardedPackageStorageTempExist.getOperateType())){
            log.warn("checkBusinessParam4ScanDiscardedPackage，已操作过其他类型处处置 param: {} exist: {}", JsonHelper.toJson(paramObj), JsonHelper.toJson(discardedPackageStorageTempExist));
            result.toFail("已操作过[" + WasteOperateTypeEnum.getNameByCode(discardedPackageStorageTempExist.getOperateType()) + "]，请不要再操作其他类型的处理动作");
            return result;
        }
        // 同一人一次最多扫描maxScanSize个运单
        DiscardedPackageStorageTempQo discardedPackageStorageTempCountQo = new DiscardedPackageStorageTempQo();
        discardedPackageStorageTempCountQo.setSubmitStatus(Constants.YN_NO);
        discardedPackageStorageTempCountQo.setYn(Constants.YN_YES);
        discardedPackageStorageTempCountQo.setCurrentUserErp(paramObj.getOperateUser().getUserCode());
        final Long waybillExistCount = discardedWaybillStorageTempDao.selectCount(discardedPackageStorageTempCountQo);
        if(waybillExistCount > maxScanSize){
            log.warn("checkBusinessParam4ScanDiscardedPackage，一次最多扫描maxScanSize个运单 param: {} waybillExistCount: {}", JsonHelper.toJson(paramObj), waybillExistCount);
            result.toFail("一次最多扫描maxScanSize个运单，请先将已扫描的数据操作弃件完成");
            return result;
        }
        return result;
    }

    /**
     * 弃件暂存提交已扫描弃件数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.submitDiscardedPackage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> submitDiscardedPackage(SubmitDiscardedPackagePo paramObj) {
        if(log.isInfoEnabled()){
            log.info("DiscardedPackageStorageTempServiceImpl.submitDiscardedPackage param: {}", JsonHelper.toJson(paramObj));
        }
        Result<Boolean> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4SubmitDiscardedPackage(paramObj);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 业务条件校验
            // 2.1 是否存在未提交的数据
            final DiscardedPackageFinishStatisticsDto discardedPackageFinishStatisticsDto = discardedWaybillStorageTempDao.selectDiscardedPackageFinishStatistics(this.genUnSubmitDiscardedListQo(paramObj));
            if(discardedPackageFinishStatisticsDto.getFinishCount() + discardedPackageFinishStatisticsDto.getUnFinishCount() == 0){
                return result.toFail("没有未提交的数据，请先扫描");
            }
            // 2.2 是否强制提交，若否，则校验所有待提交包裹是否都已扫齐
            if(!Objects.equals(Constants.YN_YES, paramObj.getForceSubmit()) && discardedPackageFinishStatisticsDto.getUnFinishCount() > 0){
                return result.toFail("还有未扫描齐全的运单，请检查漏扫包裹，是否强制完成本次弃件操作？", ResultCodeConstant.MISMATCH_CONDITION);
            }
            // 3. 执行业务逻辑
            // 3.1 符合提交条件，更改所有提交状态
            final FinishSubmitDiscardedUo finishSubmitDiscardedUo = new FinishSubmitDiscardedUo();
            finishSubmitDiscardedUo
                    .setUnSubmitStatus(Constants.YN_NO)
                    .setSubmitStatus(Constants.YN_YES)
                    .setOperatorErp(paramObj.getOperateUser().getUserCode())
                    .setOperateTime(new Date(paramObj.getOperateTime()));
            final int updateCount = discardedWaybillStorageTempDao.finishSubmitDiscarded(finishSubmitDiscardedUo);
            if(updateCount == 0){
                return result.toFail("处理失败，未更新数据");
            }

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DiscardedPackageStorageTempServiceImpl.submitDiscardedPackage exception param {} exception {}", JsonHelper.toJson(paramObj), e.getMessage(), e);
        }
        return result;
    }

    private Result<Void> checkParam4SubmitDiscardedPackage(SubmitDiscardedPackagePo paramObj){
        Result<Void> result = Result.success();

        if(paramObj == null){
            log.warn("checkParam4ScanDiscardedPackage 参数不能为空");
            return result.toFail("弃件暂存请求参数不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getOperateUser() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，operateUser不能为空");
            return result.toFail("参数错误，operateUser不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getSiteDepartType() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，siteDepartType不能为空");
            return result.toFail("参数错误，siteDepartType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        /*if(paramObj.getWaybillType() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，waybillType不能为空");
            return result.toFail("参数错误，waybillType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }*/
        if(paramObj.getOperateTime() == null){
            paramObj.setOperateTime(System.currentTimeMillis());
        }

        return result;
    }

    private UnSubmitDiscardedListQo genUnSubmitDiscardedListQo(SubmitDiscardedPackagePo paramObj){
        final UnSubmitDiscardedListQo unSubmitDiscardedPackageQo = new UnSubmitDiscardedListQo();
        unSubmitDiscardedPackageQo.setOperatorErp(paramObj.getOperateUser().getUserCode());
        unSubmitDiscardedPackageQo.setUnSubmitStatus(Constants.YN_NO);
        unSubmitDiscardedPackageQo.setWaybillType(paramObj.getWaybillType());
        unSubmitDiscardedPackageQo.setPageSize(maxScanSize);
        unSubmitDiscardedPackageQo.setPageNumber(1);
        return unSubmitDiscardedPackageQo;
    }

    /**
     * 查询未扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:55:24 周四
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DiscardedPackageStorageTempServiceImpl.queryUnScanDiscardedPackage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<DiscardedPackageNotScanItemDto>> queryUnScanDiscardedPackage(QueryUnScanDiscardedPackagePo paramObj) {
        if(log.isInfoEnabled()){
            log.info("DiscardedPackageStorageTempServiceImpl.queryUnScanDiscardedPackage param: {}", JsonHelper.toJson(paramObj));
        }
        Result<List<DiscardedPackageNotScanItemDto>> result = Result.success();
        List<DiscardedPackageNotScanItemDto> dataList = new ArrayList<>();
        result.setData(dataList);
        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4QueryUnScanDiscardedPackage(paramObj);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 查询已扫描未扫描全的数据
            final UnFinishScanDiscardedPackageQo unFinishScanDiscardedPackageQo = this.genUnFinishScanDiscardedPackageQo(paramObj);
            // 查询未扫全的运单号列表
            final List<DiscardedWaybillScanResultItemDto> discardedWaybillScanList = discardedWaybillStorageTempDao.selectUnFinishScanDiscardedWaybillList(unFinishScanDiscardedPackageQo);
            // 3. 执行业务逻辑
            if (CollectionUtils.isNotEmpty(discardedWaybillScanList)) {
                Set<String> waybillCodeSet = new HashSet<>();
                Map<String, DiscardedWaybillScanResultItemDto> discardedWaybillStorageTempMap = new HashMap<>();
                for (DiscardedWaybillScanResultItemDto discardedWaybillStorageTemp : discardedWaybillScanList) {
                    waybillCodeSet.add(discardedWaybillStorageTemp.getWaybillCode());
                    discardedWaybillStorageTempMap.put(discardedWaybillStorageTemp.getWaybillCode(), discardedWaybillStorageTemp);
                }
                unFinishScanDiscardedPackageQo.setWaybillCodeList(new ArrayList<>(waybillCodeSet));
                // 找到除已扫包裹外的其他包裹
                List<String> packageCodeNotScanList = new ArrayList<>();
                final long discardedPackageScanTotal = discardedPackageStorageTempDao.selectUnFinishScanDiscardedPackageCount(unFinishScanDiscardedPackageQo);
                long batchTotal = discardedPackageScanTotal / maxScanSize + discardedPackageScanTotal % maxScanSize == 0 ? 0 : 1;
                boolean overMaxScanSizeFlag = false;
                for (int i = 0; i < (int)batchTotal; i++) {
                    if(overMaxScanSizeFlag){
                        break;
                    }
                    unFinishScanDiscardedPackageQo.setPageNumber(unFinishScanDiscardedPackageQo.getPageNumber() + i);
                    final List<DiscardedPackageScanResultItemDto> discardedPackageScanResultList = discardedPackageStorageTempDao.selectUnFinishScanDiscardedPackageList(unFinishScanDiscardedPackageQo);
                    List<String> packageCodeScanList = new ArrayList<>();
                    Set<String> waybillCodeSetTemp = new HashSet<>();
                    for (DiscardedPackageScanResultItemDto discardedPackageScanResultItemDto : discardedPackageScanResultList) {
                        packageCodeScanList.add(discardedPackageScanResultItemDto.getPackageCode());
                        waybillCodeSetTemp.add(discardedPackageScanResultItemDto.getWaybillCode());
                    }
                    for (String waybillCode : waybillCodeSetTemp) {
                        if(overMaxScanSizeFlag){
                            break;
                        }
                        final DiscardedWaybillScanResultItemDto discardedWaybillScanResultItemDto = discardedWaybillStorageTempMap.get(waybillCode);
                        final List<String> packageCodeAllList = WaybillUtil.generateAllPackageCodesByPackNum(waybillCode, discardedWaybillScanResultItemDto.getPackageSysTotal());
                        for (String packageCode : packageCodeAllList) {
                            if(!packageCodeScanList.contains(packageCode)){
                                packageCodeNotScanList.add(packageCode);
                                if(packageCodeNotScanList.size() > maxScanSize){
                                    overMaxScanSizeFlag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(packageCodeNotScanList)){
                    // 每一批maxScanSize条
                    List<List<String>> packageCodeNotScanPartitionList = ListUtils.partition(packageCodeNotScanList, maxScanSize);
                    for (List<String> packNotScanList : packageCodeNotScanPartitionList) {
                        final BaseEntity<Map<String, PackageState>> packSateResult = waybillTraceManager.getNewestPKStateByOpCodes(packNotScanList);
                        final Map<String, PackageState> packSateMapData = packSateResult.getData();
                        for (String packageCode : packNotScanList) {
                            final DiscardedPackageNotScanItemDto dto = this.genDiscardedPackageNotScanItemDto(packageCode, packSateMapData.get(packageCode));
                            dataList.add(dto);
                        }
                    }
                }
            }

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("DiscardedPackageStorageTempServiceImpl.queryUnScanDiscardedPackage exception param {} exception {}", JsonHelper.toJson(paramObj), e.getMessage(), e);
        }
        return result;
    }

    private UnFinishScanDiscardedPackageQo genUnFinishScanDiscardedPackageQo(QueryUnScanDiscardedPackagePo paramObj){
        final UnFinishScanDiscardedPackageQo unFinishScanDiscardedPackageQo = new UnFinishScanDiscardedPackageQo();
        unFinishScanDiscardedPackageQo.setOperatorErp(paramObj.getOperateUser().getUserCode());
        unFinishScanDiscardedPackageQo.setUnSubmitStatus(Constants.YN_NO);
        if(WaybillUtil.isWaybillCode(paramObj.getBarCode())){
            unFinishScanDiscardedPackageQo.setWaybillCode(paramObj.getBarCode());
        }
        if(WaybillUtil.isPackageCode(paramObj.getBarCode())){
            unFinishScanDiscardedPackageQo.setPackageCode(paramObj.getBarCode());
        }
        unFinishScanDiscardedPackageQo.setPageSize(maxScanSize);
        unFinishScanDiscardedPackageQo.setPageNumber(1);
        return unFinishScanDiscardedPackageQo;
    }

    private DiscardedPackageNotScanItemDto genDiscardedPackageNotScanItemDto(String packageCode, PackageState packageState){
        DiscardedPackageNotScanItemDto discardedPackageNotScanItemDto = new DiscardedPackageNotScanItemDto();
        discardedPackageNotScanItemDto.setPackageCode(packageCode);
        if(packageState != null){
            discardedPackageNotScanItemDto.setOperatorUserName(packageState.getOperatorUser())
                    .setOperatorUserErp(packageState.getOperatorUserErp())
                    .setOperatorSiteName(packageState.getOperatorSite())
                    .setOperatorSiteId(packageState.getOperatorSiteId())
                    .setOperateTime(packageState.getCreateTime())
                    .setOperateTimeFormative(DateUtil.formatDateTime(packageState.getCreateTime()))
                    .setOperateState(packageState.getState())
                    .setOperateStateName(packageState.getStateName());
        }
        return discardedPackageNotScanItemDto;
    }

    private Result<Void> checkParam4QueryUnScanDiscardedPackage(QueryUnScanDiscardedPackagePo paramObj){
        Result<Void> result = Result.success();

        if(paramObj == null){
            log.warn("checkParam4ScanDiscardedPackage 参数不能为空");
            return result.toFail("弃件暂存请求参数不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getOperateUser() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，operateUser不能为空");
            return result.toFail("参数错误，operateUser不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(paramObj.getSiteDepartType() == null){
            log.warn("checkParam4ScanDiscardedPackage，参数错误，siteDepartType不能为空");
            return result.toFail("参数错误，siteDepartType不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }

        return result;
    }

}
