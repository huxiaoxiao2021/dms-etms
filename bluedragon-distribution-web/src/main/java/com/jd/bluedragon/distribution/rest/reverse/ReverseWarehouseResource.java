package com.jd.bluedragon.distribution.rest.reverse;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.ReverseWarehouseRequest;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * create by shipeilin 2018-06-27
 * <p>
 * 逆向返仓REST服务
 * <p>
 * 主要功能点
 * 1、提供返仓扫描的校验服务：是否存在已分拣或已发货的包裹
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ReverseWarehouseResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SortingService sortingService;

    @Autowired
    private SendMService sendMService;

    @Autowired
    private SendDetailService sendDetailService;

    private static final String MESSAGE_PACKAGE_ERROR = "请扫描正确的运单号或包裹号！";
    private static final String MESSAGE_PACKAGE_SORTING = "请先取消分拣建包";
    private static final String MESSAGE_PACKAGE_SEND = "请先取消发货再取消分拣建包";
    private static final String MESSAGE_WAYBILL_SORTING = "运单中分拣建包{0}个包裹，请先取消分拣建包";
    private static final String MESSAGE_WAYBILL_SEND = "运单中分拣建包{0}个包裹，发货{1}个包裹，请先取消发货再取消分拣建包";

    //老发货原包发货，存在原包没有分拣的情况
    private static final String MESSAGE_WAYBILL_SEND_ONLY = "运单中发货{0}个包裹，请先取消发货";
    private static final String MESSAGE_PACKAGE_SEND_ONLY = "请先取消发货";


    @POST
    @Path("/reverse/warehouse/check")
    public JdResponse returnWarehouseCheck(ReverseWarehouseRequest request){
        JdResponse response = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        Integer createSiteCode = request.getSiteCode();
        String waybillCode = null;
        String packageCode = null;
        //是否扫描的包裹号
        boolean isPackage = false;
        if(WaybillUtil.isPackageCode(request.getPackageOrWaybillCode())){
            packageCode = request.getPackageOrWaybillCode();
            waybillCode = WaybillUtil.getWaybillCode(packageCode);
            isPackage = true;
        }else if(WaybillUtil.isWaybillCode(request.getPackageOrWaybillCode())){
            waybillCode = request.getPackageOrWaybillCode();
        }else{
            response.toFail(MESSAGE_PACKAGE_ERROR);
            return response;
        }

        List<Sorting> sortingList = null;
        List<SendDetail> sendDetailList = null;
        List<SendM> sendMList = new ArrayList<SendM>();
        //数据的状态：已分拣，已发货
        boolean isSorting = false;
        boolean isSend = false;
        try{
            sortingList = sortingService.findByWaybillCodeOrPackageCode(createSiteCode, waybillCode, packageCode);
            //已分拣
            if(sortingList != null && !sortingList.isEmpty()){
                isSorting = true;
            }
            sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, waybillCode, packageCode);
            //已发货
            if(sendDetailList != null && !sendDetailList.isEmpty()){
                isSend = true;
            }
            //包裹只是做了发货（老发货原包发货才有的情况），需要进一步判断SendM
            if(!isSorting && isSend){
                //获取所有箱号
                Set<String> boxCodes = new HashSet<String>();
                for(SendDetail sendDetail : sendDetailList){
                    boxCodes.add(sendDetail.getBoxCode());
                }
                for (String boxCode : boxCodes){
                    List<SendM> temp = sendMService.findDeliveryRecord(createSiteCode, boxCode);
                    if(temp != null && !temp.isEmpty()){
                        sendMList.addAll(temp);
                    }
                }
                //sendM没有发货记录，说明老发货取消发货了，将isSend置为false
                if(sendMList.isEmpty()){
                    isSend = false;
                }
            }
        }catch (Exception e){
            log.error("返仓扫描查询分拣发货数据失败", e);
            response.toError("系统异常：查询分拣发货数据失败");
            return response;
        }

        String message = "ok";

        //包裹只是做了分拣
        if(isSorting && !isSend){
            if(isPackage){
                message = MESSAGE_PACKAGE_SORTING;
            }else{
                message = MessageFormat.format(MESSAGE_WAYBILL_SORTING, sortingList.size());
            }
            response.toFail(message);
        }

        //包裹只是做了发货（老发货原包发货才有的情况）
        if(!isSorting && isSend){
            if(isPackage){
                message = MESSAGE_PACKAGE_SEND_ONLY;
            }else{
                message = MessageFormat.format(MESSAGE_WAYBILL_SEND_ONLY, sendMList.size());
            }
            response.toFail(message);
        }

        //包裹已分拣发货
        if(isSorting && isSend){
            if(isPackage){
                message = MESSAGE_PACKAGE_SEND;
            }else{
                message = MessageFormat.format(MESSAGE_WAYBILL_SEND, sortingList.size(), sendDetailList.size());
            }
            response.toFail(message);
        }

        return response;
    }

}
