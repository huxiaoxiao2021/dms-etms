package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillCondition;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillController
 * @Description: 三无订单申请--Controller实现
 * @date 2018年05月08日 15:16:15
 */
@Controller
@RequestMapping("abnormal/abnormalUnknownWaybill")
public class AbnormalUnknownWaybillController extends DmsBaseController{

    private static final Logger log = LoggerFactory.getLogger(AbnormalUnknownWaybillController.class);

    @Value("${abnormalUnknown.queryLimitDay:20}")
    private int queryLimitDay;

    @Autowired
    private AbnormalUnknownWaybillService abnormalUnknownWaybillService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/abnormal/abnormalUnknownWaybill";
    }

    /**
     * 根据id获取基本信息
     *
     * @param id
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody
    JdResponse<AbnormalUnknownWaybill> detail(@PathVariable("id") Long id) {
        JdResponse<AbnormalUnknownWaybill> rest = new JdResponse<AbnormalUnknownWaybill>();
        rest.setData(abnormalUnknownWaybillService.findById(id));
        return rest;
    }

    /**
     * 二次上报
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/submitAgain/{waybillCode}")
    public @ResponseBody
    JdResponse<String> submitAgain(@PathVariable("waybillCode") String waybillCode) {
        return abnormalUnknownWaybillService.submitAgain(waybillCode);
    }

    /**
     * 保存数据
     *
     * @param abnormalUnknownWaybill
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/save")
    public @ResponseBody
    JdResponse<String> save(@RequestBody AbnormalUnknownWaybill abnormalUnknownWaybill) {
        JdResponse<String> rest = new JdResponse<String>();
        try {
            LoginUser loginUser=getLoginUser();
            if (loginUser==null){
                rest.toError("保存失败，用户未登录。");
            }
            return abnormalUnknownWaybillService.queryAndReport(abnormalUnknownWaybill,loginUser);
        } catch (Exception e) {
            log.error("fail to save！", e);
            rest.toError("保存失败，服务异常！");
        }
        return rest;
    }

    /**
     * 根据id删除多条数据
     *
     * @param ids
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody
    JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
        JdResponse<Integer> rest = new JdResponse<Integer>();
        try {
            rest.setData(abnormalUnknownWaybillService.deleteByIds(ids));
        } catch (Exception e) {
            log.error("fail to delete！", e);
            rest.toError("删除失败，服务异常！");
        }
        return rest;
    }

    /**
     * 校验输入的运单号
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/checkWaybillCode")
    public @ResponseBody
    JdResponse checkWaybillCode(@QueryParam("waybillCodes") String waybillCodes) {
        JdResponse response = new JdResponse();
        response.setCode(JdResponse.CODE_SUCCESS);
        if(StringHelper.isEmpty(waybillCodes.trim())){
            return response;
        }else {
            List<String> waybillCodeList = new ArrayList<String>();
            if(waybillCodes.contains(AbnormalUnknownWaybill.SEPARATOR_APPEND)){
                String[] split = waybillCodes.split(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                waybillCodeList = Arrays.asList(split);
            }else {
                waybillCodeList.add(waybillCodes);
            }
            return abnormalUnknownWaybillService.queryByWaybillCode(waybillCodeList);
        }
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalUnknownWaybillCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/listData")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.AbnormalUnknownWaybillController.listData", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public @ResponseBody
    JdResponse<PagerResult<AbnormalUnknownWaybill>> listData(@RequestBody AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition) {
        JdResponse<PagerResult<AbnormalUnknownWaybill>> rest = new JdResponse<>();
        if(StringUtils.isEmpty(abnormalUnknownWaybillCondition.getWaybillCode())
                && (abnormalUnknownWaybillCondition.getStartTime() == null || abnormalUnknownWaybillCondition.getEndTime() == null)){
            rest.toFail("运单号和上报时间条件不能同时为空！");
            return rest;
        }
        if(StringUtils.isEmpty(abnormalUnknownWaybillCondition.getWaybillCode())
                && DateHelper.daysBetween(abnormalUnknownWaybillCondition.getStartTime(),abnormalUnknownWaybillCondition.getEndTime()) >
                queryLimitDay){
            rest.toFail("上报时间相差不能超过"+ queryLimitDay +"天！");
            return rest;
        }
        if (abnormalUnknownWaybillCondition.getWaybillCode() != null && abnormalUnknownWaybillCondition.getWaybillCode().contains(AbnormalUnknownWaybill.SEPARATOR_APPEND)) {
            String[] waybillcodes = abnormalUnknownWaybillCondition.getWaybillCode().split(AbnormalUnknownWaybill.SEPARATOR_APPEND);
            abnormalUnknownWaybillCondition.setWaybillCodes(Arrays.asList(waybillcodes));
            abnormalUnknownWaybillCondition.setWaybillCode(null);
        }
        rest.toSucceed();
        rest.setData(abnormalUnknownWaybillService.queryByPagerCondition(abnormalUnknownWaybillCondition));
        return rest;
    }

    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.abnormal.controller.AbnormalUnknownWaybillController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)){
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


    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/toExport")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.AbnormalUnknownWaybillController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @ResponseBody
    public  InvokeResult toExport(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        try {
            if(StringUtils.isEmpty(abnormalUnknownWaybillCondition.getWaybillCode())
                    && DateHelper.daysBetween(abnormalUnknownWaybillCondition.getStartTime(),abnormalUnknownWaybillCondition.getEndTime()) >
                    queryLimitDay){
                result.customMessage(InvokeResult.RESULT_THIRD_ERROR_CODE,"上报时间相差不能超过"+ queryLimitDay +"天！");
                return result;
            }

            if (abnormalUnknownWaybillCondition.getWaybillCode() != null && abnormalUnknownWaybillCondition.getWaybillCode().contains(AbnormalUnknownWaybill.SEPARATOR_APPEND)) {
                String[] waybillcodes = abnormalUnknownWaybillCondition.getWaybillCode().split(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                abnormalUnknownWaybillCondition.setWaybillCodes(Arrays.asList(waybillcodes));
                abnormalUnknownWaybillCondition.setWaybillCode(null);
            }

            String fileName = "三无托寄物核实结果";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);
            abnormalUnknownWaybillService.export(abnormalUnknownWaybillCondition,bfw);
        } catch (Exception e) {
            log.error("abnormal/abnormalUnknownWaybill--toExport:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
        }finally {
            try {
                if (bfw != null) {
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                log.error("export-error", e);
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
            }
        }
        return result;
    }
}
