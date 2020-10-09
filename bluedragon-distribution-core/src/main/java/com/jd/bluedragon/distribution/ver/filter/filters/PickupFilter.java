package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dudong
 * @date 2016/3/2
 * 自提校验过滤
 * 
 * 1. 自提订单分配到自提点 
 * 2. 生鲜订单必须分拣到生鲜自提点，不能分拣到普通自提点
 */
public class PickupFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void doFilter(FilterContext request, FilterChain chain) throws Exception {
		/*if (logger.isInfoEnabled()) {*/
			logger.info("do filter process...");
		/*}*/
		
		if (WaybillCacheHelper.isPickup(request.getWaybillCache())){//1. 自提订单
			if(!SiteHelper.isDistributionCenter(request.getReceiveSite())){
				//2. 判断是不否是生鲜订单
				//3. 判断站点是否支持生鲜自提,如果否则提示: "此[包裹]或[运单]为[生鲜自提订单], 请分拣到生鲜自提点"
				if(WaybillCacheHelper.isShengXian(request.getWaybillCache()) && ! SiteHelper.hasShengXianPickupBusiness(request.getReceiveSite())) {
					throw new SortingCheckException(SortingResponse.CODE_2910801, SortingResponse.MESSAGE_2910801);
				}
				
				//4. 站点没有自提业务, 提示: "此[包裹]或[运单]为[自提订单], 请分拣到自提点"
				if(!SiteHelper.hasPickupBusiness(request.getReceiveSite())){
					throw new SortingCheckException(SortingResponse.CODE_29108, SortingResponse.MESSAGE_29108);
				}
			}
		}else if(SiteHelper.hasOnlyPickupBusiness(request.getReceiveSite())){//5. 非自提订单,站点为纯自提点提示: "此[站点]只能分拣[自提订单]"
			throw new SortingCheckException(SortingResponse.CODE_29204, SortingResponse.MESSAGE_29204);
		}

		chain.doFilter(request, chain);
	}

}
