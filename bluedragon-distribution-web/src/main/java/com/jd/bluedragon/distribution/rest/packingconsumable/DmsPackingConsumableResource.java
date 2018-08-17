package com.jd.bluedragon.distribution.rest.packingconsumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created by hanjiaxing1 on 2018/8/16.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DmsPackingConsumableResource {

    private final Log logger = LogFactory.getLog(this.getClass());

//    @Autowired
//    private DmsPackingConsumableService dmsPackingConsumableService;

    @Autowired
    private PackingConsumableInfoService packingConsumableInfoService;

    @Autowired
    private DmsConsumableRelationService dmsConsumableRelationService;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

//    @Autowired
//    private WaybillConsumableRelationService waybillConsumableRelationService;

    @GET
    @Path("/packing/info/{dmsId}")
    public JdResponse getDriver(@PathParam("dmsId") Integer dmsId) {
//        JdResponse jdResponse = dmsPackingConsumableService.getPackingConsumableInfoByDmsId(dmsId);
//        try {
            //        PackingConsumableInfo packingConsumableInfo = new PackingConsumableInfo();
//        packingConsumableInfo.setCode("HC001");
//        packingConsumableInfo.setName("test");
//        packingConsumableInfo.setType("test");
//        packingConsumableInfo.setVolume(1.366);
//        packingConsumableInfo.setVolumeCoefficient(1.366);
//        packingConsumableInfo.setUnit("test");
//        packingConsumableInfo.setSpecification("test");
//        packingConsumableInfo.setOperateUserCode("test");
//        packingConsumableInfo.setOperateUserErp("test");
//
//        packingConsumableInfoService.addPackingConsumableInfo(packingConsumableInfo);

//        DmsConsumableRelation dmsConsumableRelation = new DmsConsumableRelation();
//        dmsConsumableRelation.setDmsId(910);
//        dmsConsumableRelation.setDmsName("北京马驹桥分拣中心");
//        dmsConsumableRelation.setConsumableCode("HC001");
//        dmsConsumableRelation.setOperateUserCode("test");
//        dmsConsumableRelation.setOperateUserErp("test");
//
//        dmsConsumableRelationService.addDmsConsumableRelation(dmsConsumableRelation);

//        WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
//        waybillConsumableRecord.setDmsId(910);
//        waybillConsumableRecord.setDmsName("北京马驹桥分拣中心");
//        waybillConsumableRecord.setWaybillCode("7111111111");
//        waybillConsumableRecord.setConfirmStatus(1);
//        waybillConsumableRecord.setModifyStatus(0);
//
//        waybillConsumableRecord.setReceiveTime(new Date());
//        waybillConsumableRecord.setReceiveUserCode("test");
//        waybillConsumableRecord.setReceiveUserErp("test");
//        waybillConsumableRecord.setReceiveUserName("test");
//
//        waybillConsumableRecordService.addWaybillConsumableRecord(waybillConsumableRecord);

//            WaybillConsumableRelation waybillConsumableRelation = new WaybillConsumableRelation();
//            waybillConsumableRelation.setWaybillCode("7111111111");
//            waybillConsumableRelation.setConsumableCode("HC001");
//            waybillConsumableRelation.setReceiveQuantity(10);
//            waybillConsumableRelation.setConfirmQuantity(10);
//            waybillConsumableRelationService.addWaybillConsumableRecord(waybillConsumableRelation);
//        } catch (Exception e) {
//            logger.error(e);
//        }
        return new JdResponse();
    }
}
