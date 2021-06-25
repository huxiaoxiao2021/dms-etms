package com.jd.bluedragon.distribution.rest.investigation;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 排查工具rest
 *
 * @author hujiping
 * @date 2021/6/25 1:51 下午
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class InvestigationResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    /**
     * 查询面单始发目的道口笼车
     *
     * @param waybillCode 运单号
     * @param createSiteCode 始发地ID
     * @param preSiteCode 预分拣站点ID
     * @return
     */
    @GET
    @Path("/investigation/queryCrossPackageTag/{waybillCode}/{createSiteCode}/{preSiteCode}")
    public InvokeResult<JSONObject> queryCrossPackageTag(@PathParam("waybillCode") String waybillCode,
                                                                 @PathParam("createSiteCode") Integer createSiteCode,
                                                                 @PathParam("preSiteCode") Integer preSiteCode) {
        InvokeResult<JSONObject> result = new InvokeResult<JSONObject>();

        Waybill waybill = waybillCommonService.findWaybillAndPack(waybillCode);
        if(waybill == null){
            result.hintMessage("运单不存在!");
            return result;
        }

        BaseDmsStore baseDmsStore = new BaseDmsStore();
        baseDmsStore.setStoreId(waybill.getStoreId());
        baseDmsStore.setCky2(waybill.getCky2());
        baseDmsStore.setOrgId(waybill.getOrgId());
        baseDmsStore.setDmsId(createSiteCode);
        CrossPackageTagNew tag = null;
        JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore,
                preSiteCode, createSiteCode,
                BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay()));
        if(jdResult.isSucceed()) {
            tag = jdResult.getData();
        }else{
            result.hintMessage(jdResult.getMessage());
            return result;
        }
        JSONObject resultObject  = new JSONObject();
        resultObject.put("始发", tag.getOriginalDmsName());
        resultObject.put("目的", tag.getDestinationDmsName());
        resultObject.put("始发道口", tag.getOriginalCrossCode());
        resultObject.put("目的道口", tag.getDestinationCrossCode());
        resultObject.put("始发笼车", tag.getOriginalTabletrolleyCode());
        resultObject.put("目的笼车", tag.getDestinationTabletrolleyCode());
        result.setData(resultObject);
        return result;
    }
}
