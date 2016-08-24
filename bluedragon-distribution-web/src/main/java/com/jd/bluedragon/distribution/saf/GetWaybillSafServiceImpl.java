package com.jd.bluedragon.distribution.saf;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.saf.domain.WaybillResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.saf.service.GetWaybillSafService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.distribution.saf.domain.WaybillSafResponse;
import org.springframework.stereotype.Service;

@Service("getWaybillSafService")
public class GetWaybillSafServiceImpl implements GetWaybillSafService{
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private SortingService sortingService;
	
	@Autowired
    private SendMDao sendMDao;

    @Autowired
    private SendDatailDao sendDatailDao;
    
    private static final int OPERATE_TYPE_CANCEL_L = 0;
	 
    /**
     * 通过箱号获取包裹信息
     * */
	@Override
	public WaybillSafResponse<List<WaybillResponse>> getOrdersDetails(
			String boxCode) {
		if(boxCode ==null || boxCode.trim().equals("")){
			logger.error("boxCode的参数为空");
			return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_PARAM_ERROR,WaybillSafResponse.MESSAGE_PARAM_ERROR);
		}
		List<WaybillResponse> responseList = new ArrayList<WaybillResponse>();
		try {
			Sorting sorting = new Sorting();
			sorting.setBoxCode(boxCode);
			List<Sorting> list = sortingService.findOrderDetail(sorting);
			if(list!=null && !list.isEmpty()){
				for(Sorting sort :list){
					WaybillResponse  response = new WaybillResponse();
					response.setBoxCode(sort.getBoxCode());
					response.setPackageCode(sort.getPackageCode());
					response.setWaybillCode(sort.getWaybillCode());
					responseList.add(response);
				}
			}else{
				logger.error("boxCode获取的数据为空"+boxCode);
				return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_OK_NULL,WaybillSafResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			logger.error("boxCode获取异常",e);
			return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_SERVICE_ERROR,WaybillSafResponse.MESSAGE_SERVICE_ERROR);
		}
		return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_OK,WaybillSafResponse.MESSAGE_OK,responseList);
	}

	 /**
     * 通过批次号获取包裹信息
     * */
	@Override
	public WaybillSafResponse<List<WaybillResponse>> getPackageCodesBySendCode(
			String sendCode) {
		if(sendCode ==null || sendCode.trim().equals("")){
			logger.error("sendCode的参数为空");
			return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_PARAM_ERROR,WaybillSafResponse.MESSAGE_PARAM_ERROR);
		}
		List<WaybillResponse> responseList = new ArrayList<WaybillResponse>();
		try {
			SendM sendM = new SendM();
			sendM.setSendCode(sendCode);
			List<SendM> tSendM = this.sendMDao.selectBySendSiteCode(sendM);

			SendDetail tSendDatail = new SendDetail();
			List<SendDetail> sendDatailList= null;
			
			for (SendM newSendM : tSendM) {
				if(BusinessHelper.isBoxcode(newSendM.getBoxCode())){
					tSendDatail.setBoxCode(newSendM.getBoxCode());
					tSendDatail.setCreateSiteCode(newSendM.getCreateSiteCode());
					tSendDatail.setReceiveSiteCode(newSendM.getReceiveSiteCode());
					tSendDatail.setIsCancel(OPERATE_TYPE_CANCEL_L);
					sendDatailList = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);
					if(sendDatailList!=null && !sendDatailList.isEmpty()){
						for(SendDetail send :sendDatailList){
							WaybillResponse  response = new WaybillResponse();
							response.setPackageCode(send.getPackageBarcode());
							response.setWaybillCode(send.getWaybillCode());
                            response.setBoxCode(send.getBoxCode());  //王显群需要box_code
							responseList.add(response);
						}
					}
				}else{
					WaybillResponse  response = new WaybillResponse();
					response.setPackageCode(newSendM.getBoxCode());
					response.setWaybillCode(BusinessHelper.getWaybillCode(newSendM.getBoxCode()));
					responseList.add(response);
				}
			}
			
		} catch (Exception e) {
			logger.error("sendCode获取异常",e);
			return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_SERVICE_ERROR,WaybillSafResponse.MESSAGE_SERVICE_ERROR);
		}
		return new WaybillSafResponse<List<WaybillResponse>>(WaybillSafResponse.CODE_OK,WaybillSafResponse.MESSAGE_OK,responseList);
	}
	
}
