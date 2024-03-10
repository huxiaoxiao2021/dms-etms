package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
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

    @Cache(key = "hrUserManager.getSuperiorErp@args0", memoryEnable = true, memoryExpiredTime = 5 * 1000,
            redisEnable = false)
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

    /**
     * 根据组织机构编码获取机构负责人erp
     * @param organizationCode
     * @return
     */
    @Override
    public String getSuperiorErpByOrganizationCode(String organizationCode){
        if(StringUtils.isBlank(organizationCode)){
            logger.warn("查询场地负责人erp,入参机构编码为空");
            return null;
        }
        HRUserPublicParam publicParam = getHRUserPublicParam(organizationCode);
        //接口文档 https://cf.jd.com/pages/viewpage.action?pageId=91295469
        String result = hrUserService.getLeadershipBaseInfo(hrAppCode, publicParam.getBusinessId(), publicParam.getRequestTimestamp(), 
                publicParam.getSign(), publicParam.getResponseFormat(), organizationCode);
        return getAttributeValue(result, "userName",organizationCode, "查询机构负责人基础信息");
    }
    
    private String getAttributeValue(String result, String attributeName, String queryParam, String functionName){
        if(StringUtils.isBlank(result)){
            logger.error("根据参数:{},{}失败,返回结果为空", queryParam, functionName);
            return null;
        }
        try {
            result = URLDecoder.decode(result, "UTF-8");
            JSONObject jsonResult = JSON.parseObject(result);
            String resStatus = jsonResult.get("resStatus").toString();
            String responseBody = jsonResult.get("responsebody").toString();
            if(!Objects.equals(resStatus, String.valueOf(200)) || StringUtils.isEmpty(responseBody)){
                logger.error("根据参数:{},{}失败,返回查询状态失败，resStatus:{}", queryParam, functionName, resStatus);
                return null;
            }
            JSONObject bodyResult = JSON.parseObject(responseBody);
            return bodyResult.get(attributeName).toString();
        } catch (Exception e) {
            logger.error("根据参数:{},{}，解析返回值时异常", queryParam, functionName, e);
            return null;
        }
    }

    private HRUserPublicParam getHRUserPublicParam(String queryParam){
        HRUserPublicParam publicParam = new HRUserPublicParam();
        String businessId = String.valueOf(System.currentTimeMillis());
        publicParam.setBusinessId(businessId);
        String requestTimestamp = DateHelper.formatDate(new Date(), Constants.DATE_TIME_MS_FORMAT);
        publicParam.setRequestTimestamp(requestTimestamp);
        String responseFormat = "JSON";
        publicParam.setResponseFormat(responseFormat);
        String sign =  DigestUtils.md5Hex((hrAppCode + businessId + requestTimestamp + hrToken + queryParam).getBytes());
        publicParam.setSign(sign);
        return publicParam;
    }


    class  HRUserPublicParam{
        //业务流水号，客户端的业务流水号
        private String businessId;
        //时间戳，主数据平台允许客户端请求时间误差为5分钟。
        private String requestTimestamp;
        //JSON
        private String responseFormat;
        //API输入参数签名结果(接入应用需要用主数据平台分配的加密Key进行md5加密，全小写)
        //sign =MD5（appCode + businessId + requestTimestamp+SAFETY_KEY+ 查询参数）
        private String sign;

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public String getRequestTimestamp() {
            return requestTimestamp;
        }

        public void setRequestTimestamp(String requestTimestamp) {
            this.requestTimestamp = requestTimestamp;
        }

        public String getResponseFormat() {
            return responseFormat;
        }

        public void setResponseFormat(String responseFormat) {
            this.responseFormat = responseFormat;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
