package com.jd.bluedragon.distribution.ver.filter.filters;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class ForceChangeWaybillSignFilter implements Filter {

    @Autowired
    private WaybillService waybillService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        // 提示 WaybillDistributeTypeChangeFilter 存在拦截消息的改址拦截 这里只处理标位的 ForceChangeWaybillSignFilter 中处理强制改址拦截 ChangeWaybillSignFilter 中处理弱拦截

        // 改址拦截（现阶段存在快递改址和快运改址）
        boolean kdAddressModify = WaybillCacheHelper.isForceChangeWaybillSign(request.getWaybillCache());
        boolean kyAddressModify = BusinessUtil.isKyAddressModifyWaybill(request.getWaybillCache().getWaybillSign());
        List<Integer> featureTypeList = Lists.newArrayList(); // 拦截类型集合
        if(kdAddressModify){
            featureTypeList.add(CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
        }
        if(kyAddressModify){
            featureTypeList.add(CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT);
        }
        if (CollectionUtils.isEmpty(featureTypeList)){
            chain.doFilter(request, chain);
            return;
        }
        // 查询所有拦截类型记录
        BlockResponse response;
        if (WaybillUtil.isPackageCode(request.getPackageCode())) {
            response = waybillService.checkPackageBlockByFeatureTypes(request.getPackageCode(), featureTypeList);
        } else {
            response = waybillService.checkWaybillBlockByFeatureTypes(request.getWaybillCode(), featureTypeList);
        }
        if (BlockResponse.BLOCK.equals(response.getCode())) {
            Integer currentFeatureType = response.getFeatureType();
            // 快递改址拦截
            if(Objects.equals(currentFeatureType, CancelWaybill.FEATURE_TYPE_ORDER_MODIFY)){
                throw  new SortingCheckException(SortingResponse.CODE_29333,
                        HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_INFO_CHANGE_FORCE, request.getFuncModule()));
            }
            // 快运包裹维度改址拦截
            if(WaybillUtil.isPackageCode(request.getPackageCode())){
                throw  new SortingCheckException(SortingResponse.CODE_29333,
                        HintService.getHintWithFuncModule(HintCodeConstants.PACK_KY_ADDRESS_MODIFY_INTERCEPT, request.getFuncModule()));
            }
            // 快运运单维度改址拦截
            List<String> blockPackageList = response.getBlockPackages();
            if(CollectionUtils.isNotEmpty(blockPackageList) && blockPackageList.size() < 5){ // 运单下拦截状态的包裹数小于5则提示具体包裹
                List<Integer> lockPackIndexList = Lists.newArrayList();
                for (String packCode : blockPackageList) {
                    lockPackIndexList.add(WaybillUtil.getPackIndexByPackCode(packCode));
                }
                Map<String, String> argsMap = new HashMap<>();
                argsMap.put(HintArgsConstants.ARG_FIRST, JsonHelper.toJson(lockPackIndexList));
                throw  new SortingCheckException(SortingResponse.CODE_29333,
                        HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_KY_ADDRESS_MODIFY_INTERCEPT_HINT, request.getFuncModule(), argsMap));
            }
            throw  new SortingCheckException(SortingResponse.CODE_29333,
                    HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_KY_ADDRESS_MODIFY_INTERCEPT, request.getFuncModule()));
        }
        //endregion
        chain.doFilter(request, chain);
    }
}
