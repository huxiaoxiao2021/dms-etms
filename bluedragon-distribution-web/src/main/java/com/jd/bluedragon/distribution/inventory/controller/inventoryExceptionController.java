package com.jd.bluedragon.distribution.inventory.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/inventoryException")
public class inventoryExceptionController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(inventoryExceptionController.class);

    @Value("${waybill.trace.url:waybill.test.jd.com}")
    private String waybillTraceUrl;

    @Autowired
    private InventoryExceptionService inventoryExceptionService;

    /**
     * 返回主页面
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = -1;
        Integer orgId = -1;
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
            orgId = loginUser.getOrgId();
        }
        model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode).addAttribute("url",this.waybillTraceUrl);
        return "/inventory/inventoryException";
    }

    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<InventoryException> listData(@RequestBody InventoryExceptionCondition condition){

        PagerResult<InventoryException> result = new PagerResult<>();
        try {
            result = inventoryExceptionService.queryByPagerCondition(condition);
        } catch (Exception e) {
            logger.error("获取清场异常记录分页数据异常", e);
        }

        return result;
    }

    /**
     * 导出
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(InventoryExceptionCondition condition, Model model) {

        logger.info("导出转运清场异常结果");
        try{
            List<List<Object>> resultList = inventoryExceptionService.getExportData(condition);
            model.addAttribute("filename", "转运清场异常统计表.xls");
            model.addAttribute("sheetname", "转运清场异常统计结果");
            model.addAttribute("contents", resultList);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        }catch (Exception e){
            logger.error("导出转运清场异常统计表失败:" + e.getMessage(), e);
            return null;
        }
    }


    /**
     * 根据id确认多条数据
     * @param records
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping(value = "/handle", method = RequestMethod.POST)
    public @ResponseBody JdResponse<Integer> handle(@RequestBody List<Long> records) {
        JdResponse<Integer> result = new JdResponse<Integer>();
        try {
            LoginUser loginUser = getLoginUser();
            result.setData(inventoryExceptionService.handleException(records, loginUser));
        } catch (Exception e) {
            logger.error("处理转运清场异常失败："+e.getMessage(),e);
            result.toError("确认失败，服务异常！");
        }
        return result;
    }

}
