package com.jd.bluedragon.distribution.wastePackage.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.WastePackageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.wastePackage.service.WastePackageService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_PARAMETER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;

/**
 * 弃件暂存
 * @author biyuo
 * @date 2021/3/23
 */
@Service("wastePackageService")
public class WastePackageServiceImpl implements WastePackageService {
    private final Logger log = LoggerFactory.getLogger(WastePackageServiceImpl.class);

    @Qualifier("waybillPackageManager")
    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 生成滞留明细消息
     * @param request
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.com.WastePackageServiceImpl.wastepackagestorage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> wastepackagestorage(WastePackageRequest request) {
        InvokeResult<Boolean> result = checkParam(request);
        if(RESULT_SUCCESS_CODE != result.getCode()){
            return result;
        }

        try {
//            if (!waybillTraceManager.isWaybillWaste(request.getWaybillCode())){
//                result.error("不是弃件，请勿操作弃件暂存");
//                return result;
//            }

//            List<String> packageList = getPackageCodesByWaybillCode(request.getWaybillCode());
//            if (CollectionUtils.isEmpty(packageList)) {
//                result.error("没有查询到运单号内包裹信息");
//                return result;
//            }

            log.info("发送弃件全程跟踪。运单号：{}",request.getWaybillCode());
            BdTraceDto packagePrintBdTraceDto = getPackagePrintBdTraceDto(request);
            //发送全程跟踪消息
            waybillQueryManager.sendBdTrace(packagePrintBdTraceDto);


        }catch (Exception e){
            log.error("弃件暂存异常,请求参数：{}", JsonHelper.toJson(request),e);
            result.error("弃件暂存异常,请联系分拣小秘！");
        }

        return result;
    }

    private List<String> getPackageCodesByWaybillCode(String waybillCode) {
        String key = CacheKeyConstants.CACHE_KEY_WAYBILL_PACKAGE_CODES + waybillCode;
        List<String> packageCodes = jimdbCacheService.getList(key, String.class);
        // 如果缓存为空，从远程获取
        if (org.apache.commons.collections.CollectionUtils.isEmpty(packageCodes)) {
            packageCodes = waybillPackageManager.getWaybillPackageCodes(waybillCode);
            // 如果不为空，缓存7天
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(packageCodes)) {
                jimdbCacheService.setEx(key, packageCodes, 7, TimeUnit.DAYS);
                return packageCodes;
            }
        }
        return packageCodes;
    }

    private BdTraceDto getPackagePrintBdTraceDto(WastePackageRequest request) {
        BdTraceDto bdTraceDto = new BdTraceDto();
        bdTraceDto.setWaybillCode(request.getWaybillCode());
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL);
        bdTraceDto.setOperatorDesp(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL_MSG);
        bdTraceDto.setOperatorSiteId(null!=request.getSiteCode()?request.getSiteCode():0);
        bdTraceDto.setOperatorSiteName(request.getSiteName());
        bdTraceDto.setOperatorUserName(request.getUserName());
        bdTraceDto.setOperatorUserId(null!=request.getUserCode()?request.getUserCode():0);
        bdTraceDto.setOperatorTime(new Date());
        return bdTraceDto;
    }

    /**
     * 参数检查
     * @param request
     * @return
     */
    private InvokeResult<Boolean> checkParam(WastePackageRequest request){
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.setCode(RESULT_PARAMETER_ERROR_CODE);
        if(request == null){
            invokeResult.setMessage("弃件暂存请求参数null");
            log.warn("弃件暂存上报请求参数为空");
            return invokeResult;
        }
        String barcode = request.getWaybillCode();
        if(StringUtils.isBlank(barcode)){
            invokeResult.setMessage("弃件暂存请求参数错误，运单号为空");
            log.warn("弃件暂存请求参数错误，运单号为空");
            return invokeResult;
        }

        if(!WaybillUtil.isWaybillCode(barcode)){
            invokeResult.setMessage("弃件暂存请求参数错误，运单号不正确");
            log.warn("弃件暂存请求参数错误，运单号不正确");
            return invokeResult;
        }

        invokeResult.success();
        return invokeResult;
    }
}
