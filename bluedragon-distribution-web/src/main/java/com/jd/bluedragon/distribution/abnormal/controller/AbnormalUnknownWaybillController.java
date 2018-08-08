package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillCondition;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
public class AbnormalUnknownWaybillController {

    private static final Log logger = LogFactory.getLog(AbnormalUnknownWaybillController.class);

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
            return abnormalUnknownWaybillService.queryAndReport(abnormalUnknownWaybill);
        } catch (Exception e) {
            logger.error("fail to save！" + e.getMessage(), e);
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
            logger.error("fail to delete！" + e.getMessage(), e);
            rest.toError("删除失败，服务异常！");
        }
        return rest;
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
    PagerResult<AbnormalUnknownWaybill> listData(@RequestBody AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition) {
        JdResponse<PagerResult<AbnormalUnknownWaybill>> rest = new JdResponse<PagerResult<AbnormalUnknownWaybill>>();
        if (abnormalUnknownWaybillCondition.getWaybillCode() != null && abnormalUnknownWaybillCondition.getWaybillCode().contains(AbnormalUnknownWaybill.SEPARATOR_APPEND)) {
            String[] waybillcodes = abnormalUnknownWaybillCondition.getWaybillCode().split(AbnormalUnknownWaybill.SEPARATOR_APPEND);
            abnormalUnknownWaybillCondition.setWaybillCodes(Arrays.asList(waybillcodes));
            abnormalUnknownWaybillCondition.setWaybillCode(null);
        }
        rest.setData(abnormalUnknownWaybillService.queryByPagerCondition(abnormalUnknownWaybillCondition));
        return rest.getData();
    }

    @Authorization(Constants.DMS_WEB_SORTING_UNKNOWNWAYBILL_R)
    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition, Model model) {
        try {
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
            logger.error("abnormal/abnormalUnknownWaybill--toExport:" + e.getMessage(), e);
            return null;
        }
    }
}
