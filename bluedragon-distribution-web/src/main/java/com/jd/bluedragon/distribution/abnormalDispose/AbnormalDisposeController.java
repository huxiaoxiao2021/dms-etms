package com.jd.bluedragon.distribution.abnormalDispose;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.bluedragon.distribution.abnormalDispose.service.AbnormalDisposeService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月13日 14时:25分
 */
@Controller
@RequestMapping("abnormalDispose/abnormalDispose")
public class AbnormalDisposeController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(AbnormalDisposeController.class);

    @Autowired
    AbnormalDisposeService abnormalDisposeService;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex(Model model) {
        model.addAttribute("usercode", getLoginUser().getUserErp());
        return "/abnormalDispose/abnormalDispose";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeMain> listData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeMain> pagerResult = abnormalDisposeService.queryMain(abnormalDisposeCondition, getLoginUser());
        return pagerResult;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/inspection/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeInspection> inspectionListData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeInspection> rest;
        if (abnormalDisposeCondition.getWaveBusinessId() == null || abnormalDisposeCondition.getSiteCode() == null) {
            rest = new PagerResult<AbnormalDisposeInspection>();
            rest.setTotal(0);
            rest.setRows(new ArrayList<AbnormalDisposeInspection>());
            return rest;
        }
        rest = abnormalDisposeService.queryInspection(abnormalDisposeCondition);
        return rest;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/send/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeSend> sendListData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeSend> rest;
        if (abnormalDisposeCondition.getWaveBusinessId() == null || abnormalDisposeCondition.getSiteCode() == null) {
            rest = new PagerResult<AbnormalDisposeSend>();
            rest.setTotal(0);
            rest.setRows(new ArrayList<AbnormalDisposeSend>());
            return rest;
        }
        rest = abnormalDisposeService.querySend(abnormalDisposeCondition);
        return rest;
    }

    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/send/toExport")
    public ModelAndView toExportSend(AbnormalDisposeCondition abnormalDisposeCondition, Model model) {
        try {
            model.addAttribute("filename", "未发货明细.xls");
            model.addAttribute("sheetname", "未发货明细");
            model.addAttribute("contents", abnormalDisposeService.getExportDataSend(abnormalDisposeCondition));

            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            log.error("abnormal/AbnormalDisposeCondition--toExport:", e);
            return null;
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/inspection/toExport")
    public ModelAndView toExportInspection(AbnormalDisposeCondition abnormalDisposeCondition, Model model) {
        try {
            model.addAttribute("filename", "未验货明细.xls");
            model.addAttribute("sheetname", "未验货明细");
            model.addAttribute("contents", abnormalDisposeService.getExportDataInspection(abnormalDisposeCondition));

            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            log.error("abnormal/AbnormalDisposeCondition--toExport:", e);
            return null;
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_ABNORMALDISPOSE_R)
    @RequestMapping(value = "/saveQcCode")
    public @ResponseBody
    JdResponse<String> saveQcCode(@RequestBody AbnormalDisposeInspection abnormalDisposeInspection) {
        if (abnormalDisposeInspection.getWaveBusinessId() == null || abnormalDisposeInspection.getWaybillCode() == null || abnormalDisposeInspection.getQcCode() == null) {
            log.warn("保存质控编码 --> 传入参数非法");
            return new JdResponse<String>(JdResponse.CODE_FAIL, JdResponse.MESSAGE_FAIL);
        }

        return abnormalDisposeService.saveAbnormalQc(abnormalDisposeInspection, getLoginUser());

    }

}
