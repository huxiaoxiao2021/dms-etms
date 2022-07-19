package com.jd.bluedragon.distribution.enterpriseDistribution.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.enterpriseDistribution.domain.QualityInspectionQueryCondition;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDetailDto;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDto;
import com.jd.bluedragon.distribution.enterpriseDistribution.service.EnterpriseDistributionService;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * @Description 企配仓服务
 * @Author chenjunyan
 * @Date 2022/6/14
 */
@Controller
@RequestMapping("enterpriseDistribution")
public class EnterpriseDistributionController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseDistributionController .class);

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Autowired
    private EnterpriseDistributionService enterpriseDistributionService;

    /**
     * 企配仓界面
     * @return
     */
    @RequestMapping(value = "/toQualityInspection")
    @Authorization(Constants.ENTERPRISE_DISTRIBUTION_R)
    public String toIndex(Model model) {
        LoginUser loginUser = this.getLoginUser();
        model.addAttribute("orgId",loginUser.getOrgId()).addAttribute("createSiteCode",loginUser.getSiteCode());
        return "/enterpriseDistribution/qualityInspection";
    }

    /**
     * 质检报表查询列表
     * @param condition
     * @return
     */
    @RequestMapping("/listData")
    @Authorization(Constants.ENTERPRISE_DISTRIBUTION_R)
    @ResponseBody
    public PagerResult<QualityInspectionDto> listData(@RequestBody QualityInspectionQueryCondition condition){
        LoginUser loginUser = this.getLoginUser();
        condition.setSiteCode(loginUser.getSiteCode());
        return enterpriseDistributionService.queryQualityInspectionPage(condition);
    }

    /**
     * 质检报表查询列表
     * @param condition
     * @return
     */
    @RequestMapping("/detailListData")
    @Authorization(Constants.ENTERPRISE_DISTRIBUTION_R)
    @ResponseBody
    public PagerResult<QualityInspectionDetailDto> detailListData(@RequestBody QualityInspectionQueryCondition condition){
        return enterpriseDistributionService.queryQualityInspectionDetailPage(condition);
    }

    /**
     * 企配质检导出
     * @param condition
     * @param response
     */
    @RequestMapping(value = "/toExport")
    @Authorization(Constants.ENTERPRISE_DISTRIBUTION_R)
    public void toExport(QualityInspectionQueryCondition condition, HttpServletResponse response) {
        BufferedWriter bfw = null;
        try{
            LoginUser loginUser = this.getLoginUser();
            condition.setSiteCode(loginUser.getSiteCode());
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION.getCode());
            String fileName = "增值服务质检报表";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
//            condition.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
//            condition.setQueryForWeb(Constants.YN_YES);
            enterpriseDistributionService.export(condition, bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.WEIGHT_AND_VOLUME_CHECK_REPORT.getCode());
        }catch (Exception e){
            log.error("exportData error", e);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                log.error("export-error", e);
            }
        }
    }
}
