package com.jd.bluedragon.distribution.rest.allianceBusi;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiFailDetailDto;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 加盟商相关rest接口
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AllianceBusiResouse {

    private Logger log = LoggerFactory.getLogger(AllianceBusiResouse.class);

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 交接服务
     * @param dto
     * @return
     */
    @POST
    @Path("allianceBusi/delivery")
    @JProfiler(jKey = "DMS.WEB.AllianceBusiResouse.allianceBusiDelivery", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<List<AllianceBusiFailDetailDto>> allianceBusiDelivery(AllianceBusiDeliveryDto dto){
        BaseEntity<List<AllianceBusiFailDetailDto>> result = new BaseEntity<List<AllianceBusiFailDetailDto>>();
        try{
            result = allianceBusiDeliveryDetailService.allianceBusiDelivery(dto);
        }catch (Exception e){
            log.error("加盟商交接异常Rest：{}", JsonHelper.toJson(dto),e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }

        return result;
    }

    /**
     * 加盟商交接是否已交接
     * @param waybillCode
     * @return
     */
    @GET
    @Path("allianceBusi/checkDelivered/{waybillCode}")
    @JProfiler(jKey = "DMS.WEB.AllianceBusiResouse.checkDelivered", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> checkDelivered(@PathParam("waybillCode") String waybillCode){
        BaseEntity<Boolean> result = new BaseEntity<Boolean>(BaseEntity.CODE_SUCCESS,BaseEntity.MESSAGE_SUCCESS);
        result.setData(Boolean.FALSE);
        try{
            result.setData(allianceBusiDeliveryDetailService.checkExist(waybillCode));
        }catch (Exception e){
            log.error("加盟商校验已交接异常Rest:{}", waybillCode,e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }

    /**
     * 加盟商余额是否充足
     * 已交接直接返回充足
     *
     * 默认返回充足
     * @param waybillCode
     * @return
     */
    @GET
    @Path("allianceBusi/checkMoney/{waybillCode}")
    @JProfiler(jKey = "DMS.WEB.AllianceBusiResouse.checkMoney", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> checkMoney(@PathParam("waybillCode") String waybillCode){
        BaseEntity<Boolean> result = new BaseEntity<Boolean>(BaseEntity.CODE_SUCCESS,BaseEntity.MESSAGE_SUCCESS);
        result.setData(Boolean.TRUE);
        try{
            if(WaybillUtil.isPackageCode(waybillCode)){
                waybillCode = WaybillUtil.getWaybillCode(waybillCode);
            }
            if(allianceBusiDeliveryDetailService.checkExist(waybillCode)){
                return result;
            }else{
                result.setData(allianceBusiDeliveryDetailService.checkMoney(waybillCode));
            }

        }catch (Exception e){
            log.error("加盟商校验余额异常Rest:{}", waybillCode,e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }


    /**
     * 运单是否为加盟商运单 只处理正向单
     *
     * 默认返回否
     * @param waybillCode
     * @return
     */
    @GET
    @Path("allianceBusi/checkWaybill/{waybillCode}")
    @JProfiler(jKey = "DMS.WEB.AllianceBusiResouse.checkWaybill", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> checkWaybill(@PathParam("waybillCode") String waybillCode){
        BaseEntity<Boolean> result = new BaseEntity<Boolean>(BaseEntity.CODE_SUCCESS,BaseEntity.MESSAGE_SUCCESS);
        result.setData(Boolean.FALSE);
        try{
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryWaybillExtend(true);
            com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,wChoice);
            if(baseEntity.getResultCode() == 1 && baseEntity.getData().getWaybill()!=null) {
                result.setData(BusinessUtil.isForeignForward(baseEntity.getData().getWaybill().getWaybillSign()) && BusinessUtil.isAllianceBusi(baseEntity.getData().getWaybill().getWaybillSign()));
            }
        }catch (Exception e){
            log.error("运单是否为加盟商运单异常Rest:{}", waybillCode,e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }


    /**
     * 此单是否归属此加盟商站点
     *
     * 预分拣站点 或 揽收站点等于该加盟商站点时通过
     * @param waybillCode
     * @return
     */
    @GET
    @Path("allianceBusi/checkOwn/{waybillCode}/{siteId}")
    @JProfiler(jKey = "DMS.WEB.AllianceBusiResouse.checkOwn", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> checkOwn(@PathParam("waybillCode") String waybillCode,@PathParam("siteId") String siteId){
        BaseEntity<Boolean> result = new BaseEntity<Boolean>(BaseEntity.CODE_SUCCESS,BaseEntity.MESSAGE_SUCCESS);
        result.setData(Boolean.FALSE);
        try{
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillP(true);
            com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,wChoice);
            if(baseEntity.getResultCode() == 1 && baseEntity.getData().getWaybill()!=null) {
                if(Integer.valueOf(siteId).equals(baseEntity.getData().getWaybill().getOldSiteId()) ||
                        (baseEntity.getData().getWaybillPickup()!=null && Integer.valueOf(siteId).equals(baseEntity.getData().getWaybillPickup().getPickupSiteId()))){
                    result.setData(Boolean.TRUE);
                }
            }
        }catch (Exception e){
            log.error("此单是否归属此加盟商站点异常Rest:{}", waybillCode,e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }
}
