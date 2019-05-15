package com.jd.bluedragon.distribution.rest.residentPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.TerminalManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintMessages;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ResidentResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private TerminalManager terminalManager;

    /**
     * 查询包裹是否操作站点发货
     * @param boxCode
     * @return
     */
    @GET
    @Path("/resident/isSendBySite/{boxCode}")
    public InvokeResult<String> isSendBySite(@PathParam("boxCode") String boxCode){
        InvokeResult<String> result = new InvokeResult<String>();
        String packageCode = null;
        try{
            //获取箱号中其中的一个包裹号（终端）
            com.jd.etms.erp.service.domain.BaseEntity<List<SendInfoDto>> baseEntity = terminalManager.getSendDetails(boxCode);
            if(baseEntity != null && baseEntity.getResultCode() > 0) {
                List<SendInfoDto> data = baseEntity.getData();
                if(data != null && data.size() > 0){
                    packageCode = data.get(0).getPackageBarcode();
                }
            }
            //根据包裹号判断是否操作发货
            if(packageCode != null){
                BaseEntity<List<PackageState>> entity = waybillTraceManager.getPkStateByPCode(packageCode);
                if(entity != null && entity.getData() != null &&
                        entity.getData().size() > 0){
                    List<PackageState> packageStateList = entity.getData();
                    for(PackageState packageState : packageStateList){
                        if(packageState.getState().equals(Constants.WAYBILL_TRACE_STATE_SEND_BY_SITE)){
                            result.setData(WaybillPrintMessages.MESSAGE_WAYBILL_STATE_SEND_BY_SITE);
                            break;
                        }
                    }
                }
            }
        }catch (Exception e){
            this.logger.error("查询箱号"+boxCode+"是否操作站点发货服务异常!");
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            result.setData(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

}
