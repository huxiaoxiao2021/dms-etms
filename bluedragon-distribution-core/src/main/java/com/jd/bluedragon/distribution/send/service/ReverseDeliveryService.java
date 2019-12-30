package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.util.List;

public interface ReverseDeliveryService {

    /**
     * 查找发货数据上传TMST信息
     * 
     * @param
     */
    public boolean findsendMToReverse(Task task) throws Exception;

    /**
	 * 更新运单状态
	 * @return
	 */
	public void updateIsCancelToWaybillByBox(SendM tSendM,List<SendDetail> tlist);
	
	/**
	 * 更新m表和d表
	 * @return
	 */
	public void updateIsCancelByBox(SendM tSendM);
	/**
	 * 更新m表和d表
	 * @return
	 */
	public void updateIsCancelByPackageCode(SendM tSendM,SendDetail tSendDatail);
	
	/**
	 * 更新运单状态
	 * @return
	 */
	public void updateIsCancelToWaybillByPackageCode(SendM tSendM,SendDetail tSendDatail);
	
	/**
	 * 武汉邮政订单拉取接口
	 * @return
	 */
	public WhemsWaybillResponse getWhemsWaybill(List<String> wlist);
	
	/**
	 * 武汉邮政订单推送接口
	 * @return
	 */
	void pushWhemsWaybill(String waybillCode);
	
	/**
	 * 全国邮政订单推送接口
	 * @return
	 */
	public WaybillInfoResponse getEmsWaybillInfo(String waybillCode);
	
	public void toEmsServer(List<String> waybillList);
	
	/**
	 * 快生获取订单信息
	 * @return
	 */
	public BigWaybillDto getWaybillQuickProduce(String waybillCode);
}
