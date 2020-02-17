package com.jd.bluedragon.distribution.web;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-4-18 上午11:38:07
 * <p>
 * 当前登录用户信息获取
 */
public class ErpUserClient {
    private static final Logger log = LoggerFactory.getLogger(ErpUserClient.class);
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
        erpUser.setUserId((int) loginContext.getUserId());
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
