package com.jd.bluedragon.distribution.rest.mq;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.UmpAlertRequest;
import com.jd.bluedragon.distribution.api.request.WmsOrderPackagesRequest;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PushMqResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private IPushPackageToMqService pushMqService;
    
    @POST
    @Path("/pushmq/pushmq")
    public JdResponse pushOrderToMq(WmsOrderPackagesRequest orderPackages) {
        this.logger.info("WMS库房调用DMS_WEB推送包裹信息到MQ");
        try{
        	pushMqService.pushPackageToMq(orderPackages);
        	return getSuccessResponse();
        }catch(Exception e){
        	this.logger.error("WMS库房调用DMS_WEB推送包裹信息到MQ_失败",e);
        	return getErrorResponse();
        }
    }
    
    @POST
    @Path("/pushump/pushump")
    public JdResponse pushUmpAlert(UmpAlertRequest umpAlertRequest) {
        this.logger.info("WMS库房调用DMS_WEB出发UMP自定义报警");
        try{
        	String key = umpAlertRequest.getKey();
        	String area = umpAlertRequest.getArea();
        	String time = umpAlertRequest.getTime();
        	String msg = umpAlertRequest.getMsg();
        	this.logger.info("WMS库房调用DMS_WEB出发UMP自定义报警:UMP-KEY[" + key + "],机房[" + area + "],推送时间[" + time + "],推送内容[" + msg + "]");
        	pushMqService.pushAlert(key,msg);
        	return getSuccessResponse();
        }catch(Exception e){
        	this.logger.error("WMS库房调用DMS_WEB出发UMP自定义报警_失败",e);
        	return getErrorResponse();
        }
    }

    @POST
    @Path("/pushump/package/handover")
    public JdResponse pushPackageHandoverUMP(UmpAlertRequest umpAlertRequest){
        this.logger.info("大福线自动分拣出发UMP自定义报警");
        try{
            String key = umpAlertRequest.getKey();
            String area = umpAlertRequest.getArea();
            String time = umpAlertRequest.getTime();
            String msg = umpAlertRequest.getMsg();
            this.logger.info("大福线自动分拣出发UMP自定义报警:UMP-KEY[" + key + "],分拣中心[" + area + "],推送时间[" + time + "],推送内容[" + msg + "]");
            pushMqService.pushAlert(key,msg);
            return getSuccessResponse();
        }catch(Exception e){
            this.logger.error("大福线自动分拣出发UMP自定义报警失败",e);
            return getErrorResponse();
        }
    }

    private JdResponse getErrorResponse(){
    	JdResponse r = new JdResponse(JdResponse.CODE_PARAM_ERROR,JdResponse.MESSAGE_PARAM_ERROR);
    	return r;
    }
    
    private JdResponse getSuccessResponse(){
    	JdResponse r = new JdResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
    	return r;
    }
    
//    @GET
//	@Path("/pushump/test/pushToVosSendCar}")
//	public void pushToVosSendCar() {
//    	Consumer service = (Consumer) SpringHelper
//				.getBean("messageConumerService");
//
//		Message message = new Message();
//		message.setDestinationCode("dms_pop_pickup");
//		message.setConnectionSystemId("");
//		message.setContent("msg test!");
//		
//		try {
//			service.consume(message);
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
