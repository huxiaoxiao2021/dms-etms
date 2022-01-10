package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class FWaybillFilter implements Filter {
	public static final String PARALLEL_INVENTORY_SWITCH = "parallel.inventory.switch";
	public static final String SWITCH_ON = "1";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SiteService siteService;

	@Autowired
	private QuickProduceService quickProduceService;

	@Autowired
	private SysConfigService sysConfigService;

	@Override
	public void doFilter(FilterContext request, FilterChain chain) throws Exception {
		logger.info("do filter process...");

		WaybillCache waybill = request.getWaybillCache();

		if (waybill.getPaymentType() == null) {
			Integer paymentType = null;
			String configContent = this.getSwitchStatus();
			// 如果获取开关失败或者开关是开状态，则执行
			if (configContent == null || configContent.trim().equals(SWITCH_ON)) {
				try {
					// 如果运单支付方式为空，从订单中间件和外单接口获取数据
					paymentType = this.getWaybillPrePack(waybill.getWaybillCode());
				} catch (Exception e) {
					logger.error("[支付方式] 从订单中间件外单获取运单信息失败", e);
				}
				if (logger.isInfoEnabled()) {
					logger.info("[支付方式] 从订单中间件外单获取运单支付方式为 " + paymentType);
				}
			}
			if (null == paymentType) {
				throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                        HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_MISSING_PAYMENT_TYPE, request.getFuncModule()));
			}
			waybill.setPaymentType(paymentType);
		}

		if (waybill.getSendPay() == null) {
			throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                    HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_MISSING_SEND_PAY, request.getFuncModule()));
		}

		if (waybill.getType() == null) {
			throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR,
                    HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_TYPE_MISSING, request.getFuncModule()));
		}

		//大件订单 不进行预分拣相关校验 tangcq2018年11月2日10:28:42
		if (BusinessHelper.isLasOrder(request.getWaybillCache().getWaybillSign())){
			chain.doFilter(request, chain);
			return;
		}

		// 判断预分拣站点是否合法,
		Site waybillSite = null;
		//判断预分拣站点是否存在
		if (request.hasPreSite()) {
			waybillSite = siteService.get(request.getWaybillCache().getSiteCode());
			//判断预分拣站点是否还在运营
			if (null == waybillSite || null == waybillSite.getCode() || waybillSite.getCode() <= 0) {
				throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SITE_CLOSE,
                        HintService.getHintWithFuncModule(HintCodeConstants.PRE_SITE_CLOSE, request.getFuncModule()));
			}
		} else if(request.hasEndDmsId()){
			Site waybillEndDmsSite = siteService.get(request.getWaybillCache().getEndDmsId());
			if (null == waybillEndDmsSite || null == waybillEndDmsSite.getCode() || waybillEndDmsSite.getCode() <= 0) {
				throw new SortingCheckException(SortingResponse.CODE_WAYBILL_EMD_DMSSITE_CLOSE,
                        HintService.getHintWithFuncModule(HintCodeConstants.EMD_DMSSITE_CLOSE, request.getFuncModule()));
			}
			request.setWaybillEndDmsSite(waybillEndDmsSite);
		}else {
			throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SUPER_AREA,
					HintService.getHintWithFuncModule(HintCodeConstants.OVER_AREA_WAYBILL, request.getFuncModule()));
		}

		request.setWaybillSite(waybillSite);

		chain.doFilter(request, chain);

	}


	private Integer getWaybillPrePack(String waybillCodeOrPackage) {

		// 转换运单号
		String waybillCode = WaybillUtil.getWaybillCode(waybillCodeOrPackage);
		// 调用服务
		try {
			QuickProduceWabill quickProduceWabill = quickProduceService.getQuickProduceWabill(waybillCode);
			if(quickProduceWabill == null){
				logger.warn("运单号【{}】调用订单中间键获取运单包裹信息接口成功, 无数据", waybillCode);
				return null;
			}
			Waybill waybill = quickProduceWabill.getWaybill();
			if (waybill == null) {
				logger.warn("运单号【{}】调用根据运单号获取运单包裹信息接口成功, 无数据", waybillCode);
				return null;
			}

			logger.info("快生根据运单号获取运单包裹信息接口成功. 运单号【{}】", waybillCode);
			return waybill.getPaymentType();

		} catch (Exception e) {
			// 调用服务异常
			logger.error("根据运单号【{}】 获取运单包裹信息接口 --> 异常", waybillCode, e);
		}
		return null;
	}

	private String getSwitchStatus() {
		List<SysConfig> sysConfigs = null;
		try{
			sysConfigs = sysConfigService.getCachedList(PARALLEL_INVENTORY_SWITCH);
		}catch(Exception ex){
			logger.error("获取开关信息失败:conName={}",PARALLEL_INVENTORY_SWITCH, ex);
			return null;
		}

		if(null == sysConfigs || sysConfigs.size() <= 0){
			return null;
		}

		SysConfig sysConfig = sysConfigs.get(0);
		return sysConfig.getConfigContent();
	}
}
