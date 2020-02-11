package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReviewWeightSpotCheckController
 * @Description: 复重抽检任务导入Controller
 * @author: hujiping
 * @date: 2019/4/17 15:47
 */
@Controller
@RequestMapping("/reviewWeightSpotCheck")
public class ReviewWeightSpotCheckController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(ReviewWeightSpotCheckController.class);

    @Autowired
    private ReviewWeightSpotCheckService reviewWeightSpotCheckService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){

        Integer createSiteCode = new Integer(-1);
        Integer orgId = new Integer(-1);
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
            orgId = loginUser.getOrgId();
        }
        model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode);
        return "/weightAndVolumeCheck/reviewWeightSpotCheck";
    }

    /**
     * 获取明细
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<ReviewWeightSpotCheck> listData(@RequestBody WeightAndVolumeCheckCondition condition){

        PagerResult<ReviewWeightSpotCheck> result = reviewWeightSpotCheckService.listData(condition);
        return result;
    }

    /**
     * 导入
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_SPECIAL_R)
    @RequestMapping(value = "/toImport", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImport(@RequestParam("importExcelFile") MultipartFile file) {
        log.debug("uploadExcelFile begin...");
        JdResponse response = new JdResponse();
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xlsx")) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            String importErpCode = erpUser.getUserCode();

            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(2);
            List<SpotCheckInfo> dataList = dataResolver.resolver(file.getInputStream(), SpotCheckInfo.class, new PropertiesMetaDataFactory("/excel/reviewWeightSpotCheck.properties"));
            String errorMessage = reviewWeightSpotCheckService.checkExportData(dataList,importErpCode);
            if (!"".equals(errorMessage)) {
                return new JdResponse(JdResponse.CODE_FAIL, errorMessage);
            }
        } catch (Exception e) {
            this.log.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
        return response;
    }

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(WeightAndVolumeCheckCondition condition, Model model) {

        this.log.info("分拣复重抽查任务统计表");
        List<List<Object>> resultList;
        try{
            model.addAttribute("filename", "分拣复重抽检任务统计表.xls");
            model.addAttribute("sheetname", "分拣复重抽检任务统计结果");
            resultList = reviewWeightSpotCheckService.getExportData(condition);
        }catch (Exception e){
            this.log.error("导出分拣复重抽检任务统计表失败:", e);
            List<Object> list = new ArrayList<>();
            list.add("导出分拣复重抽检任务统计表失败!");
            resultList = new ArrayList<>();
            resultList.add(list);
        }
        model.addAttribute("contents", resultList);
        return new ModelAndView(new DefaultExcelView(), model.asMap());
    }

    /**
     * 导出抽查任务表
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/toExportSpot", method = RequestMethod.POST)
    public ModelAndView toExportSpot(Model model) {

        this.log.info("导出抽查任务表");
        List<List<Object>> resultList;
        try{
            model.addAttribute("filename", "抽查任务表.xls");
            model.addAttribute("sheetname", "抽查统计结果");
            resultList = reviewWeightSpotCheckService.exportSpotData();
        }catch (Exception e){
            this.log.error("导出抽查任务表失败:", e);
            List<Object> list = new ArrayList<>();
            list.add("导出抽查任务表失败!");
            resultList = new ArrayList<>();
            resultList.add(list);
        }
        model.addAttribute("contents", resultList);
        return new ModelAndView(new DefaultExcelView(), model.asMap());
    }

}
