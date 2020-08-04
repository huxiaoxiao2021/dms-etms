package com.jd.bluedragon.distribution.financialForKA.controller;

import com.jcloud.jss.domain.StorageObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLogCondition;
import com.jd.bluedragon.distribution.exportlog.service.ExportLogService;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.bluedragon.distribution.financialForKA.service.WaybillCodeCheckService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.distribution.web.view.ExcelWriter;
import com.jd.bluedragon.distribution.web.view.MutiSheetExcelView;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: WaybillCodeCheckController
 * @Description: 金融客户运单号对比校验-Controller
 * @author: hujiping
 * @date: 2019/3/7 15:16
 */
@Controller
@RequestMapping("waybillCodeCheckForKA")
public class WaybillCodeCheckController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(WaybillCodeCheckController.class);
    public static List<Object> heads = new ArrayList<Object>();

    static {
        //添加表头
        heads.add("运单号");
        heads.add("比较单号");
        heads.add("商家编码");
        heads.add("商家名称");
        heads.add("操作站点");
        heads.add("操作站点名称");
        heads.add("校验结果");
        heads.add("操作人ERP");
        heads.add("操作时间");
    }

    @Autowired
    private WaybillCodeCheckService waybillCodeCheckService;

    @Autowired
    private ExportLogService exportLogService;

    @Autowired
    private JssService jssService;
    @Value("${jss.waybillcheck.export.zip.bucket}")
    private String bucket;
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Value("${waybillcheck.export.maxNum}")
    private Integer exportMaxNum;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model) {
        LoginUser loginUser = getLoginUser();
        model.addAttribute("operateSiteCode", loginUser.getSiteCode());
        model.addAttribute("operateSiteName", loginUser.getSiteName());
        model.addAttribute("operateErp", loginUser.getUserErp());
        return "/financialForKA/waybillCodeCheck";
    }

    /**
     * 条码校验
     *
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/check")
    @ResponseBody
    public InvokeResult waybillCodeCheck(@RequestBody WaybillCodeCheckCondition condition) {
        return waybillCodeCheckService.waybillCodeCheck(condition);
    }

    /**
     * 跳转查询页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/toSearchIndex")
    public String toSearchIndex() {
        return "/financialForKA/financialForKAsearch";
    }

    /**
     * 跳转导出明细查看页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/toSearchExportTaskIndex")
    public String toSearchExportTaskIndex() {
        return "/financialForKA/searchExportTask";
    }


    /**
     * 获取明细
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<WaybillCodeCheckDto> listData(@RequestBody KaCodeCheckCondition condition) {
        return waybillCodeCheckService.listData(condition);
    }

    /**
     * 获取导出任务列表
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/exportLogList")
    @ResponseBody
    public PagerResult<ExportLog> listData(@RequestBody ExportLogCondition condition) {
        return exportLogService.listData(condition);
    }

    /**
     * 导出校验
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/exortCheck")
    public @ResponseBody
    JdResponse<String> exortCheck(@RequestBody KaCodeCheckCondition condition) {
        LoginUser loginUser = getLoginUser();
        String exportCode = loginUser.getUserErp() + DateHelper.formatDate(new Date(), "yyyyMMddHHmm");
        JdResponse<String> rest = new JdResponse<String>();
        String repeatExport = jimdbCacheService.get(exportCode);
        if(!com.jd.bk.common.util.string.StringUtils.isBlank(repeatExport)) {
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("一分钟内只能导出一次，请稍后再试");
            return rest;
        }
        Integer count = waybillCodeCheckService.queryCountByCondition(condition);
        if(count > exportMaxNum) {
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("导出KA条码对比校验操作记录统计表失败,最多只能导出" + exportMaxNum + "条");
            return rest;
        }
        rest.setCode(JdResponse.CODE_SUCCESS);
        if(count > 100000) {
            rest.setMessage("正在导出数据中，您导出的数量比较大，可能需要1-3分钟，请耐心等待");
        }
        return rest;
    }

    /**
     * 导出
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult toExportNew(KaCodeCheckCondition condition, Model model) {
        LoginUser loginUser = getLoginUser();
        InvokeResult invokeResult = new InvokeResult();
        String result = waybillCodeCheckService.exportApply(loginUser, condition);
        if(StringUtils.isBlank(result)) {
            invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        } else {
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage(result);
        }
        return invokeResult;
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param response
     * @param request
     */
    @RequestMapping(value = "/downLoadFile")
    public void downLoad(@RequestParam("fileName") String fileName, HttpServletResponse response, HttpServletRequest request) {
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try {
            InputStream inputStream = jssService.downloadFile(bucket, fileName);
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.info("下载运单号校验导出记录失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult deleteFile(Long id) {
        InvokeResult invokeResult = new InvokeResult();
        try {
            exportLogService.delete(id);
            invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        } catch (Exception ex) {
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage("删除失败");
            log.error("删除文件失败", ex);
        }
        return invokeResult;
    }

    /**
     * 导出
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping(value = "/toExportOld", method = RequestMethod.POST)
    @Deprecated
    public ModelAndView toExportOld(KaCodeCheckCondition condition, Model model) {
        LoginUser loginUser = getLoginUser();
        log.info("KA条码对比校验操作记录统计表");
        List<List<Object>> resultList;
        try {
            model.addAttribute("filename", "KA条码对比校验操作记录统计表.xls");
            model.addAttribute("sheetname", "KA条码对比校验操作记录");
            resultList = waybillCodeCheckService.getExportData(condition);
        } catch (Exception e) {
            log.error("导出KA条码对比校验操作记录统计表失败:", e);
            List<Object> list = new ArrayList<>();
            list.add("导出KA条码对比校验操作记录统计表失败!");
            resultList = new ArrayList<>();
            resultList.add(list);
        }
        model.addAttribute("contents", resultList);
        return new ModelAndView(new MutiSheetExcelView(heads), model.asMap());
    }

}
