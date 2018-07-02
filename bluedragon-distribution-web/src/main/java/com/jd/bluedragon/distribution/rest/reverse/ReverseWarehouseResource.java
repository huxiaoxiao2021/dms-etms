package com.jd.bluedragon.distribution.rest.reverse;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.ReverseWarehouseRequest;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.List;

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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SortingService sortingService;

    @Autowired
    private SendDetailService sendDetailService;

    private static final String MESSAGE_PACKAGE_ERROR = "请扫描正确的运单号或包裹号！";
    private static final String MESSAGE_PACKAGE_SORTING = "请先取消分拣建包";
    private static final String MESSAGE_PACKAGE_SEND = "请先取消发货再取消分拣建包";
    private static final String MESSAGE_WAYBILL_SORTING = "运单中分拣建包{0}个包裹，请先取消分拣建包";
    private static final String MESSAGE_WAYBILL_SEND = "运单中分拣建包{0}个包裹，发货{1}个包裹，请先取消发货再取消分拣建包";

    @POST
    @Path("/reverse/warehouse/check")
    public JdResponse returnWarehouseCheck(ReverseWarehouseRequest request){
        JdResponse response = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        Integer createSiteCode = request.getSiteCode();
        String waybillCode = null;
        String packageCode = null;
        //是否扫描的包裹号
        boolean isPackage = false;
        if(BusinessHelper.isPackageCode(request.getPackageOrWaybillCode())){
            packageCode = request.getPackageOrWaybillCode();
            waybillCode = BusinessHelper.getWaybillCode(packageCode);
            isPackage = true;
        }else if(BusinessHelper.isWaybillCode(request.getPackageOrWaybillCode())){
            waybillCode = request.getPackageOrWaybillCode();
        }else{
            response.toFail(MESSAGE_PACKAGE_ERROR);
            return response;
        }

        List<Sorting> sortingList = null;
        List<SendDetail> sendDetailList = null;
        //数据的状态：0正常，1已分拣，2已发货
        int status = 0;

        try{
            sortingList = sortingService.findByWaybillCodeOrPackageCode(createSiteCode, waybillCode, packageCode);
            //已分拣
            if(sortingList != null && !sortingList.isEmpty()){
                status++;
            }
            sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, waybillCode, packageCode);
            //已发货
            if(sendDetailList != null && !sendDetailList.isEmpty()){
                status++;
            }
        }catch (Exception e){
            logger.error("返仓扫描查询分拣发货数据失败", e);
            response.toError("系统异常：查询分拣发货数据失败");
            return response;
        }

        String message = "ok";

        //包裹只是做了分拣
        if(1 == status){
            if(isPackage){
                message = MESSAGE_PACKAGE_SORTING;
            }else{
                message = MessageFormat.format(MESSAGE_WAYBILL_SORTING, sortingList.size());
            }
            response.toFail(message);
        }

        //包裹已发货
        if(2 == status){
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
