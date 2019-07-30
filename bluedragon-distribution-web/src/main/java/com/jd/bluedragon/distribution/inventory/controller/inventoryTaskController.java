package com.jd.bluedragon.distribution.inventory.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.inventory.domain.InventoryException;
import com.jd.bluedragon.distribution.inventory.domain.InventoryExceptionCondition;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTask;
import com.jd.bluedragon.distribution.inventory.domain.InventoryTaskCondition;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.inventory.service.InventoryTaskService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/inventoryTask")
public class inventoryTaskController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(inventoryTaskController.class);
    
    @Autowired
    private InventoryTaskService inventoryTaskService;

    /**
     * 返回主页面
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        return "/inventory/inventoryTask";
    }

    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<InventoryTask> listData(@RequestBody InventoryTaskCondition condition){

        PagerResult<InventoryTask> result = new PagerResult<>();
        try {
            result = inventoryTaskService.queryByPagerCondition(condition);
        } catch (Exception e) {
            logger.error("获取清场任务分页数据异常", e);
        }
        return result;
    }

    /**
     * 导出
     */
    @Authorization(Constants.DMS_WEB_SORTING_INVENTORYTASK_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(InventoryTaskCondition condition, Model model) {

        logger.info("转运清场任务信息结果");
        try{
            List<List<Object>> resultList = inventoryTaskService.getExportData(condition);
            model.addAttribute("filename", "转运清场任务信息表.xls");
            model.addAttribute("sheetname", "转运清场任务信息结果");
            model.addAttribute("contents", resultList);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        }catch (Exception e){
            logger.error("导出转运清场任务信息表失败:" + e.getMessage(), e);
            return null;
        }
    }


}
