package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.weightVolume.domain.ZeroWeightVolumeCheckEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 称重量方拦截
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/12/20
 * @Description:
 */
public class ZeroWeightVolumeFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(ZeroWeightVolumeFilter.class);

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        //依赖数据获取
        String waybillCode = request.getWaybillCode();
        String waybillSign = request.getWaybillCache().getWaybillSign();
        String packageCode = request.getPackageCode();
        String customerCode = request.getWaybillCache().getCustomerCode();


        ZeroWeightVolumeCheckEntity entity = new ZeroWeightVolumeCheckEntity();
        entity.setWaybillSign(waybillSign);
        entity.setCustomerCode(customerCode);
        entity.setPackageCode(packageCode);
        entity.setWaybillCode(waybillCode);

        if(dmsWeightVolumeService.zeroWeightVolumeIntercept(entity)){
            throw new SortingCheckException(SortingResponse.CODE_29403,
                    HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_OR_VOLUME, request.getFuncModule()));
        }

        chain.doFilter(request, chain);

    }


}
