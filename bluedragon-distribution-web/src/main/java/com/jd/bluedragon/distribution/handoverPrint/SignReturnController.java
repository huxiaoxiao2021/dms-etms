package com.jd.bluedragon.distribution.handoverPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log logger = LogFactory.getLog(SignReturnController.class);

    @Autowired
    private SignReturnService signReturnService;

    @Autowired
    private MergedWaybillService mergedWaybillService;


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

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public void toExport(SignReturnCondition condition, HttpServletResponse response, Model model){

        logger.info("导出签单返回合单打印交接单");
        try{
            PagerResult<SignReturnPrintM> result = query(condition);
            signReturnService.toExport(result,response);
        }catch (Exception e){
            logger.error("导出失败!");
        }
    }

    /**
     * 跳转打印页面
     * @return
     */
    @RequestMapping(value = "/toPrint")
    public String toPrint(SignReturnCondition condition,Model model){

        model.addAttribute("newWaybillCode",condition.getNewWaybillCode());
        model.addAttribute("waybillCode",condition.getWaybillCode());
        return "/signReturn/signReturnPrintInfo";
    }

    /**
     * 打印明细
     * */
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
