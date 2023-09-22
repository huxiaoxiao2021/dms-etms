package com.jd.bluedragon.distribution.merchantWeightAndVolume.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
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

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        // 设置基础信息
        setBaseModelInfo(model);
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
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.MERCHANT_WEIGHT_AND_VOLUME_WHITE_REPORT.getCode());
            String fileName = "托寄物品名";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);

            merchantWeightAndVolumeWhiteListService.getExportData(condition,bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.MERCHANT_WEIGHT_AND_VOLUME_WHITE_REPORT.getCode());
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
