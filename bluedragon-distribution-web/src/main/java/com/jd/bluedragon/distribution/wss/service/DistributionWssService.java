package com.jd.bluedragon.distribution.wss.service;

import com.jd.bluedragon.distribution.departure.domain.SendBox;
import com.jd.bluedragon.distribution.wss.dto.BoxSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.DepartureWaybillDto;
import com.jd.bluedragon.distribution.wss.dto.PackageSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.WaybillCodeSummatyDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.Date;
import java.util.List;

public interface DistributionWssService {

	public List<BoxSummaryDto> getBoxSummary(String code, Integer type, Integer siteCode);
	public List<PackageSummaryDto> getPackageSummary(String code, Integer type, Integer siteCode);

    PageDto<PackageSummaryDto> queryPageSendInfoByBatchCode(PageDto pageDto, String batchCode);

	/**
	 * 通过封车号查询封车信息
	 * @param sealCode
	 * @return
	 */
	public SealVehicleSummaryDto findSealByCodeSummary(String sealCode);
	
	/**
	 *  根据站点，发货开始时间，发货结束时间   获取这段时间发货数据 （发送运单，对应包裹数）
	 * @param 参数 （int siteid,Date startTime,Date endTime）
	 * @return
	 */
	public List<WaybillCodeSummatyDto> findDeliveryPackageBySiteSummary(int siteid, Date startTime, Date endTime);
	
	/**
	 * 根据运单号获取发货包裹数
	 * @param 参数 （int siteid,String waybillCode）
	 * @return
	 */
	public List<WaybillCodeSummatyDto> findDeliveryPackageByCodeSummary(int siteid,String waybillCode);
	
	public List<DepartureWaybillDto> getWaybillsByDeparture(String code, Integer type);
}
