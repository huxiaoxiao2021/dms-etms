package com.jd.bluedragon.distribution.inventory.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/inventoryException")
public class inventoryExceptionController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(inventoryExceptionController.class);

    @Value("${waybill.trace.url:waybill.test.jd.com}")
    private String waybillTraceUrl;

    @Autowired
    private InventoryExceptionService inventoryExceptionService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = -1;
        Integer orgId = -1;
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
            orgId = loginUser.getOrgId();
        }
        model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode).addAttribute("url",this.waybillTraceUrl);
        return "/inventory/inventoryException";
    }

    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<InventoryException> listData(@RequestBody InventoryExceptionCondition condition){

        PagerResult<InventoryException> result = new PagerResult<>();
        try {
            result = inventoryExceptionService.queryByPagerCondition(condition);
        } catch (Exception e) {
            log.error("获取清场异常记录分页数据异常", e);
        }

        return result;
    }

    /**
     * 导出
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    @ResponseBody
    @JProfiler(jKey = "com.jd.bluedragon.distribution.inventory.controller.inventoryExceptionController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult toExport(InventoryExceptionCondition condition, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        log.info("导出转运清场异常结果");
        try{
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.INVENTORY_EXCEPTION_REPORT.getCode());
            String fileName = "转运清场异常统计表";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            inventoryExceptionService.getExportData(condition,bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.INVENTORY_EXCEPTION_REPORT.getCode());
        }catch (Exception e){
            log.error("导出转运清场异常统计表:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException es) {
                log.error("导出转运清场异常统计表 流关闭异常", es);
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
            }
        }
        return result;
    }


    /**
     * 根据id确认多条数据
     * @param records
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYEXCEPTION_R)
    @RequestMapping(value = "/handle", method = RequestMethod.POST)
    public @ResponseBody JdResponse<Integer> handle(@RequestBody List<Long> records) {
        JdResponse<Integer> result = new JdResponse<Integer>();
        try {
            LoginUser loginUser = getLoginUser();
            result.setData(inventoryExceptionService.handleException(records, loginUser));
        } catch (Exception e) {
            log.error("处理转运清场异常失败：",e);
            result.toError("确认失败，服务异常！");
        }
        return result;
    }

}
