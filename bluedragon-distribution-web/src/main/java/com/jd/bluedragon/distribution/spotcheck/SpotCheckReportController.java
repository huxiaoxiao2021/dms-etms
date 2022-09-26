package com.jd.bluedragon.distribution.spotcheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckReportService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightVolumePictureDto;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.*;

/**
 * 抽检报表
 *
 * @author hujiping
 * @date 2021/12/3 8:41 下午
 */
@Controller
@RequestMapping("spotCheckReport")
public class SpotCheckReportController extends DmsBaseController {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckReportController.class);

    @Autowired
    private SpotCheckReportService spotCheckReportService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        return "/spotcheck/spotCheckReport";
    }

    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<WeightVolumeSpotCheckDto> listData(@RequestBody SpotCheckReportQueryCondition condition){
        return spotCheckReportService.listData(condition);
    }

    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping("/packageDetailListData")
    @ResponseBody
    public PagerResult<WeightVolumeSpotCheckDto> packageDetailListData(@RequestBody SpotCheckReportQueryCondition condition){
        return spotCheckReportService.packageDetailListData(condition);
    }

    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping(value = "/toExport")
    @JProfiler(jKey = "dmsWeb.SpotCheckReportController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void toExport(SpotCheckReportQueryCondition condition, HttpServletResponse response) {
        BufferedWriter bfw = null;
        try{
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getCode());
            if(StringUtils.isNotBlank(condition.getMerchantName())){
                condition.setMerchantName(URLDecoder.decode(condition.getMerchantName(), "UTF-8"));
            }
            String fileName = "重量体积抽检统计表";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            spotCheckReportService.export(condition,bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getCode());
        }catch (Exception e){
            logger.error("exportData error", e);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                logger.error("export-error", e);
            }
        }
    }


    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping(value = "/securityCheck/{waybillCode}", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<Boolean> securityCheck(@PathVariable("waybillCode") String waybillCode) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        if (erpUser == null || StringUtils.isEmpty(erpUser.getUserCode())) {
            result.error("获取当前登录用户信息失败，请重新登录ERP后尝试");
            return result;
        }
        return spotCheckReportService.securityCheck(waybillCode, erpUser.getUserCode());
    }

    /**
     * 跳转图片查询页面
     *
     * @param waybillCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping(value = "/toSearchPicture")
    public String toSearchPicture(@QueryParam("waybillCode")String waybillCode,
                                                  @QueryParam("reviewSiteCode")Integer reviewSiteCode,
                                                  @QueryParam("reviewSource")Integer reviewSource, Model model){
        model.addAttribute("reviewSiteCode",reviewSiteCode);
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("reviewSource",reviewSource);
        return "/spotcheck/spotCheckPackPic";
    }

    /**
     * 查看超标图片
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping(value = "/searchPicture", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<WeightVolumePictureDto>> searchPicture(@RequestBody SpotCheckReportQueryCondition condition) {
        return spotCheckReportService.searchPicture(condition);
    }

    /**
     * 跳转上传页面
     *
     * @param waybillCode
     * @param packageCode
     * @param reviewSiteCode
     * @param model
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_SPOT_CHECK_REPORT_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillCode")String waybillCode,
                           @QueryParam("packageCode")String packageCode,
                           @QueryParam("reviewSiteCode")String reviewSiteCode, Model model) {
        model.addAttribute("waybillCode",waybillCode);
        model.addAttribute("packageCode",packageCode);
        model.addAttribute("reviewSiteCode",reviewSiteCode);
        return "spotcheck/excessPictureUpload";
    }

    /**
     * 上传超标图片
     *
     * @param image
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Boolean> uploadExcessPicture(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        return spotCheckReportService.uploadExcessPicture(image, request);
    }
}
