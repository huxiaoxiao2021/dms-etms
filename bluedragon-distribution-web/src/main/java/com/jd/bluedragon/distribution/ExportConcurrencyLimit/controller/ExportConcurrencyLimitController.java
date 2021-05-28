package com.jd.bluedragon.distribution.ExportConcurrencyLimit.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: liming522
 * @Description:导出并发限制
 * @Date: create in 2021/4/14 13:43
 */
@Controller
@RequestMapping("exportConcurrencyLimit")
public class ExportConcurrencyLimitController {

    private static final Logger log = LoggerFactory.getLogger(ExportConcurrencyLimitController.class);

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;


    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.ExportConcurrencyLimit.controller.ExportConcurrencyLimitController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(@RequestParam(value = "currentKey") String currentKey){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(currentKey)){
                result.customMessage(InvokeResult.RESULT_EXPORT_LIMIT_CODE,InvokeResult.RESULT_EXPORT_LIMIT_MESSAGE);
                return result;
            }
        }catch (Exception e){
            log.error("校验导出并发接口异常",e);
            result.customMessage(InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_CODE,InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_MESSAGE);
            return result;
        }
        return result;
    }
}
    
