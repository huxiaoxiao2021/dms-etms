package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.waybill.dao.CancelWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptModeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillSiteTrackMq;
import com.jd.bluedragon.utils.JsonHelper;
import com.alibaba.fastjson.JSON;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class WaybillCancelServiceImpl implements WaybillCancelService {
    private static final Log LOGGER = LogFactory.getLog(WaybillCancelServiceImpl.class);

    private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String IS_RERUND_WAYBILL = "29303";
	private static final String DMSVER_URI = "dmsver.uri";
	
	private final RestTemplate template = new RestTemplate();

	@Autowired
	private CancelWaybillDao cancelWaybillDao;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

	@SuppressWarnings({ "rawtypes" })
    @Override
	public Boolean isRefundWaybill(String waybillCode) {
//		String uri = PropertiesHelper.newInstance().getValue(DMSVER_URI) + waybillCode;
//
//		this.setTimeout();
//
//		ResponseEntity<Map> map = this.template.getForEntity(uri, Map.class);
//		if (map != null && map.getBody() != null && map.getBody().get("code") != null
//				&& map.getBody().get("code").toString().equals(IS_RERUND_WAYBILL)) {
//			return Boolean.TRUE;
//		}
//
//		return Boolean.FALSE;

        try {
            SortingJsfResponse jsfResponse = jsfSortingResourceService.isCancel(waybillCode);
            LOGGER.error("调用VER接口获取订单退款100分状态 " + JsonHelper.toJson(jsfResponse));
            return jsfResponse.getCode().equals(Integer.valueOf(IS_RERUND_WAYBILL));
        } catch (Exception e) {
            LOGGER.error("调用VER接口获取订单是否是退款100分失败", e);
            return Boolean.FALSE;
        }

	}

//	private void setTimeout() {
//		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//		factory.setConnectTimeout(500);
//		factory.setReadTimeout(3000);
//
//		this.template.setRequestFactory(factory);
//	}

    /**
     * 按运单号获取运单取消列表
     * @param waybillCode 运单号
     * @return 结果列表
     * @author fanggang7
     * @time 2020-09-09 11:27:10 周三
     */
    @Override
    public List<CancelWaybill> getByWaybillCode(String waybillCode){
        List<CancelWaybill> cancelWaybills = this.cancelWaybillDao.getByWaybillCode(waybillCode);
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return null;
        }
        return cancelWaybills;
    }

    /**
     * 异常平台发出的可换单消息处理
     * @param waybillSiteTrackMq 消息体
     * @return 处理结果
     * @author fanggang7
     * @time 2020-09-09 16:09:21 周三
     */
    @Override
    public InvokeResult<Boolean> handleWaybillSiteTrackMq(WaybillSiteTrackMq waybillSiteTrackMq){
        log.info("WaybillCancelServiceImpl handleWaybillSiteTrackMq param {}", JSON.toJSONString(waybillSiteTrackMq));
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(true);
        try {
            CancelWaybill cancelWaybill = new CancelWaybill();
            this.assembleWaybillCancelModel(cancelWaybill, waybillSiteTrackMq);
            // 写入到waybill_cancel表中
            boolean addResult = cancelWaybillDao.add(cancelWaybill);
            if(!addResult){
                result.setData(false);
                result.setMessage("插入数据到waybill_cancel表中失败");
            }
        } catch (Exception e) {
            log.error("WaybillCancelServiceImpl handleWaybillSiteTrackMq exception {}", e.getMessage(), e);
            result.setData(false);
        }
        return result;
    }

    /**
     * 将消息转换为waybill_cancel记录
     * @param cancelWaybill 目标记录
     * @param waybillSiteTrackMq 消息体
     * @author fanggang7
     * @time 2020-09-09 16:33:53 周三
     */
    private void assembleWaybillCancelModel(CancelWaybill cancelWaybill, WaybillSiteTrackMq waybillSiteTrackMq){
        Date now = new Date();
        long currentTimeMillis = System.currentTimeMillis();
        cancelWaybill.setWaybillCode(waybillSiteTrackMq.getWaybillCode());
        cancelWaybill.setCreateTime(now.toString());
        cancelWaybill.setUpdateTime(now.toString());
        // 理赔拦截后可换单通知
        cancelWaybill.setBusinessType(CancelWaybill.BUSINESS_TYPE_LOCK);
        cancelWaybill.setOperateTime(waybillSiteTrackMq.getDispatchTime());
        cancelWaybill.setYn(Constants.YN_YES);
        cancelWaybill.setFeatureType(CancelWaybill.FEATURE_TYPE_INTERCEPT_LP);
        cancelWaybill.setInterceptType(WaybillCancelInterceptTypeEnum.CLAIM_DAMAGED.getCode());
        cancelWaybill.setInterceptMode(WaybillCancelInterceptModeEnum.NOTICE.getCode());
        cancelWaybill.setOperateTimeOrder(currentTimeMillis);
    }

	@Override
	public boolean isFullOrderFail(String waybillCode) {
		CancelWaybill cancelWaybill = this.cancelWaybillDao.getByWaybillCodeAndInterceptType(waybillCode, WaybillCancelInterceptTypeEnum.FULL_ORDER_FAIL.getCode());
		return cancelWaybill!=null;
	}

	@Override
    @JProfiler(jKey = "DMSWEB.WaybillCancelServiceImpl.updateByWaybillCodeInterceptType99", jAppName= Constants.UMP_APP_NAME_DMSWORKER, mState={JProEnum.TP, JProEnum.FunctionError})
    public int updateByWaybillCodeInterceptType99(String waybillCode){
        return cancelWaybillDao.updateByWaybillCodeInterceptType99(waybillCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillCancelServiceImpl.delByWaybillCodeListInterceptType99", jAppName= Constants.UMP_APP_NAME_DMSWORKER, mState={JProEnum.TP, JProEnum.FunctionError})
    public Result<Integer> delByWaybillCodeListInterceptType99(List<String> waybillCodeList){
        log.info("WaybillCancelServiceImpl.delByWaybillCodeListInterceptType99 param {}", JSON.toJSONString(waybillCodeList));
        Result<Integer> result = Result.success();
        try {
            if (waybillCodeList == null) {
                return result.toFail("入参为空");
            }
            if (waybillCodeList.size() > 500) {
                return result.toFail("一次最多更新500条");
            }
            final int updateCount = cancelWaybillDao.delByWaybillCodeListInterceptType99(waybillCodeList);
            result.setData(updateCount);
        } catch (Exception e) {
            log.error("WaybillCancelServiceImpl.delByWaybillCodeListInterceptType99 exception {}", JSON.toJSONString(waybillCodeList), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 按运单号校验是否存在异常运单拦截
     * @param waybillCode 运单号
     * @return 校验结果 true-有拦截 false-无
     */
    @Override
    public boolean checkWaybillCancelInterceptType99(String waybillCode) {
        try {
            List<CancelWaybill> cancelWaybills = cancelWaybillDao.getByWaybillCode(waybillCode);
            if (cancelWaybills == null || cancelWaybills.isEmpty()) {
                return false;
            }
            boolean hasIntercept = false;
            for (CancelWaybill cancelWaybill : cancelWaybills) {
                if(java.util.Objects.equals(WaybillCancelInterceptTypeEnum.CUSTOM_INTERCEPT.getCode(), cancelWaybill.getInterceptType())){
                    hasIntercept = true;
                    break;
                }
            }
            log.info("checkWaybillCancelInterceptType99 waybillCode: {} hasIntercept: {} ", waybillCode, hasIntercept);
            return hasIntercept;
        } catch (Exception e) {
            log.error("checkWaybillCancelInterceptType99 exception waybillCode: {}", waybillCode, e);
        }
        return false;
    }
}