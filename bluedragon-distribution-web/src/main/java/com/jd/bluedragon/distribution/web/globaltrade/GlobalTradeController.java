package com.jd.bluedragon.distribution.web.globaltrade;

import com.jd.bluedragon.utils.StringHelper;
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

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/globalTrade")
public class GlobalTradeController {

    private final Log logger = LogFactory.getLog(this.getClass());
    private static final String CARCODE_REG = "[A-Za-z0-9\u4e00-\u9fa5]+";
    
    @Autowired
    private LoadBillService loadBillService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(){
        return "globaltrade/global-trade-index";
    }

    @RequestMapping(value = "/preload", method = RequestMethod.POST)
    public LoadBillReportResponse prepareLoadBill(HttpServletRequest request){
        String carCode = request.getParameter("carCode");
        String waybillCodes = request.getParameter("waybillCodes");
        if(StringHelper.isEmpty(carCode) || !Pattern.matches(CARCODE_REG,carCode)){
            return new LoadBillReportResponse(1000,"车次号不符合要求");
        }
        if(StringHelper.isEmpty(waybillCodes)){
            return new LoadBillReportResponse(2000,"订单号不能为空");
        }

        return new LoadBillReportResponse(200,"预装载成功");
    }
    
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
