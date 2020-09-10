package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.etms.waybill.dto.PackageStateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 拒收、妥投禁止分拣
 *      存在拒收、妥投节点则提示拦截,操作配送异常可以跳过(临时解决方案)
 *
 * @author: hujiping
 * @date: 2019/12/30 11:27
 */
public class WaybillPackStateFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Autowired
    private AbnormalWayBillService abnormalWayBillService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        logger.info("do WaybillPackStateFilter process waybillCode[{}]" + request.getWaybillCode());
        String waybillCode = request.getWaybillCode();
        String state = Constants.WAYBILL_TRACE_STATE_REJECTED + Constants.SEPARATOR_COMMA
                + Constants.WAYBILL_TRACE_STATE_APPROPRIVATED;
        List<PackageStateDto> existList = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, state);
        //操作过拒收或妥投
        if(existList != null && ! existList.isEmpty()){
            //当前站点是否操作异常处理标识
            boolean abnormalSign = this.isTreatedAbnormal(waybillCode, request.getCreateSiteCode());
            if(! abnormalSign) {
                throw new SortingCheckException(SortingResponse.CODE_29418, SortingResponse.MESSAGE_29418);
            }
        }

        chain.doFilter(request, chain);

    }

    private boolean isTreatedAbnormal(String waybillCode, Integer siteCode) {
        AbnormalWayBill abnormalWayBill = null;
        try {
            abnormalWayBill = abnormalWayBillService.getAbnormalWayBillByWayBillCode(waybillCode, siteCode);
        }catch (Exception e){
            logger.error("查询站点：{}运单号：{}的异常记录失败!" ,siteCode ,waybillCode,e);
        }
        return abnormalWayBill != null;
    }
}
