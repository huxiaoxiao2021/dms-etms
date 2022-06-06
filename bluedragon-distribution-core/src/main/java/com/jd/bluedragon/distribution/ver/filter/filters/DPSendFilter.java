package com.jd.bluedragon.distribution.ver.filter.filters;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.dto.WaybillExtraItemDto;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 德邦发货校验
 */
public class DPSendFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UccPropertyConfiguration uccConfiguration;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 1. 非德邦一单一件的不能发到德邦虚拟分拣中心
     * @param request
     * @param chain
     * @throws Exception
     */
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        String waybillCode = request.getWaybillCode();
        Integer receiveSiteCode = request.getReceiveSiteCode();
        List<Integer> dpSiteCodeList = uccConfiguration.getDpSiteCodeList();
        CallerInfo info = Profiler.registerInfo("DMSWEB.DPSendFilter.doFilter", false, true);
        try{
            //发货目的非德邦不校验
            if(!BusinessHelper.isDPSiteCode(dpSiteCodeList, receiveSiteCode)){
                chain.doFilter(request, chain);
                return;
            }

            //查询运单包裹数和 运单扩展属性
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillExtend(true);
            BaseEntity<BigWaybillDto> baseEntity =  waybillQueryManager.getDataByChoiceNoCache(waybillCode, wChoice);
            if(baseEntity.getData() == null){
                logger.warn("根据运单号获取BigWaybillDto返回data为null,waybillCode:{},baseEntity.code:{}", waybillCode, baseEntity.getResultCode());
                chain.doFilter(request, chain);
                return;
            }

            BigWaybillDto bigWaybillDto = baseEntity.getData();
            //获取信息失败，不拦截
            Waybill waybill = bigWaybillDto.getWaybill();
            int packageNum = waybill.getGoodNumber() == null ? 0 : waybill.getGoodNumber();
            // 非一单一件发到德邦分拣中心时拦截
            if(packageNum > 1){
                logger.warn("非一单一件不能发到德邦分拣中心,waybill:{},receiveSiteCode:{}", waybillCode, receiveSiteCode);
                throw new SortingCheckException(SortingResponse.CODE_DP_SEND_ERROR,
                        HintService.getHintWithFuncModule(HintCodeConstants.NOT_ONE_PACK_WAYBILL_WRONG_SEND_MSG, request.getFuncModule()));
            }



            if(waybill == null){
                logger.error("非德邦单 发到德邦拦截，获取waybill为null为空，waybill:{}", waybillCode);
                chain.doFilter(request, chain);
                return;
            }

            WaybillExt waybillExt = waybill.getWaybillExt();
            if(waybillExt == null || !BusinessHelper.isDPWaybill(waybillExt.getThirdCarrierFlag())){
                logger.warn("非德邦单发到德邦分拣中心被拦截,waybillCode:{},receiveSiteCode:{}", waybillCode, receiveSiteCode);
                throw new SortingCheckException(SortingResponse.CODE_DP_SEND_ERROR,
                        HintService.getHintWithFuncModule(HintCodeConstants.NOT_DP_WAYBILL_WRONG_SEND_MSG, request.getFuncModule()));
            }
        }catch (SortingCheckException se){
            throw se;
        }catch (Exception e){
            logger.error("德邦发货校验异常", e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }

        chain.doFilter(request, chain);
    }




}
