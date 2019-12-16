package com.jd.bluedragon.distribution.external.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.DmsBaseResponse;
import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.external.service.DmsExternalReadService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.saf.EmsOrderJosSafService;
import com.jd.bluedragon.distribution.saf.OrdersResourceSafService;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.saf.WaybillSafService;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDSimple;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.bluedragon.distribution.wss.dto.*;
import com.jd.bluedragon.distribution.wss.service.DistributionWssService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("dmsExternalReadService")
public class DmsExternalReadServiceImpl implements DmsExternalReadService {
	
	private final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);
	
	@Autowired
	private SendDatailReadDao sendDatailReadDao;
	
	@Autowired
	private KvIndexDao kvIndexDao;

	@Autowired
	private DistributionWssService distributionService;

	@Autowired
	private WaybillSafService waybillSafService;

	@Autowired
	private EmsOrderJosSafService emsOrderJosSafService;

	@Autowired
	private OrdersResourceSafService ordersResourceSafService;

	@Autowired
	private CrossBoxService crossBoxService;

	@Autowired
	private ReversePrintService reversePrintService;

    @Value("${jsf.dmsExternal.pageLimit}")
	private int pageLimit;

	/* (non-Javadoc)
	 * @see com.jd.bluedragon.distribution.external.service.DmsExternalService#findWaybillByBoxCode(java.lang.String)
	 */
	@Override
    @JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findWaybillByBoxCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public List<String> findWaybillByBoxCode(String boxCode) {
		CallerInfo info = Profiler.registerInfo("DMSWEB.DmsExternalReadServiceImpl.findWaybillByBoxCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            Integer createSiteCode = kvIndexDao.queryOneByKeyword(boxCode);
            if (createSiteCode != null) {
                return sendDatailReadDao.findWaybillByBoxCode(boxCode, createSiteCode);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            this.log.error("根据箱号获得运单号异常boxCode:{}", boxCode, e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return Collections.emptyList();
    }


	@Override
    @JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findSendBoxByWaybillCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.DmsExternalReadServiceImpl.findSendBoxByWaybillCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
		List<SendBoxDetailResponse> sendBoxList = null;
		try {
			sendBoxList = sendDatailReadDao.findSendBoxByWaybillCode(waybillCode);
		} catch (Exception e) {
            Profiler.functionError(info);
			this.log.error("根据箱号获得批次号,箱号,包裹号异常 waybillCode:{}", waybillCode, e);
		}finally {
            Profiler.registerInfoEnd(info);
        }
		return sendBoxList;
	}


	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getBoxSummary", mState = {JProEnum.TP})
	public List<BoxSummaryDto> getBoxSummary(String code, Integer type, Integer siteCode) {
		return distributionService.getBoxSummary(code, type, siteCode);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getPackageSummary", mState = {JProEnum.TP})
	public List<PackageSummaryDto> getPackageSummary(String code, Integer type, Integer siteCode) {
		return distributionService.getPackageSummary(code, type, siteCode);
	}

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.queryPagePackageSummaryByBatchCode", mState = {JProEnum.TP})
    public InvokeResult<PageDto<PackageSummaryDto>> queryPagePackageSummaryByBatchCode(PageDto<PackageSummaryDto> pageDto,String batchCode) {
        InvokeResult<PageDto<PackageSummaryDto>> invokeResult = new InvokeResult<>();
	    if(StringUtils.isEmpty(batchCode)){
            invokeResult.parameterError("批次号不能为空");
            return invokeResult;
        }
        if(pageDto == null){
            invokeResult.parameterError("分页参数不能为空");
            return invokeResult;
        }
        if(pageDto.getPageSize() < 0 || pageDto.getPageSize() > pageLimit){
            invokeResult.parameterError("pageSize参数必须大于0并且必须小于"+pageLimit);
            return invokeResult;
        }
        if(pageDto.getCurrentPage() < 0){
            invokeResult.parameterError("currentPage参数必须大于0");
            return invokeResult;
        }
        PageDto<PackageSummaryDto> resultPage = distributionService.queryPageSendInfoByBatchCode(pageDto,batchCode);
        invokeResult.success();
        invokeResult.setData(resultPage);
        return invokeResult;
    }

    @Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findSealByCodeSummary", mState = {JProEnum.TP})
	public SealVehicleSummaryDto findSealByCodeSummary(String sealCode) {
		return distributionService.findSealByCodeSummary(sealCode);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findDeliveryPackageBySiteSummary", mState = {JProEnum.TP})
	public List<WaybillCodeSummatyDto> findDeliveryPackageBySiteSummary(int siteid, Date startTime, Date endTime) {
		return distributionService.findDeliveryPackageBySiteSummary(siteid, startTime, endTime);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findDeliveryPackageByCodeSummary", mState = {JProEnum.TP})
	public List<WaybillCodeSummatyDto> findDeliveryPackageByCodeSummary(int siteid, String waybillCode) {
		return distributionService.findDeliveryPackageByCodeSummary(siteid, waybillCode);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getWaybillsByDeparture", mState = {JProEnum.TP})
	public List<DepartureWaybillDto> getWaybillsByDeparture(String code, Integer type) {
		return distributionService.getWaybillsByDeparture(code, type);
	}


	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getWaybillInfo", mState = {JProEnum.TP})
	public WaybillInfoResponse getWaybillInfo(String waybillCode) {
		return emsOrderJosSafService.getWaybillInfo(waybillCode);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getOrdersDetails", mState = {JProEnum.TP})
	public OrderDetailEntityResponse getOrdersDetails(String boxCode, String startTime, String endTime, String createSiteCode, String receiveSiteCode) {
		return ordersResourceSafService.getOrdersDetails(boxCode, startTime, endTime, createSiteCode, receiveSiteCode);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.isCancel", mState = {JProEnum.TP})
	public WaybillSafResponse isCancel(String packageCode) {
		return waybillSafService.isCancel(packageCode);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getBoxInfoByCode", mState = {JProEnum.TP})
	public BoxResponse getBoxInfoByCode(String boxCode) {
		return crossBoxService.getCrossDmsBoxByBoxCode(boxCode);
	}

	@Override
    @JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.getNewWaybillCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public InvokeResult<String> getNewWaybillCode(String oldWaybillCode) {
		InvokeResult<String> invokeResult = new InvokeResult<String>();
		try {
			com.jd.bluedragon.distribution.base.domain.InvokeResult<String> result = reversePrintService.getNewWaybillCode(oldWaybillCode, true);
			if (result != null) {
				invokeResult.setCode(result.getCode());
				invokeResult.setMessage(result.getMessage());
				invokeResult.setData(result.getData());
			}
		} catch (Throwable e) {
			log.error("[分拣中心外部JSF服务][逆向换单获取新单号]，旧单号：{}", oldWaybillCode, e);
			invokeResult.error(e);
		}
		return invokeResult;
	}


	/**
	 * 根据批次号列表获取发货的简单信息
	 * @param sendCodes 批次号列表
	 * @return key:批次号 value:批次号对应的发货信息
	 */
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey="DMSWEB.DmsExternalReadServiceImpl.getSendDSimpleBySendCodes", mState = {JProEnum.TP})
	public DmsBaseResponse<Map<String,List<SendDSimple>>> getSendDSimpleBySendCodes(List<String>sendCodes){
		if(log.isInfoEnabled()) {
			log.info("根据批次号列表获取发货明细的简单信息，参数：{}", JSON.toJSONString(sendCodes));
		}
		DmsBaseResponse<Map<String,List<SendDSimple>>> response = new DmsBaseResponse<Map<String, List<SendDSimple>>>(DmsBaseResponse.CODE_SUCCESS,DmsBaseResponse.MESSAGE_SUCCESS);
		Map<String,List<SendDSimple>> data = new HashMap<String, List<SendDSimple>>();

		//如果传的参数为空
		if(sendCodes == null || sendCodes.size() < 1){
			response.setCode(DmsBaseResponse.CODE_FAILED);
			response.setMessage(DmsBaseResponse.MESSAGE_FAILED_NO_PARAMS);
			return response;
		}

		for(String sendCode : sendCodes){
			List<SendDSimple> sendDSimpleList = new ArrayList<SendDSimple>();
			//通过批次号获取始发站点
			Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
			//如果无法解析始发站点，放入一个空的列表，返回
			if(createSiteCode == null){
				log.debug("根据批次号列表获取发货明细的简单信息,无法根据批次号获取始发分拣中心.sendCode:{}", sendCode);
				data.put(sendCode,sendDSimpleList);
				continue;
			}

			//查询sendD
			List<SendDetail> sendDetailList = sendDatailReadDao.querySendDSimpleInfoBySendCode(sendCode,createSiteCode);

			//循环将SendDetail对象转换为SendDSimple对象
			for(SendDetail sendDetail : sendDetailList){
				SendDSimple sendDSimple = new SendDSimple();
				sendDSimple.setSendCode(sendDetail.getSendCode());
				sendDSimple.setWaybillCode(sendDetail.getWaybillCode());
				sendDSimple.setPackageBarCode(sendDetail.getPackageBarcode());
				sendDSimple.setCreateSiteCode(sendDetail.getCreateSiteCode());
				sendDSimple.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
                sendDSimple.setCreateUserCode(sendDetail.getCreateUserCode());
				sendDSimple.setCreateUserName(sendDetail.getCreateUser());
				sendDSimpleList.add(sendDSimple);
			}
			data.put(sendCode,sendDSimpleList);
		}

		response.setData(data);
		return response;
	}

//
//	@Override
//	public WaybillSafResponse<List<WaybillResponse>> getOrdersDetailsByBoxcode(String boxCode) {
//		return getWaybillSafService.getOrdersDetails(boxCode);
//	}
//
//	@Override
//	public WaybillSafResponse<List<WaybillResponse>> getPackageCodesBySendCode(String sendCode) {
//		return getWaybillSafService.getPackageCodesBySendCode(sendCode);
//	}

}
