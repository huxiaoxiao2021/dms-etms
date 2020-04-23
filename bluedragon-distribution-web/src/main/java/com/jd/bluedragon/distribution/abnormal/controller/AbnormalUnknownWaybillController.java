package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillCondition;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition, Model model) {
        try {
            if(StringUtils.isEmpty(abnormalUnknownWaybillCondition.getWaybillCode())
                    && (abnormalUnknownWaybillCondition.getStartTime() == null || abnormalUnknownWaybillCondition.getEndTime() == null)){
                model.addAttribute("exception",new IllegalArgumentException("运单号和上报时间条件不能同时为空！") );
                return new ModelAndView("uncaught");
            }
            if(StringUtils.isEmpty(abnormalUnknownWaybillCondition.getWaybillCode())
                    && DateHelper.daysBetween(abnormalUnknownWaybillCondition.getStartTime(),abnormalUnknownWaybillCondition.getEndTime()) >
                    queryLimitDay){
                model.addAttribute("exception",new IllegalArgumentException("上报时间相差不能超过"+ queryLimitDay +"天！") );
                return new ModelAndView("uncaught");
            }
            if (abnormalUnknownWaybillCondition.getWaybillCode() != null && abnormalUnknownWaybillCondition.getWaybillCode().contains(AbnormalUnknownWaybill.SEPARATOR_APPEND)) {
                String[] waybillcodes = abnormalUnknownWaybillCondition.getWaybillCode().split(AbnormalUnknownWaybill.SEPARATOR_APPEND);
                abnormalUnknownWaybillCondition.setWaybillCodes(Arrays.asList(waybillcodes));
                abnormalUnknownWaybillCondition.setWaybillCode(null);
            }
            List<List<Object>> resultList = abnormalUnknownWaybillService.getExportData(abnormalUnknownWaybillCondition);
            model.addAttribute("filename", "三无托寄物核实结果.xls");
            model.addAttribute("sheetname", "三无托寄物核实结果");
            model.addAttribute("contents", resultList);

            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            log.error("abnormal/abnormalUnknownWaybill--toExport:", e);
            return null;
        }
    }
}
