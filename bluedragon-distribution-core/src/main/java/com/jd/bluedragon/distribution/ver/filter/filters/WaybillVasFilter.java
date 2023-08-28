package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 增值服务相关
 * @author fanggang7
 * @time 2023-08-15 16:16:04 周二
 */
public class WaybillVasFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillCommonService waybillCommonService;


    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(request.getPackageCode());
        if (!Objects.equals(barCodeType, BarCodeType.WAYBILL_CODE) && !Objects.equals(barCodeType, BarCodeType.PACKAGE_CODE)) {
            return;
        }
        final WaybillCache waybill = request.getWaybillCache();
        if (waybill == null) {
            return;
        }
        // 判断产品类型
        // 1. 航空件类型
        if(BusinessUtil.isAirLineMode(waybill.getWaybillSign())){
            logger.info("WaybillVasFilter 航空件 {}", JsonHelper.toJson(request));
            request.getMsgBoxList().add(new InvokeWithMsgBoxResult.MsgBox(InvokeWithMsgBoxResult.MsgBoxTypeEnum.PROMPT, InvokeResult.CODE_AIR_TRANSPORT, InvokeResult.CODE_AIR_TRANSPORT_MESSAGE));
        }
        // 2. 生鲜特保类型
        if (BusinessUtil.hasWaybillVas(waybill.getWaybillSign())) {
            logger.info("WaybillVasFilter 具有增值服务 {}", request.getPackageCode());
            // 无增值服务不需要调接口查询
            final Result<Boolean> checkWaybillVasResult = waybillCommonService.checkWaybillVas(waybill.getWaybillCode(), WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD_COLD_FRESH_OPERATION);
            if(!checkWaybillVasResult.isSuccess()){
                logger.info("WaybillVasFilter checkWaybillVas fail {}", JsonHelper.toJson(request));
                return;
            }
            if(checkWaybillVasResult.getData()){
                logger.info("WaybillVasFilter 生鲜特保件 {}", request.getPackageCode());
                request.getMsgBoxList().add(new InvokeWithMsgBoxResult.MsgBox(InvokeWithMsgBoxResult.MsgBoxTypeEnum.PROMPT, InvokeResult.CODE_FRESH_SPECIAL, InvokeResult.CODE_FRESH_SPECIAL_MESSAGE));
                return;
            }
        }

        chain.doFilter(request, chain);
    }
}
