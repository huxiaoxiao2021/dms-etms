package com.jd.bluedragon.distribution.external.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.external.service.DmsExternalReadService;
import com.jd.bluedragon.distribution.saf.EmsOrderJosSafService;
import com.jd.bluedragon.distribution.saf.OrdersResourceSafService;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.saf.WaybillSafService;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.bluedragon.distribution.wss.dto.BoxSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.DepartureWaybillDto;
import com.jd.bluedragon.distribution.wss.dto.PackageSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.WaybillCodeSummatyDto;
import com.jd.bluedragon.distribution.wss.service.DistributionWssService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

@Service("dmsExternalReadService")
public class DmsExternalReadServiceImpl implements DmsExternalReadService {
	
	private final Logger logger = Logger.getLogger(DeliveryServiceImpl.class);
	
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

	/* (non-Javadoc)
	 * @see com.jd.bluedragon.distribution.external.service.DmsExternalService#findWaybillByBoxCode(java.lang.String)
	 */
	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findWaybillByBoxCode", mState = { JProEnum.TP })
	public List<String> findWaybillByBoxCode(String boxCode) {
        try {
            Integer createSiteCode = kvIndexDao.queryOneByKeyword(boxCode);
            if (createSiteCode != null) {
                return sendDatailReadDao.findWaybillByBoxCode(boxCode, createSiteCode);
            }
        } catch (Exception e) {
            this.logger.error("根据箱号获得运单号异常boxCode:" + boxCode, e);
        }
        return Collections.emptyList();
    }


	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalReadServiceImpl.findSendBoxByWaybillCode", mState = {JProEnum.TP})
	public List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode) {
		List<SendBoxDetailResponse> sendBoxList = null;
		try {
			sendBoxList = sendDatailReadDao.findSendBoxByWaybillCode(waybillCode);
		} catch (Exception e) {
			this.logger.error("根据箱号获得批次号,箱号,包裹号异常 waybillCode:"+waybillCode, e);
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
