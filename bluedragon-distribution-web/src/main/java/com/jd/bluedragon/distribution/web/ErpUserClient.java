package com.jd.bluedragon.distribution.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.common.hrm.UimHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-4-18 上午11:38:07
 * <p>
 * 当前登录用户信息获取
 */
@Component
public class ErpUserClient {

    private static final Logger log = LoggerFactory.getLogger(ErpUserClient.class);

    @Value("${sso.clientId}")
    private String appKey;
    @Value("${sso.clientSecret}")
    private String token;
    @Value("${sso.target}")
    private String target;

    private static String ssoToken;
    private static String ssoAppKey;
    private static String ssoTarget;

    @PostConstruct
    public void setStaticData() {
        ssoAppKey = this.appKey;
        ssoToken = this.token;
        ssoTarget = this.target;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static ErpUser getCurrUser() {
        LoginContext loginContext = LoginContext.getLoginContext();
        if (loginContext == null) {
            return null;
        }
        ErpUser erpUser = new ErpUser();
        erpUser.setUserId(getLoginUserId(loginContext));
        erpUser.setUserCode(loginContext.getPin());
        erpUser.setUserName(loginContext.getNick());
        BaseMajorManager baseMajorManager = (BaseMajorManager) SpringHelper.getBean("baseMajorManager");
        if (baseMajorManager != null) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            if(baseStaffSiteOrgDto != null){
                log.info("员工编号为空usercode[{}]username[{}]",erpUser.getUserCode(),erpUser.getUserName());
                erpUser.setStaffNo(baseStaffSiteOrgDto.getStaffNo());
            }
        }
        return erpUser;
    }

    public static Integer getLoginUserId(LoginContext loginContext) {
        Long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();
        String erp = loginContext.getPin();
        String tenantCode = loginContext.getTenantCode();
        Map<String,String> parameterMap = new HashMap<String,String>();
        parameterMap.put("erp", erp);
        parameterMap.put("tenantCode", tenantCode);
        String bizParams= JSON.toJSONString(parameterMap);
        //这里签名生成方法中的md5算法，可以使用自己系统的MD5标准工具类
        String signature = Md5Helper.getMd5(ssoToken + bizParams + timestamp);
        Map<String, String> params = new HashMap<>();
        params.put("erp", erp);
        params.put("tenantCode", tenantCode);
        params.put("appkey", ssoAppKey);
        params.put("signature", signature);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("nonce", nonce);
        String content = UimHelper.doPost(ssoTarget, params);
        //返回内容:{"code":200,"data":1906928,"message":"ok","success":true}
        JSONObject jsonObject = JSON.parseObject(content);
        if(jsonObject == null){
            log.warn("根据erp:{}获取用户id失败!", loginContext.getPin());
            return null;
        }
        if(Objects.equals(jsonObject.getInteger("code"), 200)){
            return jsonObject.getInteger("data");
        }
        log.warn("获取登录人userId失败:{}", jsonObject.getString("message"));
        return null;
    }

    /**
     * @author zhaohc
     * 当前登录用户类
     */
    public static class ErpUser {
        /**
         * 当前登录用户ID
         */
        private Integer userId;
        /**
         * 当前登录用户ERP账号
         */
        private String userCode;
        /**
         * 当前登录用户名称
         */
        private String userName;

        /**
         * 青龙用户编号
         */
        private Integer staffNo;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Integer getStaffNo() {
            return staffNo;
        }

        public void setStaffNo(Integer staffNo) {
            this.staffNo = staffNo;
        }
    }
}
