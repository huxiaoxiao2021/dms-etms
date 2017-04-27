package com.jd.bluedragon.distribution.rest.transBillSchedule;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.bluedragon.distribution.urban.service.UrbanWaybillService;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

    private static final String TRANSBILL_PREFIX = "schedule_transBill_";

    private static final long expire_time = 7*24*60*60;//过期时间，以秒为单位

    @Autowired
    private Cluster redisClientCache;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    private UrbanWaybillService urbanWaybillService;

    @Autowired
    private TransBillScheduleService transBillScheduleService;

    /**
     * 派车单号调度，此rest接口用于分拣校验，提供两个参数，
     * 输出分拣到这个箱号下面的包裹是不是同一个派车单号
     * @param boxCode 箱号
     * @param waybillCode 运单号
     * @return true / false
     */
    @POST
    @Path("transBillSchedule/check")
    public TransBillScheduleResponse checkScheduleBill(String boxCode, String waybillCode){
        TransBillScheduleResponse response = new TransBillScheduleResponse();

        if(StringUtils.isNotBlank(boxCode) && StringUtils.isNotBlank(waybillCode)){
            TransBillScheduleRequest request = new TransBillScheduleRequest();
            request.setBoxCode(boxCode);
            request.setWaybillCode(waybillCode);
            response.setSameScheduleBill(transBillScheduleService.checkSameScheduleBill(request));
            response.setRoadCode(transBillScheduleService.queryRoadCodeByWaybillCode(waybillCode));
        }
        return response;
    }

    /**
     * 获取箱号对应的派车单号
     * @param boxCode
     * @return
     */
    @POST
    @Path("transBillSchedule/getKey")
    public String getKey(String boxCode) {
        return transBillScheduleService.getKey(boxCode);
    }

    /**
     * 设置派车单的redis记录，统一前缀
     * @param boxCode 箱号
     * @param waybillCode 包裹号运单号
     * @return
     */
    @POST
    @Path("transBillSchedule/setKey")
    public void setKey(String boxCode,String waybillCode) {
        transBillScheduleService.setKey(boxCode,waybillCode);
    }

    /**
     * 检查该箱子的派车单信息是不是存在
     * @param boxCode
     * @return
     */
    @POST
    @Path("transBillSchedule/existsKet")
    public Boolean existsKey(String boxCode){
        return transBillScheduleService.existsKey(boxCode);
    }

    /**
     * 删除该箱号的派车单信息
     * @param boxCode
     * @return
     */
    @POST
    @Path("transBillSchedule/delete")
    public boolean delete(String boxCode) {
        return transBillScheduleService.delete(boxCode);
    }
}
