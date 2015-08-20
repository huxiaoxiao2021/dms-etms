package com.jd.bluedragon.distribution.web.globaltrade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoadBillReportRequest;
import com.jd.bluedragon.distribution.api.request.LoadBillReportResponse;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.jsf.gd.util.StringUtils;

@Controller
@RequestMapping("/globalTrade")
public class GlobalTradeController {

    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private LoadBillService loadBillService;
    
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    @ResponseBody
    public LoadBillReportResponse updateLoadBillStatus(LoadBillReportRequest request){
    	LoadBillReportResponse response = new LoadBillReportResponse(1, JdResponse.MESSAGE_OK);
    	try{
        	if(request == null || StringUtils.isBlank(request.getReportId()) 
        			|| StringUtils.isBlank(request.getLoadId()) 
        			|| StringUtils.isBlank(request.getOrderId()) 
        			|| StringUtils.isBlank(request.getLoadId())
        			|| StringUtils.isBlank(request.getWarehouseId())
        			|| request.getStatus() == null
        			|| request.getStatus() < 0){
        		return new LoadBillReportResponse(2, "参数错误");
        	}
        	//对orderId进行进一步校验和组装
        	String[] orderIdArray = request.getOrderId().split(",");
        	if(orderIdArray.length == 0){
        		return new LoadBillReportResponse(2, "orderId数量为0");
        	}        	
        	loadBillService.updateLoadBillStatusByReport(resolveLoadBillReport(request, orderIdArray));
    	}catch(Exception e){
    		response = new LoadBillReportResponse(2, "操作异常");
    		logger.error("GlobalTradeController 发生异常,异常信息 : ", e);
    	}
    	return response;
    }

	private LoadBillReport resolveLoadBillReport(LoadBillReportRequest request, String[] orderIdArray) {
		LoadBillReport report = new LoadBillReport();
		report.setReportId(request.getReportId());
		report.setLoadId(request.getLoadId());
		report.setProcessTime(request.getProcessTime());
		report.setStatus(request.getStatus());
		report.setWarehouseId(request.getWarehouseId());
		report.setNotes(request.getNotes());
		report.setOrderId(getOrderId(orderIdArray));
		return report;
	}

	private String getOrderId(String[] orderIdArray) {
		boolean first = true;
		String orderId = "";
		for(int i = 0, len = orderIdArray.length; i < len; i++){
			if(first){
				first = false;
				orderId += orderIdArray[i];
			}else{
				orderId += "," + orderIdArray[i];
			}
		}
		return orderId;
	}
    
}
