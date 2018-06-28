package com.jd.bluedragon.distribution.abnormalDispose;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalQc;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.bluedragon.distribution.abnormalDispose.service.AbnormalDisposeService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月13日 14时:25分
 */
@Controller
@RequestMapping("abnormalDispose/abnormalDispose")
public class AbnormalDisposeController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(AbnormalDisposeController.class);

    @Autowired
    AbnormalDisposeService abnormalDisposeService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 返回主页面
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/abnormalDispose/abnormalDispose";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeMain> listData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeMain> pagerResult = abnormalDisposeService.queryMain(abnormalDisposeCondition);
        return pagerResult;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @RequestMapping(value = "/inspection/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeInspection> inspectionListData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeInspection> rest;
        if (abnormalDisposeCondition.getWaveBusinessId() == null) {
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
    @RequestMapping(value = "/send/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeSend> sendListData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        PagerResult<AbnormalDisposeSend> rest;
        if (abnormalDisposeCondition.getWaveBusinessId() == null) {
            rest = new PagerResult<AbnormalDisposeSend>();
            rest.setTotal(0);
            rest.setRows(new ArrayList<AbnormalDisposeSend>());
            return rest;
        }
        rest = abnormalDisposeService.querySend(abnormalDisposeCondition);
        return rest;
    }

    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(AbnormalDisposeCondition abnormalDisposeCondition, Model model) {
        try {
            model.addAttribute("filename", "三无托寄物核实结果.xls");
            model.addAttribute("sheetname", "三无托寄物核实结果");
//            model.addAttribute("contents", resultList);

            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            logger.error("abnormal/AbnormalDisposeCondition--toExport:" + e.getMessage(), e);
            return null;
        }
    }

    @RequestMapping(value = "/saveQcCode")
    public @ResponseBody
    JdResponse<String> saveQcCode(@RequestBody AbnormalDisposeInspection abnormalDisposeInspection) {
        JdResponse<String> rest = new JdResponse<String>();
        Date date =new Date();
        //获取操作人信息封装数据
        LoginContext loginContext = LoginContext.getLoginContext();
        BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
        AbnormalQc abnormalQc =new AbnormalQc();
        abnormalQc.setWaveBusinessId(abnormalDisposeInspection.getWaveBusinessId());
        abnormalQc.setWaybillCode(abnormalDisposeInspection.getWaybillCode());
        abnormalQc.setCreateUserCode(userDto.getStaffNo());
        abnormalQc.setCreateUser(userDto.getAccountNumber());
        abnormalQc.setCreateUser(userDto.getStaffName());
        abnormalQc.setCreateSiteCode(userDto.getSiteCode());
        abnormalQc.setCreateSiteName(userDto.getSiteName());
        abnormalQc.setQcCode(abnormalDisposeInspection.getQcCode());
        abnormalQc.setOperateTime(date);
        abnormalQc.setCreateTime(date);

        Integer integer = abnormalDisposeService.saveInspection(abnormalQc);
        if(integer > -1) {
            rest.setCode(JdResponse.CODE_SUCCESS);
            rest.setData(abnormalDisposeInspection.getQcCode());
        }
        else{
            rest.setCode(JdResponse.CODE_FAIL);
        }
        return rest;
    }

}
