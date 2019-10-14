package com.jd.bluedragon.distribution.rest.transBillSchedule;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 用于派车单号缓存分拣信息
 * 用于校验分拣到同一个箱子里面的包裹是不是同一个派车单子下面
 * Created by wuzuxiang on 2017/4/13.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class TransBillScheduleResource {

    private static final Log logger = LogFactory.getLog(TransBillScheduleResource.class);

    @Autowired
    private TransBillScheduleService transBillScheduleService;

    /**
     * 派车单号调度，此rest接口用于分拣校验，提供两个参数，
     * 输出分拣到这个箱号下面的包裹是不是同一个派车单号
     * @return true / false
     */
    @POST
    @Path("/transBillSchedule/check")
    public TransBillScheduleResponse checkScheduleBill(TransBillScheduleRequest request){
        TransBillScheduleResponse response = new TransBillScheduleResponse();

        if(request != null && StringUtils.isNotBlank(request.getBoxCode()) && StringUtils.isNotBlank(request.getWaybillCode())){
            try{
                response.setBoxCode(request.getBoxCode());
                response.setWaybillCode(request.getWaybillCode());
                response.setScheduleCode(transBillScheduleService.queryScheduleCode(request.getWaybillCode()));
                response.setSameScheduleBill(transBillScheduleService.checkSameScheduleBill(request));
                response.setRoadCode(transBillScheduleService.queryTruckSpotByWaybillCode(request.getWaybillCode()));
            }catch(Exception e){
                this.logger.error("派车单信息校验失败" + request.toString());
            }
        }
        return response;
    }

    /**
     * 获取箱号对应的派车单号
     * @param boxCode
     * @return
     */
    @GET
    @Path("/transBillSchedule/getKey")
    public String getKey(String boxCode) {
        return transBillScheduleService.getKey(boxCode);
    }

    /**
     * 设置派车单的redis记录，统一前缀
     * @param boxCode 箱号
     * @param waybillCode 包裹号运单号
     * @return
     */
    public void setKey(String boxCode,String waybillCode) {
        transBillScheduleService.setKey(boxCode,waybillCode);
    }

    /**
     * 检查该箱子的派车单信息是不是存在
     * @param boxCode
     * @return
     */
    public Boolean existsKey(String boxCode){
        return transBillScheduleService.existsKey(boxCode);
    }

    /**
     * 删除该箱号的派车单信息
     * @param boxCode
     * @return
     */
    public boolean delete(String boxCode) {
        return transBillScheduleService.delete(boxCode);
    }
}
