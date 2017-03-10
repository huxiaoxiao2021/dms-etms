package com.jd.bluedragon.distribution.rest.CarSchedule;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleResponse;
import com.jd.bluedragon.distribution.carSchedule.service.CarScheduleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆园区调度：提供园区车载信息的rest接口
 * Created by wuzuxiang on 2017/3/10.
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class CarScheduleRest {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    CarScheduleService carScheduleService;

    /**
     * 暴露一个接口，提供车辆的线路类型routeType,车载总量packageNum,当前分拣中心的货物量localPackageNum,当前分拣中心载货明细localPackageDetail,
     * @param vehicleNumber
     * @param siteCode
     * @return
     */
    @POST
    @Path("/carSchedule/queryVehicleRouteType")
    public CarScheduleResponse queryParkingCarInfo(String vehicleNumber, String siteCode){
        CarScheduleResponse result = new CarScheduleResponse();
        result.setCode(400);
        result.setMessage("请求成功，无返回数据...");
        if(null != vehicleNumber ){
            Integer routeType = null;
            Integer totalPackageNum = null;
            Integer localPackageNum = null;
            List<SendDetail> sendDetails = new ArrayList<SendDetail>();
            try{
                if(null != siteCode && !"".equals(siteCode) && NumberUtils.isNumber(siteCode)){
                    Integer siteNo = NumberUtils.toInt(siteCode);
                    routeType = carScheduleService.routeTypeByVehicleNoAndSiteCode(vehicleNumber,siteNo);
                    totalPackageNum = carScheduleService.packageNumByVehicleNoAndSiteCode(vehicleNumber,siteNo);
                    localPackageNum = carScheduleService.localPackageNumByVehicleNo(vehicleNumber,siteNo);
                    sendDetails = carScheduleService.sendDetailByCarAndSiteCode(vehicleNumber,siteNo);
                }
            }catch (Exception e){
                result.setCode(500);
                result.setMessage("请求接口异常");
                this.logger.error("请求接口异常..",e);
            }
            result.setCode(200);
            result.setMessage("请求成功");
            result.setRouteType(routeType);
            result.setTotalPackageNum(totalPackageNum);
            result.setLocalPackageNum(localPackageNum);
            result.setSendDetails(sendDetails);
        }
        return result;
    }

}
