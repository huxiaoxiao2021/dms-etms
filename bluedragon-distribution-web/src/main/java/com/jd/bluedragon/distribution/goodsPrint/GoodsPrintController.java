package com.jd.bluedragon.distribution.goodsPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 托寄物品名打印
 * @date 2018年11月16日 13时:58分
 */
@Controller
@RequestMapping("goodsPrint")
public class GoodsPrintController extends DmsBaseController {
    private static final Logger log = LoggerFactory.getLogger(GoodsPrintController.class);

    @Autowired
    GoodsPrintService goodsPrintService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/goodsPrint/goodsPrintList";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param goodsPrint
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody JdResponse<List<GoodsPrintDto>> listData(@RequestBody GoodsPrintDto goodsPrint) {
        try {
            return goodsPrintService.query(goodsPrint);
        }catch (Exception e){
            JdResponse jdResponse=new JdResponse();
            jdResponse.setCode(500);
            jdResponse.setMessage("调用服务失败");
            log.error("goodsPrint.listData调用服务失败",e);
            return jdResponse;
        }
    }

    /**
     * 校验并发接口
     * @return
     */
    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.goodsPrint.GoodsPrintController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_SORTING_GOODSPRINT_R)){
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


    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/toExport")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.goodsPrint.GoodsPrintController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @ResponseBody
    public InvokeResult toExport(GoodsPrintDto goodsPrint, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        try {
            String fileName = "托寄物品名";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);

            goodsPrintService.export(goodsPrint,bfw);
        } catch (Exception e) {
            log.error("托寄物品名--toExport:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
            return result;
        }finally {
                try {
                    if (bfw != null) {
                        bfw.flush();
                        bfw.close();
                    }
                } catch (IOException e) {
                    log.error("托寄物品名export-error", e);
                    result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
                }
        }
        return result;
    }
}
