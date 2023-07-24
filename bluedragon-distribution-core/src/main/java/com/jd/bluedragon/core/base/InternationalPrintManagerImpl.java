package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.print.domain.international.InternationalPrintReq;
import com.jd.bluedragon.distribution.print.domain.international.InternationalPrintRes;
import com.jd.bluedragon.distribution.print.waybill.handler.InternationalPdfPrintHandler;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * 国际化打印接口
 *
 * @author hujiping
 * @date 2023/7/19 2:11 PM
 */
@Service("internationalPrintManager")
public class InternationalPrintManagerImpl implements InternationalPrintManager {

    private static final Logger logger = LoggerFactory.getLogger(InternationalPdfPrintHandler.class);
    
    /**
     * rest请求content-type
     */
    private static final String REST_CONTENT_TYPE = "application/json; charset=UTF-8";
    
    /**
     * 云打印url
     */
    @Value("${print.out.config.internationalCloudPrintUrl}")
    private String internationalCloudPrintUrl;


    @Override
    public String generatePdfUrl(InternationalPrintReq request) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo("com.jd.bluedragon.core.base.InternationalPrintManager.jdCloudPrint");
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod method = new PostMethod(internationalCloudPrintUrl);
            method.addRequestHeader("Content-type", REST_CONTENT_TYPE);
            method.addRequestHeader("Accept", REST_CONTENT_TYPE);
            method.setRequestEntity(new StringRequestEntity(JsonHelper.toJson(request), REST_CONTENT_TYPE, StandardCharsets.UTF_8.toString()));
            int statusCode = httpClient.executeMethod(method);
            if (statusCode == HttpStatus.OK.value()) {
                InternationalPrintRes result = JsonHelper.jsonToObject(method.getResponseBodyAsString(), InternationalPrintRes.class);


            }
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            logger.error("国际化调用云打印失败！req:{}",JsonHelper.toJson(request), e);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
//        return null;
        return new Random().nextInt(2) == 1 ? "" :  "www.baidu.com";
    }
    
}
