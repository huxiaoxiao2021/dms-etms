package com.jd.bluedragon.distribution.merchantWeightAndVolume.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商家称重量方白名单
 *
 * @author: hujiping
 * @date: 2019/11/5 11:01
 */
@Controller
@RequestMapping("/merchantWeightAndVolume/whiteList")
public class MerchantWeightAndVolumeWhiteListController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(MerchantWeightAndVolumeWhiteListController.class);

    /**
     * 文件后缀名
     * */
    private static final String SUFFIX_NAME = "xls";

    @Autowired
    private MerchantWeightAndVolumeWhiteListService merchantWeightAndVolumeWhiteListService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = new Integer(-1);
        Integer orgId = new Integer(-1);
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
            orgId = loginUser.getOrgId();
        }
        model.addAttribute("orgId",orgId);
        model.addAttribute("createSiteCode",createSiteCode);
        return "merchantWeightAndVolume/merchantWeightAndVolumeWhiteList";
    }

    /**
     * 查询
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<MerchantWeightAndVolumeDetail> listData(@RequestBody MerchantWeightAndVolumeCondition condition){
        return merchantWeightAndVolumeWhiteListService.queryByCondition(condition);
    }

    /**
     * 删除
     * @param detail
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> delete(@RequestBody MerchantWeightAndVolumeDetail detail){
        return merchantWeightAndVolumeWhiteListService.delete(detail);
    }

    /**
     * 导入
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping(value = "/toImport", method = RequestMethod.POST)
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.merchantWeightAndVolume.controller.MerchantWeightAndVolumeWhiteListController.toImport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public JdResponse toImport(@RequestParam("importExcelFile") MultipartFile file) {
        log.debug("uploadExcelFile begin...");
        JdResponse response = new JdResponse();
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith(SUFFIX_NAME)) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            String importErpCode = erpUser.getUserCode();

            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(1);
            List<MerchantWeightAndVolumeDetail> dataList
                    = dataResolver.resolver(file.getInputStream(), MerchantWeightAndVolumeDetail.class, new PropertiesMetaDataFactory("/excel/merchantWeightAndVolume.properties"));
            String errorMessage = merchantWeightAndVolumeWhiteListService.checkExportData(dataList,importErpCode);
            if (!StringUtils.isEmpty(errorMessage)) {
                return new JdResponse(JdResponse.CODE_FAIL, errorMessage);
            }
        } catch (Exception e) {
            this.log.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
        return response;
    }

    /**
     * 校验并发接口
     * @return
     */
    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_SORTING_REVERSEPARTDETAIL_CHECK_R)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.reverse.part.controller.ReversePartDetailController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_SORTING_REVERSEPARTDETAIL_CHECK_R)){
                result.customMessage(InvokeResult.RESULT_EXPORT_LIMIT_CODE,InvokeResult.RESULT_EXPORT_LIMIT_MESSAGE);
                return result;
            }
        }catch (Exception e){
            log.error("校验导出并发接口异常",e);
            result.customMessage(InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_CODE,InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_MESSAGE);
            return result;
        }
        return result;
    }


    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.merchantWeightAndVolume.controller.MerchantWeightAndVolumeWhiteListController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @ResponseBody
    public InvokeResult toExport(MerchantWeightAndVolumeCondition condition, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        try {
            String fileName = "托寄物品名";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);

            merchantWeightAndVolumeWhiteListService.getExportData(condition,bfw);
        }catch (Exception e){
            log.error("商家称重量方白名单统计表--toExport error:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
            return result;
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                log.error("商家称重量方白名单统计表--export-error", e);
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
            }
        }
        return  result;
    }

}
