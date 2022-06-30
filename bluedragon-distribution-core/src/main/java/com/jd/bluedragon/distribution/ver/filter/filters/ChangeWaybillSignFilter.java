package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
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
import com.jd.bluedragon.dms.utils.*;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import com.jd.etms.waybill.dto.WaybillProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class ChangeWaybillSignFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        String waybillSign = request.getWaybillCache().getWaybillSign();
        //增加接货仓 KA安踏预售逻辑 (只考虑ECLP仓配外单场景  减少调用)
        if(BusinessUtil.isEclpAndWmsForDistribution(waybillSign) && request.getCreateSite() != null && request.getCreateSite().getSubType() !=null &&
                Integer.valueOf(Constants.JHC_SITE_TYPE).equals(request.getCreateSite().getSubType())){
            //获取能力信息
            List<WaybillProductDto> waybillProductDtos = request.getWaybillProductDtos();
            //校验能力项（是预售暂存付完尾款）
            if(waybillQueryManager.checkWaybillProductAbility(waybillProductDtos,Constants.PRODUCT_ABILITY_OF_PRE_SELL_PAY_DONE)){
                //已付尾款 直接跳过订单信息变更提示
                chain.doFilter(request, chain);
                return;
            }
        }

        // 提示 WaybillDistributeTypeChangeFilter 存在拦截消息的改址拦截 这里只处理标位的 ForceChangeWaybillSignFilter 中处理强制改址拦截 ChangeWaybillSignFilter 中处理弱拦截
        //region 订单信息修改的，拦截重新打印包裹标签
        if (WaybillCacheHelper.isWeakChangeWaybillSign(request.getWaybillCache())) {
            BlockResponse response = null;
            if (WaybillUtil.isPackageCode(request.getPackageCode())) {
                response = waybillService.checkPackageBlock(request.getPackageCode(), CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
            } else {
                response = waybillService.checkWaybillBlock(request.getWaybillCode(), CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
            }
            if (response == null || !BlockResponse.UNBLOCK.equals(response.getCode())) {
                throw  new SortingCheckException(SortingResponse.CODE_39123,
                        HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_INFO_CHANGE, request.getFuncModule()));

            }
        }
        //endregion

        chain.doFilter(request, chain);
    }
}
