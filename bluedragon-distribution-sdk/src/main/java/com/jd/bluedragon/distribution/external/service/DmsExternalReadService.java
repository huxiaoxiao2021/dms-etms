package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.PackageSummaryRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.DmsBaseResponse;
import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.send.domain.SendDSimple;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import com.jd.bluedragon.distribution.wss.dto.BoxSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.DepartureWaybillDto;
import com.jd.bluedragon.distribution.wss.dto.PackageSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleSummaryDto;
import com.jd.bluedragon.distribution.wss.dto.WaybillCodeSummatyDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * 根据批次获取发货包裹信息
     * */
    public PageDto<List<PackageSummaryDto>> queryPagePackageSummaryByBatchCode(PageDto<PackageSummaryRequest> pageDto);

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
	public List<WaybillCodeSummatyDto> findDeliveryPackageByCodeSummary(int siteid, String waybillCode);


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

	/**
	 * 根据箱号 查询箱子信息
	 * @param BoxCode
	 * @return
	 */
	public BoxResponse getBoxInfoByCode(String boxCode);

	/**
	 * 根据原单号获取对应的新单号
	 * 1.自营拒收：新运单规则：T+原运单号。调取运单来源：从运单处获取，调取运单新接口。
	 * 2.外单拒收：新运单规则：生成新的V单。调取运单来源：1）从外单获得新外单单号。2）通过新外单单号从运单处调取新外单的信息。
	 * 3.售后取件单：新运单规则：生成W单或VY单。调取运单来源：从运单处获取，调取运单新接口。
	 * 4.配送异常类订单：新运单规则：T+原运单号,调取运单来源：从运单处获得，调取运单新接口。
	 * 5.返单换单：1）新运单规则：F+原运单号  或  F+8位数字,调取运单来源：从运单处获得，调取运单新接口。2）分拣中心集中换单，暂时不做。
	 *
	 * @param oldWaybillCode 原单号
	 * @return
	 */
	InvokeResult<String> getNewWaybillCode(String oldWaybillCode);

	/**
	 * 根据批次号列表获取发货的简单信息
	 * @param sendCodes 批次号列表
	 * @return key:批次号 value:批次号对应的发货信息
	 */
	DmsBaseResponse<Map<String,List<SendDSimple>>> getSendDSimpleBySendCodes(List<String>sendCodes);
}