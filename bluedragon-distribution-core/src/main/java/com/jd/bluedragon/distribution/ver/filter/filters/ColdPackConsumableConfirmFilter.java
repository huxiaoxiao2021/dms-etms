package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.ColdChainReverseManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.WaybillVasDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.ver.filter.filters
 * @Description:
 * @date Date : 2022年04月01日 16:00
 */
public class ColdPackConsumableConfirmFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private WaybillCommonService waybillCommonService;
    @Resource
    private ColdChainReverseManager coldChainReverseManager;

    /**
     * 医药保温箱增值服务
     * ll-a-0002
     */
    public static final String MEDICAL_INCUBATOR = "ll-a-0002";
    /**
     * 医药包装增值服务
     */
    public static final String MEDICAL_CONSUMABLE = "md-a-0004";

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        logger.info("ColdPackConsumableConfirmFilter start...");
        try{
            if (this.isContainPackConsumableService(request)) {
                Boolean needConfirmed = coldChainReverseManager.checkIsNeedConfirmed(request.getWaybillCode());
                if (needConfirmed) {
                    //强制拦截
                    throw new SortingCheckException(SortingResponse.CODE_29322,
                            HintService.getHintWithFuncModule(HintCodeConstants.PACKING_CONSUMABLE_CONFIRM, request.getFuncModule()));
                }
            }
        }catch (RuntimeException e){
            logger.error("查询冷链运单是否已经确认耗材失败，运单号：" + JsonHelper.toJson(request), e);
        }

        chain.doFilter(request, chain);
    }

    /**
     * 过滤需要检查确认的运单
     * @param request
     * @return
     */
    private boolean isContainPackConsumableService(FilterContext request){
        WaybillCache waybill = request.getWaybillCache();
        if (waybill == null || StringUtils.isBlank(waybill.getWaybillSign()) || StringUtils.isBlank(waybill.getWaybillCode())) {
            return Boolean.FALSE;
        }
        String waybillSign = waybill.getWaybillSign();
        String waybillCode = request.getWaybillCode();
        // 是否医药冷链产品（精温送）,历史原因这个产品没有打上行业标
        if(BusinessUtil.isMedicalFreshProductType(waybillSign)){
            return Boolean.TRUE;
        }
        //wbs54=2、4分别表示生鲜行业和医药行业，这是冷链标识
        if(BusinessUtil.isColdChainWaybill(waybillSign)
                || BusinessUtil.isBMedicine(waybillSign)){
            //查询是否包含包装增值服务
            List<WaybillVasDto> list =  waybillCommonService.getWaybillVasList(waybillCode);
            if(CollectionUtils.isNotEmpty(list)){
                for(WaybillVasDto waybillVasDto : list){
                    if(Objects.equals(MEDICAL_INCUBATOR,waybillVasDto.getVasNo()) || Objects.equals(MEDICAL_CONSUMABLE,waybillVasDto.getVasNo())){
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }
}
