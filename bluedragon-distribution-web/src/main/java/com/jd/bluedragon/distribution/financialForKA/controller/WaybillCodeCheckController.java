package com.jd.bluedragon.distribution.financialForKA.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.bluedragon.distribution.financialForKA.service.WaybillCodeCheckService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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

    @Autowired
    private WaybillCodeCheckService waybillCodeCheckService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        LoginUser loginUser = getLoginUser();
        model.addAttribute("operateSiteCode",loginUser.getSiteCode());
        model.addAttribute("operateSiteName",loginUser.getSiteName());
        model.addAttribute("operateErp",loginUser.getUserErp());
        return "/financialForKA/waybillCodeCheck";
    }

    /**
     * 条码校验
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/check")
    @ResponseBody
    public InvokeResult waybillCodeCheck(@RequestBody WaybillCodeCheckCondition condition){
        return waybillCodeCheckService.waybillCodeCheck(condition);
    }

    /**
     * 跳转查询页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/toSearchIndex")
    public String toSearchIndex(){
        return "/financialForKA/financialForKAsearch";
    }


    /**
     * 获取明细
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<WaybillCodeCheckDto> listData(@RequestBody KaCodeCheckCondition condition){
        return waybillCodeCheckService.listData(condition);
    }

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(KaCodeCheckCondition condition, Model model) {

        this.log.info("KA条码对比校验操作记录统计表");
        List<List<Object>> resultList;
        try{
            model.addAttribute("filename", "KA条码对比校验操作记录统计表.xls");
            model.addAttribute("sheetname", "KA条码对比校验操作记录");
            resultList = waybillCodeCheckService.getExportData(condition);
        }catch (Exception e){
            this.log.error("导出KA条码对比校验操作记录统计表失败:", e);
            List<Object> list = new ArrayList<>();
            list.add("导出KA条码对比校验操作记录统计表失败!");
            resultList = new ArrayList<>();
            resultList.add(list);
        }
        model.addAttribute("contents", resultList);
        return new ModelAndView(new DefaultExcelView(), model.asMap());
    }

}
