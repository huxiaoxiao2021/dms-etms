package com.jd.bluedragon.distribution.web.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.dao.FreshWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.FreshWaybill;
import com.jd.bluedragon.distribution.waybill.service.FreshWaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/4/28
 */

@Controller
@RequestMapping("/fresh")
public class FreshWaybillController {
    private static final Logger log = LoggerFactory.getLogger(FreshWaybillController.class);

    private static final Integer RECORD_IS_EXISTED = 700;
    private static final String  RECODE_IS_EXISTED_MESSAGE = "此包裹数据已存在，请修改";

    @Autowired
    private FreshWaybillService freshWaybillService;

    @Autowired
    private FreshWaybillDao freshWaybillDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Authorization(Constants.DMS_WEB_SORTING_FRESH_R)
    @RequestMapping(value ="/index", method = RequestMethod.GET)
    public String toFreshWaybillIndex(){
        return "waybill/freshWaybill";
    }

    @Authorization(Constants.DMS_WEB_SORTING_FRESH_R)
    @RequestMapping(value ="/toAddPage", method = RequestMethod.GET)
    public String toFreshWaybillAdd() {
        return "waybill/freshWaybill_add";
    }

    @Authorization(Constants.DMS_WEB_SORTING_FRESH_R)
    @RequestMapping(value = "/toModifyPage",method = RequestMethod.GET)
    public String toFreshWaybillModify(FreshWaybill freshWaybill, Model model){
        if(null == freshWaybill || null == freshWaybill.getId()) {
            return "waybill/freshWaybill_modify";
        }

        List<FreshWaybill> freshWaybills = freshWaybillDao.getFreshWaybillByID(freshWaybill);
        model.addAttribute("freshWaybill",freshWaybills.get(0));
        return "waybill/freshWaybill_modify";
    }

    @Authorization(Constants.DMS_WEB_SORTING_FRESH_R)
    @RequestMapping(value ="/query", method = RequestMethod.GET)
    public String doQueryFreshWaybill(FreshWaybill freshWaybill, Pager<FreshWaybill> pager, Model model){
        if(null == freshWaybill) {
            return "waybill/freshWaybill";
        }
        if(null != freshWaybill.getPackageCode()) {
            freshWaybill.setPackageCode(freshWaybill.getPackageCode().trim());
            if(freshWaybill.getPackageCode().length() <= 0) {
                freshWaybill.setPackageCode(null);
            }
        }
        Map<String, Object> params = ObjectMapHelper.makeObject2Map(freshWaybill);
        // 设置分页对象
        if (pager == null) {
            pager = new Pager<FreshWaybill>(Pager.DEFAULT_PAGE_NO);
        } else {
            pager = new Pager<FreshWaybill>(pager.getPageNo(), pager.getPageSize());
        }

        params.putAll(ObjectMapHelper.makeObject2Map(pager));

        // 获取总数量
        int totalsize = freshWaybillDao.getFreshWaybillCountByCode(freshWaybill);
        pager.setTotalSize(totalsize);

        log.info("查询符合条件的规则数量：{}", totalsize);

        model.addAttribute("freshWaybills", freshWaybillDao.getFreshWaybillPage(params));
        model.addAttribute("freshWaybillDto", freshWaybill);
        model.addAttribute("pager", pager);

        return "waybill/freshWaybill";
    }

    @Authorization(Constants.DMS_WEB_SORTING_FRESH_R)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> doAddFreshWaybill(FreshWaybill freshWaybill) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();

        if(null == freshWaybill || null == freshWaybill.getPackageCode()) {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }

        try{
            Integer waybillCount = freshWaybillDao.getFreshWaybillCountByCode(freshWaybill);
            if(waybillCount > 0) {
                result.setCode(RECORD_IS_EXISTED);
                result.setMessage(RECODE_IS_EXISTED_MESSAGE);
                return result;
            }
            fillUserInfo(freshWaybill);
            Integer effectCount = freshWaybillDao.addFreshWaybill(freshWaybill);
        } catch (Exception ex) {
            log.error("增加生鲜温度失败",ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        return result;
    }


    @Authorization(Constants.DMS_WEB_SORTING_FRESH_R)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> doUpdateFreshWaybill(FreshWaybill freshWaybill) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();

        if(null == freshWaybill || null == freshWaybill.getPackageCode()) {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }

        try {
            fillUserInfo(freshWaybill);
            freshWaybillService.updateFreshWaybill(freshWaybill);
        } catch (Exception e) {
            log.error("doUpddateFreshWaybill:异常",e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        return result;
    }


    private void fillUserInfo(FreshWaybill freshWaybill) {
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if(log.isDebugEnabled()){
            log.debug("生鲜温度录入用户信息：{}", JsonHelper.toJson(erpUser));
        }
//        ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
//        erpUser.setUserId(11535);
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        freshWaybill.setUserName(baseStaffSiteOrgDto.getStaffName());
        freshWaybill.setUserCode(baseStaffSiteOrgDto.getsId());
        freshWaybill.setUserDmsId(baseStaffSiteOrgDto.getSiteCode());
        freshWaybill.setUserDmsName(baseStaffSiteOrgDto.getSiteName());
        freshWaybill.setUserOrgId(baseStaffSiteOrgDto.getOrgId());
        freshWaybill.setUserOrgName(baseStaffSiteOrgDto.getOrgName());
    }
}
