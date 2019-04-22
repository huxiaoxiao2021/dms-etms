package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReviewWeightSpotCheckController
 * @Description: 分拣复重抽检任务Controller
 * @author: hujiping
 * @date: 2019/4/17 15:47
 */
@Controller
@RequestMapping("/reviewWeightSpotCheck")
public class ReviewWeightSpotCheckController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(ReviewWeightSpotCheckController.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private ReceiveWeightCheckService receiveWeightCheckService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String userCode = "";
        Long createSiteCode = new Long(-1);
        Integer orgId = new Integer(-1);
        if(erpUser != null){
            userCode = erpUser.getUserCode();
            BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
            if (bssod!=null && bssod.getSiteType() == 64) {
                createSiteCode = new Long(bssod.getSiteCode());
                orgId = bssod.getOrgId();
            }
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
    public PagerResult<WeightAndVolumeCheck> listData(@RequestBody WeightAndVolumeCheckCondition condition){

        PagerResult<WeightAndVolumeCheck> result = new PagerResult<WeightAndVolumeCheck>();

        Integer num = 3;
        List<WeightAndVolumeCheck> list = new ArrayList<WeightAndVolumeCheck>();
        WeightAndVolumeCheck weightAndVolumeCheck = new WeightAndVolumeCheck();
        list.add(weightAndVolumeCheck);
        result.setRows(list);
        result.setTotal(num);

        return result;
    }

    /**
     * 导入
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R)
    @RequestMapping(value = "/toImport", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImport(@RequestParam("importExcelFile") MultipartFile file) {
        logger.debug("uploadExcelFile begin...");
        JdResponse response = new JdResponse();
        try {
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith("xlsx")) {
            return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
        }

        DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(2);
        List<ReviewWeightSpotCheck> dataList = dataResolver.resolver(file.getInputStream(), ReviewWeightSpotCheck.class, new PropertiesMetaDataFactory("/excel/reviewWeightSpotCheck.properties"));
        String errorMessage = checkExportData(dataList);


        } catch (Exception e) {
            this.logger.error("导入失败!");
        }


        return response;

    }

    private String checkExportData(List<ReviewWeightSpotCheck> dataList) {

        return null;
    }

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(WeightAndVolumeCheckCondition condition, Model model) {

        this.logger.info("分拣复重抽查任务统计表");
        try{
            List<List<Object>> resultList = receiveWeightCheckService.getExportData(condition);
            model.addAttribute("filename", "分拣复重抽检任务统计表.xls");
            model.addAttribute("sheetname", "分拣复重抽检任务统计结果");
            model.addAttribute("contents", resultList);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        }catch (Exception e){
            this.logger.error("导出分拣复重抽检任务统计表失败:" + e.getMessage(), e);
            return null;
        }
    }


}
