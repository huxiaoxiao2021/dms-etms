package com.jd.bluedragon.distribution.rest.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WareStockExportRequest;
import com.jd.bluedragon.distribution.api.response.SortDistRelation;
import com.jd.bluedragon.distribution.api.response.SortDistResponse;
import com.jd.bluedragon.distribution.api.response.WareStockExportResponse;
import com.jd.fce.upc.area.service.contract.SortDeliveryCenterInfoSyncService;
import com.jd.fce.upc.area.service.domain.SortDeliveryCenterInfo;
import com.jd.fce.upc.area.service.domain.SortDeliveryCenterInfoSyncRequest;
import com.jd.fce.upc.area.service.domain.SortDeliveryCenterInfoSyncResponse;
import com.jd.warestock.export.WareStockServiceExport;
import com.jd.warestock.export.vo.Sid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dudong
 * @date 2015/5/10
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WareStockExportResource {

    private static final Log logger = LogFactory.getLog(WareStockExportResource.class);
    public static final String requestResource = "abc";
    private static final Integer requestSuccessCode = 1;
    @Autowired
    public WareStockServiceExport wareStockServiceExport;

    @Autowired
    public SortDeliveryCenterInfoSyncService sortDeliverySyncService;

    @POST
    @GZIP
    @Path("/warestock/query")
    public WareStockExportResponse queryWareStockByWidAndRid(WareStockExportRequest request){
        try{
            List<Sid> sids = wareStockServiceExport.querySidsByWidsAdRid(request.getWids(),request.getRid());
            WareStockExportResponse response = new WareStockExportResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
            response.setSids(sids);
            return response;
        }catch(Exception ex){
            logger.error("根据配送中心和SKU获取库房信息失败", ex);
            WareStockExportResponse response = new WareStockExportResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
            return response;
        }
    }

    @GET
    @GZIP
    @Path("/sortdist/{sortID}")
    public SortDistResponse queryRelationByID(@PathParam("sortID") Integer sortID) {
        SortDeliveryCenterInfoSyncRequest request = new SortDeliveryCenterInfoSyncRequest();
        request.setSource(requestResource);
        SortDeliveryCenterInfoSyncResponse response = null;
        SortDistResponse stResponse = new SortDistResponse();
        try{
            response = sortDeliverySyncService.sync(request);
        }catch (Throwable ex){
            logger.error("调用ofc获取分拣和配送对应关系接口失败，原因 ",ex);
            stResponse = new SortDistResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
            return stResponse;
        }

        if(requestSuccessCode.equals(response.getResultCode())){
           if(null != response.getResults() && response.getResults().size() >= 0){
               stResponse = new SortDistResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
               stResponse.setSortDists(convert2SortDistRelation(response.getResults(),sortID));
               return stResponse;
           }else{
               stResponse = new SortDistResponse(JdResponse.CODE_OK_NULL,JdResponse.MESSAGE_OK_NULL);
               return stResponse;
           }
        }else{
            logger.warn("调用ofc获取分拣和配送对应关系接口结果不正确，返回码 "
                    + response.getResultCode() + ", 返回信息 "
                    + response.getResultMsg());
            stResponse = new SortDistResponse(JdResponse.CODE_INTERNAL_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
            return stResponse;
        }

    }

    private List<SortDistRelation> convert2SortDistRelation(List<SortDeliveryCenterInfo> sortDeliveryCenterInfos, Integer sortID){
        List<SortDistRelation> sortDistRelations = new ArrayList<SortDistRelation>();
        if(null == sortDeliveryCenterInfos || sortDeliveryCenterInfos.isEmpty()){
            return sortDistRelations;
        }
        for(SortDeliveryCenterInfo info : sortDeliveryCenterInfos){
            if(sortID.equals(info.getSortCenterId())){
                SortDistRelation relation = new SortDistRelation();
                relation.setSortCenterId(info.getSortCenterId());
                relation.setSortCenterName(info.getSortCenterName());
                relation.setDeliveryCenterId(info.getDeliveryCenterId());
                relation.setDeliveryCenterName(info.getDeliveryCenterName());
                sortDistRelations.add(relation);
                return sortDistRelations;
            }
        }
        return sortDistRelations;
    }
}
