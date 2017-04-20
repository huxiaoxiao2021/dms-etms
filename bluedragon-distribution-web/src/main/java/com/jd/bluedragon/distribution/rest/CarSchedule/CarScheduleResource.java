package com.jd.bluedragon.distribution.rest.CarSchedule;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleRequest;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleResponse;
import com.jd.bluedragon.distribution.carSchedule.service.CarScheduleService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
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
public class CarScheduleResource {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    CarScheduleService carScheduleService;

    @Autowired
    GoddessService goddessService;

    /**
     * 暴露一个接口，提供车辆的线路类型routeType,车载总量packageNum,当前分拣中心的货物量localPackageNum,当前分拣中心载货明细localPackageDetail,
     * @return
     */
    @POST
    @Path("/carSchedule/queryVehicleRouteType")
    public CarScheduleResponse queryParkingCarInfo(CarScheduleRequest request){
        CarScheduleResponse result = new CarScheduleResponse();
        result.setCode(400);
        result.setMessage("请求成功，无返回数据...");
        if(null != request && request.getVehicleNumber() != null ){
            String vehicleNumber = request.getVehicleNumber();
            String siteCode = request.getSiteCode();
            Integer routeType = null;
            Integer totalPackageNum = null;
            Integer localPackageNum = null;
            String platformPortJobType = "";
            Integer workType = null;
            List<String> sendCodes = new ArrayList<String>();
            try{
                if(null != siteCode && !"".equals(siteCode) && NumberUtils.isNumber(siteCode)){
                    Integer siteNo = NumberUtils.toInt(siteCode);
                    routeType = carScheduleService.routeTypeByVehicleNoAndSiteCode(vehicleNumber,siteNo);
                    totalPackageNum = carScheduleService.packageNumByVehicleNoAndSiteCode(vehicleNumber,siteNo);
                    localPackageNum = carScheduleService.localPackageNumByVehicleNo(vehicleNumber,siteNo);
                    platformPortJobType = "2";//此接口返回的都是卸货的任务(默认)
                    workType = carScheduleService.isSameOrg(vehicleNumber,siteNo);
                }
            }catch (Exception e){
                result.setCode(500);
                result.setMessage("请求接口异常");
                this.logger.error("请求接口异常..",e);
            }
            result.setCode(200);
            result.setMessage("请求成功");
            result.setVehicleNumber(vehicleNumber);
            result.setRouteType(routeType);
            result.setTotalPackageNum(totalPackageNum);
            result.setLocalPackageNum(localPackageNum);
            result.setPlatformPortJobType(platformPortJobType);
            result.setWorkType(workType);
//            result.setSendDetails(sendDetails);
        }
        return result;
    }

    /**
     * 提供车辆的线路类型routeType
     * @param vehicleNumber
     * @param siteCode
     * @return
     */
//    @POST
//    @Path("/carSchedule/queryCarRouteType")
//    public Integer queryCarRouteType(String vehicleNumber, String siteCode){
//        Integer routeType = 0;
//        if(null != vehicleNumber ){
//            try{
//                if(null != siteCode && !"".equals(siteCode) && NumberUtils.isNumber(siteCode)){
//                    Integer siteNo = NumberUtils.toInt(siteCode);
//                    routeType = carScheduleService.routeTypeByVehicleNoAndSiteCode(vehicleNumber,siteNo);
//                }
//            }catch (Exception e){
//                this.logger.error("请求接口异常..",e);
//            }
//        }
//        return routeType;
//    }

    /**
     * 车辆进出管理，登记记录(cassandra)
     * @Params vehicleNumber 车辆号；siteCode 站点编号 ; key 进出关键字
     * key : 1表示进 0表示出
     */
    @POST
    @Path("/carSchedule/InAndOut")
    public Boolean InAndOut(CarScheduleRequest request){
        String vehicleNumber = "";
        String siteCode = "";
        Integer key = -1;
        if(request != null){
            vehicleNumber = request.getVehicleNumber();
            siteCode = request.getSiteCode();
            key = request.getKey();
        }
        if(null == vehicleNumber || null == siteCode || null == request.getKey()){
            this.logger.error("车辆进出管理确少车牌号、站点、关键字基本信息。");
            return Boolean.FALSE;
        }
        Goddess domain = new Goddess();
        domain.setKey(vehicleNumber);
        String body = "";
        if(key.equals(0)){
            body = "车牌号为：" + vehicleNumber + "的车辆已经到港，站点ID：" + siteCode ;
        }else if(key.equals(1)){
            body = "车牌号为：" + vehicleNumber + "的车辆已经出港，站点ID：" + siteCode;
        }
        domain.setBody(body);
        goddessService.save(domain);
        return Boolean.TRUE;
    }

}
