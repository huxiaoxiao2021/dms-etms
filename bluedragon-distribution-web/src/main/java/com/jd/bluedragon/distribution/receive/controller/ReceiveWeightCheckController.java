package com.jd.bluedragon.distribution.receive.controller;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckController
 * @Description: 揽收重量校验统计--Controller实现
 * @author: hujiping
 * @date: 2019/2/26 20:58
 */
@Controller
@RequestMapping("receive")
public class ReceiveWeightCheckController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(ReceiveWeightCheckController.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private ReceiveWeightCheckService receiveWeightCheckService;

    /**
     * 返回主页面
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        String userCode = "";
        Long createSiteCode = new Long(-1);
        Integer orgId = new Integer(-1);
        if(erpUser != null){
            userCode = erpUser.getUserCode();
            BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
            if (bssod!=null && bssod.getSiteType() == 64) {
                createSiteCode = new Long(bssod.getSiteCode());
                orgId = bssod.getOrgId();
            }
        }
        model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode);
        return "/receive/receiveWeightCheck";
    }

    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<ReceiveWeightCheckResult> listData(@RequestBody ReceiveWeightCheckCondition condition){

        PagerResult<ReceiveWeightCheckResult> result = new PagerResult<ReceiveWeightCheckResult>();
        List<ReceiveWeightCheckResult> list = receiveWeightCheckService.queryByCondition(condition);
        result.setRows(list);
        result.setTotal(list.size());
        return result;
    }

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(ReceiveWeightCheckCondition condition, Model model) {

        this.logger.info("导出揽收重量校验统计表");
        try{
            List<List<Object>> resultList = receiveWeightCheckService.getExportData(condition);
            model.addAttribute("filename", "揽收重量统计校验表.xls");
            model.addAttribute("sheetname", "揽收重量统计校验结果");
            model.addAttribute("contents", resultList);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        }catch (Exception e){
            this.logger.error("导出揽收重量统计校验表失败:" + e.getMessage(), e);
            return null;
        }
    }

}
