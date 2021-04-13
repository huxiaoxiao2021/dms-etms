package com.jd.bluedragon.distribution.handoverPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.signAndReturn.domain.MergedWaybill;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signAndReturn.service.MergedWaybillService;
import com.jd.bluedragon.distribution.signAndReturn.service.SignReturnService;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.util.Collections;

/**
 * @ClassName: SignReturnController
 * @Description: 签单返回合单交接单打印
 * @author: hujiping
 * @date: 2018/11/23 13:54
 */
@Controller
@RequestMapping("signReturn")
public class SignReturnController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(SignReturnController.class);

    @Autowired
    private SignReturnService signReturnService;

    @Autowired
    private MergedWaybillService mergedWaybillService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex(){
        return "/signReturn/signReturnPrint";
    }

    /**
     * 查询
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @RequestMapping(value = "/query")
    public @ResponseBody PagerResult<SignReturnPrintM> query(@RequestBody SignReturnCondition condition){

        PagerResult<SignReturnPrintM> result = new PagerResult<SignReturnPrintM>();
        result.setRows(Collections.<SignReturnPrintM>emptyList());
        result.setTotal(0);
        if(StringHelper.isNotEmpty(condition.getNewWaybillCode())){
            if(WaybillUtil.isPackageCode(condition.getNewWaybillCode())){
                String packageCode = condition.getNewWaybillCode();
                condition.setNewWaybillCode(WaybillUtil.getWaybillCode(packageCode));
            }
            result = signReturnService.getListByWaybillCode(condition);
            if(result.getRows().size() == 0 && StringHelper.isNotEmpty(condition.getWaybillCode())){
                if(WaybillUtil.isPackageCode(condition.getWaybillCode())){
                    String packageCode = condition.getWaybillCode();
                    condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
                }
                result = mergedWaybillService.getSignReturnByConditon(condition);
            }
        }else if(StringHelper.isEmpty(condition.getNewWaybillCode()) && StringHelper.isNotEmpty(condition.getWaybillCode())) {
            if(WaybillUtil.isPackageCode(condition.getWaybillCode())){
                String packageCode = condition.getWaybillCode();
                condition.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
            }
            result = mergedWaybillService.getSignReturnByConditon(condition);

        }
         return result;
    }

    /**
     * 查询明细
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<MergedWaybill> listData(@RequestBody SignReturnCondition condition){

        PagerResult<MergedWaybill> result = new PagerResult<MergedWaybill>();
        PagerResult<SignReturnPrintM> signReturnResult = query(condition);
        if(signReturnResult.getRows() != null && signReturnResult.getRows().size() > 0){
            SignReturnPrintM signReturnPrintM = signReturnResult.getRows().get(0);
            result.setRows(signReturnPrintM.getMergedWaybillList());
            result.setTotal(signReturnPrintM.getMergedWaybillList().size());
        }else {
            result.setRows(Collections.<MergedWaybill>emptyList());
            result.setTotal(0);
        }
        return result;
    }

    @RequestMapping(value = "/checkConcurrencyLimit")
    @ResponseBody
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.handoverPrint.SignReturnController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult checkConcurrencyLimit(){
        InvokeResult result = new InvokeResult();
        try {
            //校验并发
            if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_TOOL_SIGNRETURN_R)){
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
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    @JProfiler(jKey = "com.jd.bluedragon.distribution.handoverPrint.SignReturnController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void toExport(SignReturnCondition condition, HttpServletResponse response, Model model){
        log.debug("导出签单返回合单打印交接单");
        try{
            PagerResult<SignReturnPrintM> result = query(condition);
            signReturnService.toExport(result,response);
        }catch (Exception e){
            log.error("导出失败!",e);
        }
    }

    /**
     * 跳转打印页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @RequestMapping(value = "/toPrint")
    public String toPrint(SignReturnCondition condition,Model model){

        model.addAttribute("newWaybillCode",condition.getNewWaybillCode());
        model.addAttribute("waybillCode",condition.getWaybillCode());
        return "/signReturn/signReturnPrintInfo";
    }

    /**
     * 打印明细
     * */
    @Authorization(Constants.DMS_WEB_TOOL_SIGNRETURN_R)
    @ResponseBody
    @RequestMapping(value = "/printInfo")
    public String printInfo(@QueryParam("newWaybillCode")String newWaybillCode,
                            @QueryParam("waybillCode")String waybillCode){

        SignReturnCondition condition = new SignReturnCondition();
        condition.setNewWaybillCode(newWaybillCode);
        condition.setWaybillCode(waybillCode);
        PagerResult<SignReturnPrintM> result = query(condition);
        SignReturnPrintM signReturn = result.getRows().get(0);
        String json = JsonHelper.toJson(signReturn);
        return json;
    }

}
