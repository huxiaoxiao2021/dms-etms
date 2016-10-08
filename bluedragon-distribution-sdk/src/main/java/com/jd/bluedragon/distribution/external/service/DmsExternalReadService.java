package com.jd.bluedragon.distribution.external.service;


import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.bluedragon.distribution.wss.dto.*;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;

public interface DmsExternalReadService {

	/**
	 * 根据箱号获得对应运单号
	 * @param boxCode 箱号
	 * @return 运单号列表
	 */
	public abstract List<String> findWaybillByBoxCode(String boxCode);


	/**
	 * 根据运单号，获取批次号，箱号，包裹号，创建站点编号，接收站点编号，操作时间
	 * @param waybillCode 运单号
	 * @return
	 */
	public abstract List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode);


	///////////////////////////DistributionWssService////////////////////
	/**
	 * 根据批次号或者箱号(包裹号)获取发货箱子信息
	 * @param code 批次号或者箱号(包裹号）
	 * @param type 1:批次号，2：箱号（包裹号）
	 * @param siteCode 如果code是包裹号，则sitecode是收货站点
	 * */
	public List<BoxSummaryDto> getBoxSummary(String code, Integer type, Integer siteCode);


	/**
	 * 根据批次号或者箱号获取发货包裹信息
	 * @param code 批次号或者箱号(包裹号）
	 * @param type 1:批次号，2：箱号（包裹号）
	 * @param siteCode 如果code是包裹号，则sitecode是收货站点
	 * */
	public List<PackageSummaryDto> getPackageSummary(String code, Integer type, Integer siteCode);

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
	public List<WaybillCodeSummatyDto> findDeliveryPackageBySiteSummary(int siteid,Date startTime,Date endTime);

	/**
	 * 根据运单号获取发货包裹数
	 * @param 参数 （int siteid,String waybillCode）
	 * @return
	 */
	public List<WaybillCodeSummatyDto> findDeliveryPackageByCodeSummary(int siteid,String waybillCode);


	/**
	 * 根据封车号或者三方运单号获取发车运单信息
	 * @param code 封车号或者三方运单号
	 * @param type 1:封车号，2：三方运单号
	 * */
	public List<DepartureWaybillDto> getWaybillsByDeparture(String code, Integer type);


	//////////////////////EmsOrderJosSafService//////////////////////
	/**
	 * 根据运单号获取发货信息
	 * @param waybillCode 运单号
	 * @return
	 * */
	public WaybillInfoResponse getWaybillInfo(String waybillCode);

	////////////////////////OrdersResourceSafService//////////////////////
	/**
	 * 根据箱号、创建时间，更新时间、创建站点和收货站点获取分拣记录
	 * @param boxCode 箱号
	 * @param startTime 创建时间
	 * @param endTime 更新时间
	 * @param createSiteCode 创建站点
	 * @param receiveSiteCode 收货站点
	 * @return
	 * */
	public OrderDetailEntityResponse getOrdersDetails(String boxCode,
													  String startTime, String endTime, String createSiteCode,
													  String receiveSiteCode);

	///////////////////////////WaybillSafService/////////////////////
	/**
	 * PDA POST请求查询锁定订单信息 参数PdaOperateRequest(属性:getPackageCode)
	 返回值 WaybillResponse(属性Integer:code String:message)

	 /waybills/cancel?packageCode=xx PDA GET请求查询锁定订单信息 参数包裹号（String）
	 返回值 WaybillResponse(属性Integer:code String:message)
	 锁定信息内容：取消订单，删除订单，锁定订单，退款100分，拦截订单
	 返回值 CODE范围 取消订单(29302)，删除订单(29302) ，锁定订单(29301) ，退款100分(29303) ，拦截订单(29305)
	 * @param packageCode
	 * @return
	 */
	public WaybillSafResponse isCancel(String packageCode);

//	//////////////////////////GetWaybillSafService////////////////////////
//	public WaybillSafResponse<List<WaybillResponse>> getOrdersDetailsByBoxcode(String boxCode);
//
//	public WaybillSafResponse<List<WaybillResponse>> getPackageCodesBySendCode(String sendCode);


}