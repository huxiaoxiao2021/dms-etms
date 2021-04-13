package com.jd.bluedragon.distribution.barcode.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;
import com.jd.bluedragon.distribution.barcode.service.BarcodeService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 69码查询
 * @date 2018年07月10日 14时:28分
 */
@Controller
@RequestMapping("barcode")
public class DmsBarCodeController extends DmsBaseController {
    private static final Logger log = LoggerFactory.getLogger(DmsBarCodeController.class);

    @Autowired
    BarcodeService barcodeService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_DMSBARCODE_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/barcode/barcode";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param barCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_DMSBARCODE_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody
    List<DmsBarCode> listData(@RequestBody DmsBarCode barCode) {
        return barcodeService.query(barCode);
    }

    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_SORTING_DMSBARCODE_R)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.barcode.controller.DmsBarCodeController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_SORTING_DMSBARCODE_R)){
                result.customMessage(InvokeResult.RESULT_EXPORT_LIMIT_CODE,InvokeResult.RESULT_EXPORT_LIMIT_MESSAGE);
                return result;
            }
        }catch (Exception e){
            log.error("校验导出并发接口异常-暂存记录统计表",e);
            result.customMessage(InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_CODE,InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_MESSAGE);
            return result;
        }
        return result;
    }

    /**
     * 导出数据
     * @param barCode
     * @param response
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_DMSBARCODE_R)
    @RequestMapping(value = "/toExport")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.barcode.controller.DmsBarCodeController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @ResponseBody
    public InvokeResult toExport(DmsBarCode barCode, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        try {
            if(!checkParam(barCode,result)){
                return result;
            }

            String fileName = "69码商品查询";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            barcodeService.export(barCode,bfw);
        } catch (Exception e) {
            log.error("69码商品查询--toExport:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
            return result;
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                log.error("69码商品查询export-error", e);
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
            }
        }
        return result;
    }

    /**
     * 校验前端参数
     * @param barCode
     * @param result
     * @return
     */
    private boolean checkParam(DmsBarCode barCode,InvokeResult result) {
        if (barCode==null || barCode.getBarcode()==null) {
            result.customMessage(InvokeResult.RESULT_THIRD_ERROR_CODE,InvokeResult.PARAM_ERROR);
            return false;
        }

        return true;
    }
}
