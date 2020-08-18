package com.jd.bluedragon.distribution.ver.filter.forward;


import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterRequest;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class WaybillPackExistFilter implements Filter {

    @Autowired
    private WaybillService waybillService;

    @Override
    public void doFilter(FilterRequest request, FilterChain chain) throws Exception {

        if(WaybillUtil.isWaybillCode(request.getPackageCode()) && !BusinessUtil.isBoxcode(request.getPackageCode())){
            if(request.getWaybill() == null || request.getWaybill().getQuantity() == null
                    || request.getWaybill().getQuantity().equals(Integer.valueOf(0))){
                //防止特殊情况，需再去调用运单接口确认数据
                Waybill waybill =  waybillService.getNoCache(request.getPackageCode());
                if(waybill == null || waybill.getQuantity()==null || waybill.getQuantity().equals(Integer.valueOf(0))){
                    //此时认为无运单数据
                    throw new SortingCheckException(SortingResponse.CODE_29412,
                            SortingResponse.MESSAGE_29412);
                }else{
                	request.setPackageNum(waybill.getQuantity());
                }
            }
        }
        chain.doFilter(request, chain);
    }
}
