package com.jd.bluedragon.distribution.web.material;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.material.service.MaterialOperationService;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanQuery;
import com.jd.bluedragon.distribution.material.vo.RecycleMaterialScanVO;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName RecycleMaterialScanController
 * @Description
 * @Author wyh
 * @Date 2020/2/27 16:09
 **/
@Controller
@RequestMapping("/recycleMaterialScan")
public class RecycleMaterialScanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecycleMaterialScanController.class);

    @Autowired
    private MaterialOperationService materialOperationService;

    @Autowired
    BaseMajorManager baseMajorManager;

    @Authorization(Constants.DMS_WEB_RECYCLE_MATERIAL_SCAN_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "material/warmbox_inout_list";
    }

    @Authorization(Constants.DMS_WEB_RECYCLE_MATERIAL_SCAN_R)
    @RequestMapping(value = "/listData", method = RequestMethod.GET)
    public @ResponseBody PagerResult<RecycleMaterialScanVO> list(@RequestBody RecycleMaterialScanQuery query){
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (null != erpUser) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
            Long createSiteCode = -1L;
            if (baseStaffSiteOrgDto != null && baseStaffSiteOrgDto.getSiteType().equals(Constants.DMS_SITE_TYPE)) {
                createSiteCode = new Long(baseStaffSiteOrgDto.getSiteCode());
            }
            query.setCreateSiteCode(createSiteCode);
        }
        return materialOperationService.queryByPagerCondition(query);
    }
}
