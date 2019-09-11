package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ufo.common.utility.ResponseObject;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import com.jd.ufo.saf.SearchOrganizationOtherService;
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

    private Logger logger = LoggerFactory.getLogger(SearchOrganizationOtherManagerImpl.class);

    @Autowired
    @Qualifier("searchOrganizationOtherServiceJsf")
    private SearchOrganizationOtherService searchOrganizationOtherServiceJsf;


    @Override
    public Organization findFinancialOrg(SendpayOrdertype var1) {

        try{
            ResponseObject responseObject = searchOrganizationOtherServiceJsf.findFinancialOrg(var1);
            if(responseObject.isSuccess()){
                return (Organization)responseObject.getObject();
            }else{
                logger.error("获取发票机构ID失败"+ JsonHelper.toJson(var1)+" "+JsonHelper.toJson(responseObject));
                return null;
            }

        }catch (Exception e){
            logger.error("获取发票机构ID异常"+ JsonHelper.toJson(var1),e);
            return null;
        }

    }
}
