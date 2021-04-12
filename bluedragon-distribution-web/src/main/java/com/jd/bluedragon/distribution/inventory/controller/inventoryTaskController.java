package com.jd.bluedragon.distribution.inventory.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTaskCondition;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/inventoryTask")
public class inventoryTaskController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(inventoryTaskController.class);
    
    @Autowired
    private InventoryTaskService inventoryTaskService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;


    /**
     * 返回主页面
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        return "/inventory/inventoryTask";
    }

    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<InventoryTask> listData(@RequestBody InventoryTaskCondition condition){

        PagerResult<InventoryTask> result = new PagerResult<>();
        try {
            result = inventoryTaskService.queryByPagerCondition(condition);
        } catch (Exception e) {
            log.error("获取清场任务分页数据异常", e);
        }
        return result;
    }

    /**
     * 导出
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.inventory.controller.inventoryTaskController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public InvokeResult toExport(InventoryTaskCondition condition, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        log.debug("转运清场任务信息结果");
        try{
            String fileName = "转运清场任务信息表";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            inventoryTaskService.getExportData(condition,bfw);
        }catch (Exception e){
            log.error("导出转运清场任务信息表失败:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException es) {
                log.error("导出转运清场任务信息表 流关闭异常", es);
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
            }
        }
        return result;
    }


    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.inventory.controller.inventoryTaskController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)){
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
}
