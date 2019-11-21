package com.jd.bluedragon.distribution.fastRefund.service;

import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;

@Component
public class WaybillCancelClient {

    private static final Logger log = LoggerFactory.getLogger(WaybillCancelClient.class);
	/**
	 * DMSVER_ADDRESS,查询取消订单地址
	 */
	private final static String DMSVER_ADDRESS = PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS");
   
    private static final String URL = DMSVER_ADDRESS + "/services/waybills/cancel?packageCode=";

    private static final String SORTING_URL=DMSVER_ADDRESS+"/services/sorting/check?createSiteCode={0}&receiveSiteCode={1}&businessType=10&boxCode={2}&packageCode={3}";
    
    private static Integer MIN_VALID_CODE = 29300;

    public static boolean isWaybillCancel(String waybillCode) {

        try {
            ClientRequest request = new ClientRequest(URL + waybillCode);
            request.accept(MediaType.APPLICATION_JSON);

            ClientResponse<WaybillResponse> response = request.get(WaybillResponse.class);
            if (200 == response.getStatus()) {
            	WaybillResponse result =  response.getEntity();
            	if(result.getCode()> MIN_VALID_CODE){
            		return true;
            	}
            }
            return false;
        } catch (Exception e) {
            log.error("获取取消订单状态[{}]信息失败,默认返回为true",waybillCode, e);
            return true;
        }
    }
    
    /**
     * 根据运单号获取运单是否取消锁定相关信息
     * 
     * @param waybillCode
     * @return
     */
    public static WaybillResponse getWaybillResponse(String waybillCode) {
    	if (waybillCode == null || "".equals(waybillCode.trim())) {
    		return null;
    	}
    	try {
            ClientRequest request = new ClientRequest(URL + waybillCode);
            request.accept(MediaType.APPLICATION_JSON);

            ClientResponse<WaybillResponse> response = request.get(WaybillResponse.class);
            if (200 == response.getStatus()) {
            	return response.getEntity();
            	
            }
        } catch (Exception e) {
            log.error("获取运单是否取消锁定相关信息【{}】信息失败,异常为：",waybillCode, e);
        }
        return null;
    }


    /**
     * 自动分拣机分拣拦截验证
     * @param createSiteCode    始发分拣中心
     * @param packageCode       包裹号
     * @param receiveSiteCode   目的站点（或目的分拣中心）
     * @return
     */
    public static BoxResponse checkAutoSorting(Integer createSiteCode, String packageCode,Integer receiveSiteCode){

        String requestUrl= MessageFormat.format(SORTING_URL, String.valueOf(createSiteCode), String.valueOf(receiveSiteCode), packageCode, packageCode);
        ClientRequest request = new ClientRequest(requestUrl);

        log.debug(requestUrl);
        request.accept(MediaType.APPLICATION_JSON);

        ClientResponse<BoxResponse> response =null;
        try {
            response = request.get(BoxResponse.class);
        }catch (Exception ex) {
            BoxResponse boxResponse=new BoxResponse();
            boxResponse.setCode(-500);
            boxResponse.setMessage(ex.getMessage());
            return boxResponse;
        }
        if (200 == response.getStatus()) {
            return response.getEntity();
		} else {
            BoxResponse boxResponse=new BoxResponse();
            boxResponse.setCode(response.getStatus()-100000);
            boxResponse.setMessage("HTTP访问结果非200");
            return boxResponse;
        }
    }
}
