package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * 外单逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ReversePrintResource {

    private static final Log logger= LogFactory.getLog(ReversePrintResource.class);

    @Autowired
    private ReversePrintService reversePrintService;
    /**
     * 外单逆向换单打印提交数据
     * @return JSON【{code: message: data:}】
     */
    @POST
    @Path("/reverse/exchange/print")
    public InvokeResult<Boolean> handlePrint(ReversePrintRequest request){
        if(logger.isInfoEnabled()) {
            logger.info("【逆向换单处理打印数据】" + request.toString());
        }
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        try{
            reversePrintService.handlePrint(request);
            result.setCode(JdResponse.CODE_OK);
            result.setMessage(JdResponse.MESSAGE_OK);
            result.setData(Boolean.TRUE);
        }
        catch (Throwable e){
            logger.error("【逆向换单打印】",e);
            result.setCode(JdResponse.CODE_SERVICE_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            result.setData(Boolean.FALSE);
        }
        return result;
    }

    /**
     * 根据原单号获取对应的新单号
     * 1.自营拒收：新运单规则：T+原运单号。调取运单来源：从运单处获取，调取运单新接口。
     * 2.外单拒收：新运单规则：生成新的V单。调取运单来源：1）从外单获得新外单单号。2）通过新外单单号从运单处调取新外单的信息。
     * 3.售后取件单：新运单规则：生成W单或VY单。调取运单来源：从运单处获取，调取运单新接口。
     * 4.配送异常类订单：新运单规则：T+原运单号,调取运单来源：从运单处获得，调取运单新接口。
     * 5.返单换单：1）新运单规则：F+原运单号  或  F+8位数字,调取运单来源：从运单处获得，调取运单新接口。2）分拣中心集中换单，暂时不做。
     * @param oldWaybillCode 原单号
     * @return
     */
    @GET
    @Path("/reverse/exchange/getNewWaybillCode/{oldWaybillCode}")
    public InvokeResult<String> getNewWaybillCode(@PathParam("oldWaybillCode") String oldWaybillCode){
        InvokeResult<String> result=new InvokeResult<String>();
        try {
           result= reversePrintService.getNewWaybillCode(oldWaybillCode, true);
        }catch (Throwable e){
            logger.error("[逆向换单获取新单号]",e);
            result.error(e);
        }
        return result;
    }

    /**
     * 自营逆向换单
     * @param domain
     * @return
     */
    @POST
    @Path("reverse/exchange/ownWaybill")
    public InvokeResult<Boolean> exchangeOwnWaybill(OwnReverseTransferDomain domain){
        InvokeResult<Boolean> result;
        try{
            result = reversePrintService.checkWayBillForExchange(domain.getWaybillCode(), domain.getSiteId());
            if(result.getData()){
                result= reversePrintService.exchangeOwnWaybill(domain);
            }else{
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            }
        }catch (Throwable e){
            result=new InvokeResult<Boolean>();
            logger.error("自营逆向换单",e);
            result.error(e);
        }
        return result;
    }

    /**
     * 逆向换单限制校验
     * @param domain
     * @return
     */
    @POST
    @Path("reverse/exchange/check")
    public InvokeResult<Boolean> exchangeCheck(OwnReverseTransferDomain domain){
        InvokeResult<Boolean> result;
        try{
            result = reversePrintService.checkWayBillForExchange(domain.getWaybillCode(), domain.getSiteId());
        }catch (Throwable e){
            result=new InvokeResult<Boolean>();
            logger.error("逆向换单检查异常",e);
            result.error(e);
        }
        return result;
    }

}
