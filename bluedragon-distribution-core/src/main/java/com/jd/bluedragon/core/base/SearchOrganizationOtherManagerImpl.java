package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ufo.common.utility.ResponseObject;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import com.jd.ufo.saf.SearchOrganizationOtherService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 财务其他查询服务包装
 */
@Service("searchOrganizationOtherManager")
public class SearchOrganizationOtherManagerImpl implements SearchOrganizationOtherManager {

    private Logger log = LoggerFactory.getLogger(SearchOrganizationOtherManagerImpl.class);

    @Autowired
    @Qualifier("searchOrganizationOtherServiceJsf")
    private SearchOrganizationOtherService searchOrganizationOtherServiceJsf;


    @Override
    @JProfiler(jKey = "DMS.BASE.SearchOrganizationOtherManagerImpl.findFinancialOrg", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public Organization findFinancialOrg(SendpayOrdertype var1) {

        try{
            ResponseObject responseObject = searchOrganizationOtherServiceJsf.findFinancialOrg(var1);
            if(responseObject.isSuccess()){
                return (Organization)responseObject.getObject();
            }else{
                log.warn("获取发票机构ID失败：{}-{}",JsonHelper.toJson(var1),JsonHelper.toJson(responseObject));
                return null;
            }

        }catch (Exception e){
            log.error("获取发票机构ID异常:{}", JsonHelper.toJson(var1),e);
            return null;
        }

    }
}
