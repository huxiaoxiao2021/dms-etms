package com.jd.bluedragon.distribution.departure.service;

import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.distribution.api.request.DeparturePrintRequest;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.departure.domain.Departure;
import com.jd.bluedragon.distribution.departure.domain.DepartureCar;
import com.jd.bluedragon.distribution.departure.domain.DepartureSend;
import com.jd.bluedragon.distribution.departure.domain.SendBox;
import com.jd.bluedragon.distribution.departure.domain.SendMeasure;
import com.jd.bluedragon.distribution.receive.domain.SendCode;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

public interface DepartureService {
	
	/**
	 * 生成发货数据
	 * @param departure 发车相关数据
	 */
	ServiceMessage<String> createDeparture(Departure departure) throws Exception;
	
	/**
	 * 检查批次是否存在以及是否已经发车
	 * @param siteCode 发车相关数据
	 */
	public ServiceMessage<String> checkSendStatus (Integer siteCode, String sendCode);

	/**
	 * 从运输系统检查批次是否已经发车
	 * @param sendCode 批次号
	 */
	public ServiceMessage<Boolean> checkSendStatusFromVOS ( String sendCode);

	/**
	 * 根据发货单号获得体积和重量
	 * @param sendCode 交接单号
	 */
	SendMeasure getSendMeasure(Integer siteCode, String sendCode);
	
	/**
	 * 检查发货单中的车号以及箱号/包裹号是否匹配
	 * @param carCode 车号
	 * @param boxCode 箱号/包裹号
	 */
	boolean checkCarBoxCodeMatch(String carCode, String boxCode);
	
	/**
	 * 根据箱号获得箱内订单列表信息
	 * @param boxCode 箱号
	 * @return
	 */
	public List<SendBox> getSendBoxInfo(String boxCode, Integer siteCode);
	
	public void batchUpdateSendDMeasure(List<SendDetail> sendDatails);
	
	public List<SendBox> getSendInfo(String sendCode);

    /**
     * 根据批次号查询 发货明细
     * @param pageDto 分页参数
     * @param batchCode 批次号
     */
    PageDto<SendBox> queryPageSendInfoByBatchCode(PageDto<SendBox> pageDto, String batchCode);
	
	public List<SendCode> getSendCodesByWaybills(List<SendCode> sendCodes);

	/**
	 * 根据任务中记录的批次号和运单号(三方运单),转换成订单与运单的对应信息推送给全程跟踪
	 * @param task
	 * @return 成功返回true,失败返回false
	 */
	public boolean sendThirdDepartureInfoToTMS(Task task);
	
	public List<DepartureSend> getDepartureSendByCarId(Long departureCarId);
	public List<DepartureCar> findDepartureList(DeparturePrintRequest  departurPrintRequest);
	
	public List<SendDetail> getWaybillsByDeparture(String code, Integer type);
	
	/**
	 * 写入发车打印时间
	 * @param departureCarId 箱号
	 * @return
	 */
	public boolean updatePrintTime(long departureCarId);


    /**
     *  根据订单号获取发货信息
     *  @param ordercode 订单号
     *  @return
     * */
    public List<DeparturePrintResponse> queryDeliveryInfoByOrderCode(String ordercode);


	/**
	 *  根据批次号获取发车信息
	 *  @param sendCodes 多个批次号
	 *  @return
	 * */
	public List<DeparturePrintResponse> queryDepartureInfoBySendCode(List<String> sendCodes);


	/**
	 * 调用运单接口发送全称跟踪
	 * 运输车辆出发	1800
	       运输车辆到达 	1900
	 * */
	public void sendWaybillAddWS(BdTraceDto bdTraceDto); 
	
	public BdTraceDto toWaybillStatus(WaybillStatus tWaybillStatus);



    /**
     *  根据车次号获取干线计费信息
     *  @param carCode 车次号
     *  @return DeparturePrintResponse
     * */
    public DeparturePrintResponse queryArteryBillingInfo(long carCode);

    /**
     *  根据箱号获取干线计费信息
     *  @param boxCode 箱号号
     *  @return DeparturePrintResponse
     * */
    public DeparturePrintResponse queryArteryBillingInfoByBoxCode(String boxCode);


    /**
     *  从Task里面获取干线计费信息推MQ
     *
     *  @param  ArteryInfoTask 干线计费信息任务
     * */
    public boolean pushMQ2ArteryBillingSysByTask(Task ArteryInfoTask) throws Exception;


	/**
	 *  从Task表里面获取发车数据，组装之前发车临时表中的批次数据，进行发车
	 *
	 *  @param sendTask
	 *  @return boolean
	 * */
	public boolean dealDepartureTmpToSend(Task sendTask);

}
