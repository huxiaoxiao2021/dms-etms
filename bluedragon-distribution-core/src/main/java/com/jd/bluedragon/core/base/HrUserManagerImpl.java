package com.jd.bluedragon.core.base;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.fastjson.JSON;
import com.jd.fastjson.JSONObject;
import com.jd.official.omdm.is.hr.HrUserService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.Date;
import java.util.Objects;

/**
 * 人资接口manager实现
 *
 * @author hujiping
 * @date 2022/12/19 2:23 PM
 */
@Service("hrUserManager")
public class HrUserManagerImpl implements HrUserManager{

    private static final Logger logger = LoggerFactory.getLogger(HrUserManagerImpl.class);

    @Value("${hr.hrAppCode:001}")
    private String hrAppCode;
    @Value("${hr.hrToken:abc123}")
    private String hrToken;

    @Autowired
    private HrUserService hrUserService;

    @Override
    public String getSuperiorErp(String userErp) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.HrUserManager.getSuperiorErp",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(StringUtils.isEmpty(userErp)){
                return null;
            }
            String businessId = String.valueOf(System.currentTimeMillis());
            String requestTimestamp = DateHelper.formatDate(new Date(), Constants.DATE_TIME_MS_FORMAT);
            String responseFormat = "JSON";
            String sign = DigestUtils.md5Hex((hrAppCode + businessId + requestTimestamp + hrToken + userErp).getBytes());
            String result = hrUserService.getSuperiorBaseInfo(hrAppCode, businessId, requestTimestamp, sign, responseFormat, userErp);
            result = URLDecoder.decode(result, "UTF-8");
            if(result == null){
                logger.warn("根据erp:{}获取直属上级用户信息失败!", userErp);
                return null;
            }
            JSONObject jsonResult = JSON.parseObject(result);
            String resStatus = jsonResult.get("resStatus").toString();
            String responseBody = jsonResult.get("responsebody").toString();
            if(!Objects.equals(resStatus, String.valueOf(200)) || StringUtils.isEmpty(responseBody)){
                logger.warn("根据erp:{}获取直属上级用户信息失败!", userErp);
                return null;
            }
            JSONObject bodyResult = JSON.parseObject(responseBody);
            return bodyResult.get("userName").toString();
        }catch (Exception e){
            logger.error("根据erp:{}获取直属上级用户信息异常!", userErp, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
}
