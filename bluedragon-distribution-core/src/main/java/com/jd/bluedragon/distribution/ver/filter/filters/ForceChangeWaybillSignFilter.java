package com.jd.bluedragon.distribution.ver.filter.filters;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.AddressForwardWaybillCheckRequest;
import com.jd.bluedragon.common.domain.AddressForwardWaybillCheckResult;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
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
    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        // 提示 WaybillDistributeTypeChangeFilter 存在拦截消息的改址拦截 这里只处理标位的 ForceChangeWaybillSignFilter 中处理强制改址拦截 ChangeWaybillSignFilter 中处理弱拦截

        // 由于非一单到底场景的单子，中台不会发送拦截MQ，所以此处新增校验非一单到底改址转寄场景，并且此场景优先，其次再执行原有的逻辑
        checkAddressForwarding(request);

        // 改址拦截（现阶段存在快递改址和快运改址）
        // todo 202404改址一单到底需求改动，原理解快递改址【featureType=6】，实际是快递配运方式变化， 原快运改址消息【featureType=9】，实际快递、快运是同一个消息，不再对快运单独区分
        boolean kdAddressModify = WaybillCacheHelper.isForceChangeWaybillSign(request.getWaybillCache());
        boolean kyAddressModify = BusinessUtil.isKyAddressModifyWaybill(request.getWaybillCache().getWaybillSign());
        List<Integer> featureTypeList = Lists.newArrayList(); // 拦截类型集合
        if(kdAddressModify){
            featureTypeList.add(CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
        }
        else {
            featureTypeList.add(CancelWaybill.FEATURE_TYPE_KD_CHANGE_ADDRESS_CHANGE_WAYBILL);
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
            // 改址换单拦截
            if(Objects.equals(currentFeatureType, CancelWaybill.FEATURE_TYPE_KD_CHANGE_ADDRESS_CHANGE_WAYBILL)){
                throw  new SortingCheckException(SortingResponse.CODE_29333,
                        HintService.getHintWithFuncModule(HintCodeConstants.CHANGE_ADDRESS_CHANGE_WAYBILL_INTERCEPT, request.getFuncModule()));
            }
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


    /**
     * 校验非一单到底改址转寄场景
     * @param request 上下文
     * @throws Exception 异常
     * @throws SortingCheckException 分拣校验异常
     */
    private void checkAddressForwarding(FilterContext request) throws Exception {
        // 开关校验
        if (!sysConfigService.getConfigByName(Constants.ADDRESS_FORWARD_NOT_JUST_ONE_ORDER_SWITCH)) {
            return;
        }

        // 构建校验参数
        AddressForwardWaybillCheckRequest addressForwardWaybillCheckRequest = createAddressForwardWaybillCheckRequest(request);
        // 调用校验接口
        AddressForwardWaybillCheckResult result = waybillService.isAddressForwardingWaybill(addressForwardWaybillCheckRequest);
        if (result == null) {
            return;
        }
        // 此单为改址拦截单，请操作换单打印
        if (result.isExchangePrintFlag()) {
            throw new SortingCheckException(SortingResponse.CODE_29333,
                    HintService.getHintWithFuncModule(HintCodeConstants.CHANGE_ADDRESS_CHANGE_WAYBILL_INTERCEPT, request.getFuncModule()));
        }
        // 订单信息变更,请补打包裹标签
        if (result.isRePrintFlag()) {
            throw new SortingCheckException(SortingResponse.CODE_29333,
                    HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_INFO_CHANGE_FORCE, request.getFuncModule()));
        }
    }

    /**
     * 创建地址转寄运单校验请求
     * @param request 过滤上下文对象
     * @return 返回地址转寄运单校验请求对象
     * @throws NullPointerException 如果请求中的运单缓存为null
     */
    private AddressForwardWaybillCheckRequest createAddressForwardWaybillCheckRequest(FilterContext request) {
        WaybillCache waybillCache = request.getWaybillCache();
        // 构建校验参数
        AddressForwardWaybillCheckRequest addressForwardWaybillCheckRequest = new AddressForwardWaybillCheckRequest();
        if (waybillCache == null) {
            return addressForwardWaybillCheckRequest;
        }
        // 运单号
        addressForwardWaybillCheckRequest.setWaybillCode(waybillCache.getWaybillCode());
        // 运单标位
        addressForwardWaybillCheckRequest.setWaybillSign(waybillCache.getWaybillSign());
        // 百川标识
        if (waybillCache.getWaybillExtVO() != null) {
            addressForwardWaybillCheckRequest.setOmcOrderCode(waybillCache.getWaybillExtVO().getOmcOrderCode());
        }
        // 运单增值服务列表
        addressForwardWaybillCheckRequest.setWaybillVasDtos(request.getWaybillVasDtos());
        return addressForwardWaybillCheckRequest;
    }

}
