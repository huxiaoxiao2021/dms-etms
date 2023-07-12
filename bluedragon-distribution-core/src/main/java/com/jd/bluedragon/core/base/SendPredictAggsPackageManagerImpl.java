package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.dms.wb.report.api.jysendpredict.dto.SendPredictAggsQuery;
import com.jd.dms.wb.report.api.jysendpredict.dto.SendPredictToScanPackage;
import com.jd.dms.wb.report.api.jysendpredict.jsf.ISendPredictAggsJsfService;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/11 20:30
 * @Description: 获取发货波次待扫数据
 */
@Service("sendPredictAggsPackageManager")
public class SendPredictAggsPackageManagerImpl implements SendPredictAggsPackageManager{

    private Logger log = LoggerFactory.getLogger(SendPredictAggsPackageManagerImpl.class);

    @Autowired
    private ISendPredictAggsJsfService sendPredictAggsJsfService;

    @Override
    @JProfiler(jKey = "DMS.BASE.SendPredictAggsPackageManagerImpl.getSendPredictToScanPackageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<SendPredictToScanPackage> getSendPredictToScanPackageList(SendPredictAggsQuery query) {
        try{
            Result<List<SendPredictToScanPackage>> response = sendPredictAggsJsfService.getSendPredictToScanPackageList(query);
            if(response != null && CollectionUtils.isNotEmpty(response.getData())){
                return response.getData();
            }
        }catch (Exception e){
            log.error("获取发货波次待扫包裹数据异常! param-{}", JSON.toJSONString(query),e);
        }
        return null;
    }
}
