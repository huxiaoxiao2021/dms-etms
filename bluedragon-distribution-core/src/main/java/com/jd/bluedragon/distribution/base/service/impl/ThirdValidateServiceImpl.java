package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.UserVerifyManager;
import com.jd.bluedragon.distribution.base.domain.BasePdaUserDto;
import com.jd.bluedragon.distribution.base.service.AbstractClient;
import com.jd.bluedragon.distribution.base.service.ErpValidateService;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description: 三方商家校验
 * @author: lql
 * @date: 2020/8/21 9:10
 */
@Service("thirdValidateService")
public class ThirdValidateServiceImpl implements ErpValidateService {

    @Autowired
    private UserVerifyManager userVerifyManager;
    @Autowired
    private BaseMajorManager baseMajorManager;

    /** 日志 */
    private Logger log = LoggerFactory.getLogger(ThirdValidateServiceImpl.class);

    @Override
    public BasePdaUserDto pdaUserLogin(String userid, String password, ClientInfo clientInfo, Byte loginVersion) {
        BasePdaUserDto basePdaUserDto = new BasePdaUserDto();
        try {
            String thirdUserId = userid.replaceAll(Constants.PDA_THIRDPL_TYPE, "");
            // 京东用户组接口验证
            BasePdaUserDto basePdaUserDtoNew = userVerifyManager.passportVerify(thirdUserId, password, clientInfo, loginVersion);
            if (basePdaUserDtoNew.getErrorCode().equals(Constants.PDA_USER_GETINFO_SUCCESS)) {
                // 用户组接口验证通过后，从基础资料获取具体信息
                BaseStaffSiteOrgDto baseStaffDto = baseMajorManager.getThirdStaffByJdAccountNoCache(thirdUserId);
                if (null == baseStaffDto) {
                    basePdaUserDto.setErrorCode(Constants.PDA_USER_GETINFO_FAILUE);
                    basePdaUserDto.setMessage(Constants.PDA_USER_GETINFO_FAILUE_MSG);
                } else {
                    fillPdaUserDto(basePdaUserDto, baseStaffDto, password);
                }
            } else {
                basePdaUserDto.setErrorCode(basePdaUserDtoNew.getErrorCode());
                basePdaUserDto.setMessage(basePdaUserDtoNew.getMessage());
            }
        } catch (Exception e) {
            log.error("user login error {}" , userid, e);
            basePdaUserDto.setErrorCode(Constants.PDA_USER_ABNORMAL);
            basePdaUserDto.setMessage(Constants.PDA_USER_ABNORMAL_MSG);
        }
        return basePdaUserDto;
    }


    protected void fillPdaUserDto(BasePdaUserDto basePdaUserDto, BaseStaffSiteOrgDto baseStaffDto, String password) {
        basePdaUserDto.setSiteId(baseStaffDto.getSiteCode());
        basePdaUserDto.setSiteName(baseStaffDto.getSiteName());
        basePdaUserDto.setDmsCode(baseStaffDto.getDmsSiteCode());
        basePdaUserDto.setLoginTime(new Date());
        basePdaUserDto.setStaffId(baseStaffDto.getStaffNo());
        basePdaUserDto.setPassword(password);
        basePdaUserDto.setStaffName(baseStaffDto.getStaffName());
        basePdaUserDto.setErrorCode(Constants.PDA_USER_GETINFO_SUCCESS);
        basePdaUserDto.setMessage(Constants.PDA_USER_GETINFO_SUCCESS_MSG);
        basePdaUserDto.setOrganizationId(baseStaffDto.getOrgId());
        basePdaUserDto.setOrganizationName(baseStaffDto.getOrgName());
        basePdaUserDto.setSiteType(baseStaffDto.getSiteType());
        basePdaUserDto.setSubType(baseStaffDto.getSubType());
    }
}
