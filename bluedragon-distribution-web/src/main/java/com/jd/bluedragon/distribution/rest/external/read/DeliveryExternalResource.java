package com.jd.bluedragon.distribution.rest.external.read;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.service.DmsExternalReadService;
import com.jd.bluedragon.distribution.wss.dto.*;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * DMS对外部系统Rest接口（李文江）
 * @author dudong
 * @date 2015/12/7
 *
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DeliveryExternalResource {
    private static final Logger log = LoggerFactory.getLogger(DeliveryExternalResource.class);
    @Autowired
    private DmsExternalReadService dmsExternalReadService;

    /**
     * 根据批次号或者箱号(包裹号)获取发货箱子信息
     * @param code 批次号或者箱号(包裹号）
     * @param type 1:批次号，2：箱号（包裹号）
     * @param siteCode 如果code是包裹号，则sitecode是收货站点
     * */
    @GZIP
    @GET
    @Path("/external/getBoxSummary/{code}/{type}/{siteCode}")
    @JProfiler(jKey = "DMSWEB.REST.DeliveryExternalResource.getBoxSummary",mState = {JProEnum.TP})
    public List<BoxSummaryDto> getBoxSummary(@PathParam("code") String code, @PathParam("type")Integer type, @PathParam("siteCode") Integer siteCode){
        if(log.isInfoEnabled()){
            log.info("code={}", code);
            log.info("type={}", type);
            log.info("siteCode={}", siteCode);
        }
        return dmsExternalReadService.getBoxSummary(code,type,siteCode);
    }


    /**
     * 根据批次号或者箱号获取发货包裹信息
     * @param code 批次号或者箱号(包裹号）
     * @param type 1:批次号，2：箱号（包裹号）
     * @param siteCode 如果code是包裹号，则sitecode是收货站点
     * */
    @GZIP
    @GET
    @Path("/external/getPackageSummary/{code}/{type}/{siteCode}")
    @JProfiler(jKey = "DMSWEB.REST.DeliveryExternalResource.getPackageSummary",mState = {JProEnum.TP})
    public List<PackageSummaryDto> getPackageSummary(@PathParam("code")String code,@PathParam("type") Integer type, @PathParam("siteCode")Integer siteCode){
        if(log.isInfoEnabled()){
            log.info("code={}", code);
            log.info("type={}", type);
            log.info("siteCode={}", siteCode);
        }
        return dmsExternalReadService.getPackageSummary(code,type,siteCode);
    }

    /**
     * 通过封车号查询封车信息
     * @param sealCode
     * @return
     */
    @GZIP
    @GET
    @Path("/external/findSealByCodeSummary/{sealCode}")
    @JProfiler(jKey = "DMSWEB.REST.DeliveryExternalResource.findSealByCodeSummary",mState = {JProEnum.TP})
    public SealVehicleSummaryDto findSealByCodeSummary(@PathParam("sealCode")String sealCode){
        if(log.isInfoEnabled()){
            log.info("sealCode={}", sealCode);
        }
        return dmsExternalReadService.findSealByCodeSummary(sealCode);
    }

    /**
     * 根据站点，发货开始时间，发货结束时间   获取这段时间发货数据 （发送运单，对应包裹数）
     * @param siteid
     * @param startTime
     * @param endTime
     * @return
     */
    @GZIP
    @GET
    @Path("/external/findDeliveryPackageBySiteSummary/{siteid}/{startTime}/{endTime}")
    @JProfiler(jKey = "DMSWEB.REST.DeliveryExternalResource.findDeliveryPackageBySiteSummary",mState = {JProEnum.TP})
    public List<WaybillCodeSummatyDto> findDeliveryPackageBySiteSummary(@PathParam("siteid")int siteid, @PathParam("startTime") String startTime, @PathParam("endTime") String endTime){
        if(log.isInfoEnabled()){
            log.info("siteid={}", siteid);
            log.info("startTime={}", startTime);
            log.info("endTime={}", endTime);
        }
        return dmsExternalReadService.findDeliveryPackageBySiteSummary(siteid, DateHelper.parseDateTime(startTime), DateHelper.parseDateTime(endTime));
    }

    /**
     * 根据运单号获取发货包裹数
     * @param siteid
     * @param waybillCode
     * @return
     */
    @GZIP
    @GET
    @Path("/external/findDeliveryPackageByCodeSummary/{siteid}/{waybillCode}")
    @JProfiler(jKey = "DMSWEB.REST.DeliveryExternalResource.findDeliveryPackageByCodeSummary",mState = {JProEnum.TP})
    public List<WaybillCodeSummatyDto> findDeliveryPackageByCodeSummary(@PathParam("siteid")int siteid,@PathParam("waybillCode")String waybillCode){
        if(log.isInfoEnabled()){
            log.info("siteid={}", siteid);
            log.info("waybillCode={}", waybillCode);
        }
        return dmsExternalReadService.findDeliveryPackageByCodeSummary(siteid, waybillCode);
    }


    /**
     * 根据封车号或者三方运单号获取发车运单信息
     * @param code 封车号或者三方运单号
     * @param type 1:封车号，2：三方运单号
     * */
    @GZIP
    @GET
    @Path("/external/getWaybillsByDeparture/{code}/{type}")
    @JProfiler(jKey = "DMSWEB.REST.DeliveryExternalResource.getWaybillsByDeparture",mState = {JProEnum.TP})
    public List<DepartureWaybillDto> getWaybillsByDeparture(@PathParam("code")String code, @PathParam("type")Integer type){
        if(log.isInfoEnabled()){
            log.info("code={}", code);
            log.info("type={}", type);
        }
        return dmsExternalReadService.getWaybillsByDeparture(code, type);
    }
}
