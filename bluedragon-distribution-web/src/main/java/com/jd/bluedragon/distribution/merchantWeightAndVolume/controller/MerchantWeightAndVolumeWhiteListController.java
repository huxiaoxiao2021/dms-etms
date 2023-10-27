package com.jd.bluedragon.distribution.merchantWeightAndVolume.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.DataFormatException;


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
    private WaybillPackageManager waybillPackageManager;

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

    @RequestMapping(value = "/toImportVolume", method = RequestMethod.POST)
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.merchantWeightAndVolume.controller.MerchantWeightAndVolumeWhiteListController.toImportVolume", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public JdResponse toImportVolume(@RequestParam("importExcelFile") MultipartFile file) {
        log.debug("uploadExcelFile begin...");
        JdResponse response = new JdResponse();
        try {
            if (file.getSize() > 13325440) throw new IOException("文件大小超过限制(1M)");
//            Sheet sheet0 = getSheet0FromFile(file);
//            if (null == sheet0) throw new DataFormatException("文件只能是Excel");
            List<PackOpeDto> packOpeDtoList = importCrossSortingRules(file);
            for (PackOpeDto item : packOpeDtoList){
                Map<String, Object> resultMap = waybillPackageManager.uploadOpe(JsonHelper.toJson(item));
                if (resultMap != null && resultMap.containsKey("code")
                        && WeightResponse.WEIGHT_TRACK_OK == Integer.parseInt(resultMap.get("code").toString())) {
                    log.info("向运单系统回传包裹称重信息成功：{}", JsonHelper.toJson(item));
                } else {
                    log.warn("向运单系统回传包裹称重信息失败：{}，运单返回值：{}",  JsonHelper.toJson(resultMap));
                }
            }
            response.toSucceed();

        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
        return response;
    }

    private Sheet getSheet0FromFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName.toLowerCase().endsWith("xlsx")) {
            return new XSSFWorkbook(file.getInputStream()).getSheetAt(0);
        } else if (fileName.toLowerCase().endsWith("xls")) {
            return new HSSFWorkbook(file.getInputStream()).getSheetAt(0);
        } else {
            throw new DataFormatException("文件只能是Excel");
        }
    }

    private List<PackOpeDto> importCrossSortingRules(MultipartFile file) throws Exception {
        CSVParser csvParser = CSVParser.parse(file.getInputStream(), Charset.forName("UTF-8"), CSVFormat.DEFAULT);
        List<PackOpeDto> needInsertRules = new ArrayList<PackOpeDto>();
        int rows = 0;
        for (CSVRecord csvRecord : csvParser) {
            rows++;
            if(rows == 1){
                continue;
            }
            PackOpeDto packOpeDto = new PackOpeDto();
            packOpeDto.setWaybillCode(csvRecord.get(0));
            packOpeDto.setOpeType(1);//分拣操作环节赋值：1
            // 处理每行数据
            PackOpeDetail packOpeDetail = new PackOpeDetail();
            packOpeDetail.setOpeSiteId(2505);
            packOpeDetail.setOpeSiteName("南京散货分拣中心");
            packOpeDetail.setPackageCode(csvRecord.get(1));
            packOpeDetail.setpWeight(Double.parseDouble(csvRecord.get(2)));
            packOpeDetail.setpLength(Double.parseDouble(csvRecord.get(3)));
            packOpeDetail.setpWidth(Double.parseDouble(csvRecord.get(4)));
            packOpeDetail.setpHigh(Double.parseDouble(csvRecord.get(5)));
            packOpeDetail.setOpeTime(DateHelper.formatDateTime(DateHelper.toDate(Long.valueOf(csvRecord.get(6)))));
            packOpeDetail.setOpeUserId(22491993);
            packOpeDetail.setOpeUserName("武美媛");
            packOpeDetail.setLongPackage(0);

            packOpeDto.setOpeDetails(Collections.singletonList(packOpeDetail));
            Thread.sleep(50);
            log.info("导入数据rows[{}]packOpeDto[{}]", rows, JsonHelper.toJson(packOpeDto));
            needInsertRules.add(packOpeDto);


        }
        return needInsertRules;
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
