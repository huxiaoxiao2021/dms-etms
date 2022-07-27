package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ipc.csa.GeneralStockAllotOutInterface;
import com.jd.ipc.csa.model.AllotRequestDetail;
import com.jd.ipc.csa.model.AllotResponseDetail;
import com.jd.ipc.csa.model.AllotScenarioEnum;
import com.jd.ipc.csa.model.ApiResult;
import com.jd.ql.dms.report.ReportExternalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 供应链中台二期改造 渠道库存分配接口 https://cf.jd.com/pages/viewpage.action?pageId=626824449
 * @author: xumigen
 * @create: 2022-07-12 15:45
 **/
@Service("generalStockAllotOutInterfaceManager")
public class GeneralStockAllotOutInterfaceManagerImpl implements GeneralStockAllotOutInterfaceManager{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GeneralStockAllotOutInterface generalStockAllotOutInterface;

    @Override
    public List<AllotResponseDetail> batchAllotStock(List<AllotRequestDetail> allotRequestDetails, AllotScenarioEnum scenario, String bizUniqueKey, Boolean isIdempotent, String sysName) {
        ApiResult<List<AllotResponseDetail>> apiResult = generalStockAllotOutInterface.batchAllotStock(allotRequestDetails,scenario.getValue(),bizUniqueKey,isIdempotent,sysName);
        logger.info("渠道库存分配接口返回结果apiResult[{}]allotRequestDetails[{}]scenario[{}]bizUniqueKey[{}]isIdempotent[{}]sysName[{}]",
                JsonHelper.toJson(apiResult), JsonHelper.toJson(allotRequestDetails),scenario.toString(),bizUniqueKey,isIdempotent,sysName);
        if(apiResult.isSuccess()){
            return apiResult.getData();
        }
        return Lists.newArrayList();
    }
}

