package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckExcelData;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.ReviewWeightSpotCheckService;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

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
    @JProfiler(jKey = "com.jd.bluedragon.distribution.weightAndVolumeCheck.controller.ReviewWeightSpotCheckController.listData", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
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
    @JProfiler(jKey = "com.jd.bluedragon.distribution.weightAndVolumeCheck.controller.ReviewWeightSpotCheckController.toImport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
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
            List<SpotCheckExcelData> dataList = dataResolver.resolver(file.getInputStream(), SpotCheckExcelData.class, new PropertiesMetaDataFactory("/excel/reviewWeightSpotCheck.properties"));
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
     * 导入
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_SPECIAL_R)
    @RequestMapping(value = "/toImportSpot", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImportSpot(@RequestParam("importExcelFileSpot") MultipartFile file) {
        JdResponse response = new JdResponse();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();

//            ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
//            erpUser.setUserCode("liuduo8");
//            erpUser.setUserCode("wuzuxiang");
//            erpUser.setUserCode("hujiping1");
            if(erpUser == null || (!Objects.equals(erpUser.getUserCode(), "hujiping1")
                    && !Objects.equals(erpUser.getUserCode(), "wuzuxiang")
                    && !Objects.equals(erpUser.getUserCode(), "liuduo8"))){
                response.toFail("非法导入!");
                return response;
            }
            long startTime = System.currentTimeMillis();
            log.info("{},此次抽检数据导入开始!", startTime);
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xlsx")) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(2);
            List<WeightVolumeSpotCheckDto> dataList = dataResolver.resolver(file.getInputStream(), WeightVolumeSpotCheckDto.class, new PropertiesMetaDataFactory("/excel/weightAndSpotCheck.properties"));
            spotCheckDealService.brushSpotCheck(dataList, erpUser.getUserCode());
            long endTime = System.currentTimeMillis();
            log.info("{},此次抽检数据导入结束!耗时:{}s", endTime, (endTime - startTime) / 1000);
        } catch (Exception e) {
            log.error("导入异常!",e);
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
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.weightAndVolumeCheck.controller.ReviewWeightSpotCheckController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult toExport(WeightAndVolumeCheckCondition condition,  HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        this.log.info("分拣复重抽查任务统计表");
        try{
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.REVIEW_WEIGHT_SPOT_CHECK_REPORT.getCode());
            String fileName = "分拣复重抽查任务统计表";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            reviewWeightSpotCheckService.getExportData(condition,bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.REVIEW_WEIGHT_SPOT_CHECK_REPORT.getCode());
        }catch (Exception e){
            this.log.error("导出分拣复重抽检任务统计表失败:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException es) {
                log.error("分拣复重抽查任务统计表 流关闭异常", es);
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
            }
        }
        return result;
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
