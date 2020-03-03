package com.jd.bluedragon.distribution.web.inspectionCheck;


import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.inspection.InspectionCheckCondition;
import com.jd.bluedragon.distribution.inspection.InsepctionCheckDto;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang.StringUtils;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * @author lijie
 * @date 2020/2/19 15:07
 */
@Controller
@RequestMapping("/inspectionCheck")
public class InspectionCheckController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(InspectionCheckController.class);

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Authorization()
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "/inspectionCheck/inspectionCheckIndex";
    }

    @Authorization()
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<InsepctionCheckDto> listData(@RequestBody InspectionCheckCondition condition){

        LoginUser user = getLoginUser();
        if(user != null && user.getSiteCode() != null){
            condition.setCreateSiteCode(user.getSiteCode());
        }

        if(condition != null && condition.getOperatorErp() != null){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(condition.getOperatorErp());
            condition.setCreateUserCode(baseStaffSiteOrgDto.getStaffNo());
        }

        PagerResult<InsepctionCheckDto> result = inspectionService.findInspectionGather(condition);
        return result;
    }

    /**
     * 查看已验包裹页面
     * @return
     */
    @Authorization()
    @RequestMapping("/listPackage")
    public String toListPackage() {
        return "/inspectionCheck/listPackage";
    }

    /**
     * 查询已验包裹页面
     * @return
     */
    @Authorization()
    @RequestMapping("/querylistPackage")
    @ResponseBody
    public PagerResult<Inspection> querylistPackage(@RequestBody InspectionCheckCondition condition) {

        Inspection inspection = new Inspection();
        inspection.setWaybillCode(condition.getWaybillCode());

        LoginUser user = getLoginUser();
        if(user != null && user.getSiteCode() != null){
            inspection.setCreateSiteCode(user.getSiteCode());
        }

        return inspectionService.findInspetionedPacks(inspection);
    }

}
