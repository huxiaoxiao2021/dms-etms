package com.jd.bluedragon.distribution.web.globaltrade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CrossSortingRequest;
import com.jd.bluedragon.distribution.api.request.LoadBillReportRequest;
import com.jd.bluedragon.distribution.api.request.LoadBillReportResponse;
import com.jd.bluedragon.distribution.api.request.LoadBillRequest;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.jsf.gd.util.StringUtils;

@Controller
@RequestMapping("/globalTrade")
public class GlobalTradeController {

    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private LoadBillService loadBillService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            ErpUser erpUser = ErpUserClient.getCurrUser();
            model.addAttribute("erpUser", erpUser);
        } catch (Exception e) {
            logger.error("index error!", e);
        }
        return "globaltrade/global-trade-index";
    }
    
    @RequestMapping(value = "/loadBill/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<Pager<List<LoadBill>>> doQueryLoadBill(LoadBillRequest request, Pager<List<LoadBill>> pager) {
        CommonDto<Pager<List<LoadBill>>> cdto = new CommonDto<Pager<List<LoadBill>>>();
        try {
            logger.info("GlobalTradeController doQueryLoadBill begin...");
            if (null == request || StringUtils.isNotBlank(request.getSendCode())
            		            || request.getDmsCode() == null
            		            || request.getDmsCode() < 1
            		            || request.getSendTimeFrom() == null
            		            || request.getSendTimeTo() == null) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            Map<String, Object> params = this.getParamsFromRequest(request);
            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<LoadBill>>(Pager.DEFAULT_PAGE_NO);
            } else {
                pager = new Pager<List<LoadBill>>(pager.getPageNo(), pager.getPageSize());
            }
            params.putAll(ObjectMapHelper.makeObject2Map(pager));

            List<LoadBill> loadBillList = loadBillService.findPageLoadBill(params);
            Integer totalSize = loadBillService.findCountLoadBill(params);
            pager.setTotalSize(totalSize);
            pager.setData(loadBillList);
            logger.info("查询符合条件的规则数量：" + totalSize);

            cdto.setData(pager);
            cdto.setCode(CommonDto.CODE_NORMAL);
        } catch (Exception e) {
            logger.error("doQueryLoadBill-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }
    
    private Map<String, Object> getParamsFromRequest(LoadBillRequest request) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("sendCode", request.getSendCode());
    	params.put("dmsCode", request.getDmsCode());
    	params.put("approvalCode", request.getApprovalCode());
    	params.put("sendTimeFrom", request.getSendTimeFrom());
    	params.put("sendTimeTo", request.getSendTimeTo());
        return params;
	}

	@RequestMapping(value = "/loadBill/initial", method = RequestMethod.GET)
    @ResponseBody
    public CommonDto<String> initialLoadBill(LoadBillRequest request){
		CommonDto<String> cdto = new CommonDto<String>();
		try {
            logger.info("GlobalTradeController initialLoadBill begin with sendCode is " + request.getSendCode());
            if (null == request || StringUtils.isNotBlank(request.getSendCode())) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            ErpUser erpUser = ErpUserClient.getCurrUser();
            if(erpUser != null){
            	loadBillService.initialLoadBill(request.getSendCode(), erpUser.getUserId(), erpUser.getUserName());
            } else {
            	loadBillService.initialLoadBill(request.getSendCode(), null, null);
            }
            cdto.setCode(CommonDto.CODE_NORMAL);
        } catch (Exception e) {
            logger.error("initialLoadBill-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
		return cdto;
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
