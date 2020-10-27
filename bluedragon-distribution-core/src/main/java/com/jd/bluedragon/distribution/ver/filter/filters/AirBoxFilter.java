package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class AirBoxFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //region 航空箱承运类型判断.如果箱子的承运类型是航空箱，但运单不是航空箱承运类型，则提示错误。航填的运单不做判断
        if (request.getBox() != null && !WaybillCacheHelper.isAirFillSign(request.getWaybillCache())) {
            //是否为航空箱
            int box_transport = request.getBox().getTransportType() != null && request.getBox().getTransportType().equals(Box.TRANSPORT_TYPE_AIR) ? 1 : 0;

            if (WaybillCacheHelper.isPlateWaybill(request.getWaybillCache())) {/*平台运单，目前主要为外单*/

                int waybill_transport =  WaybillCacheHelper.getAirSignNew(request.getWaybillCache())? 1 : 0;

                //新增逻辑：如果箱子为航空运输且包裹为非航空包裹提示：
                if(1==box_transport && waybill_transport==0){
                    throw new SortingCheckException(SortingResponse.CODE_39121, SortingResponse.MESSAGE_39121_1);
                }else if ((box_transport + waybill_transport) == 1 && SiteHelper.isDistributionCenter(request.getsReceiveBoxSite())) {
                    throw new SortingCheckException(SortingResponse.CODE_39121, SortingResponse.MESSAGE_39121);
                }
            } else {/*自营运单*/
                if ((!WaybillCacheHelper.isAirWaybill(request.getWaybillCache())) && box_transport == 1 ) {
                    throw new SortingCheckException(SortingResponse.CODE_39121,
                            SortingResponse.MESSAGE_39121_1);
                }
            }
        }

        chain.doFilter(request, chain);
    }
}
