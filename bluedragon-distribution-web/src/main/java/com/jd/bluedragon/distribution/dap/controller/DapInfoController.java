package com.jd.bluedragon.distribution.dap.controller;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfoCondition;
import com.jd.bluedragon.distribution.consumable.domain.PackingTypeEnum;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.rest.dap.DapInfo;
import com.jd.bluedragon.distribution.rest.dap.DapResource;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PackingConsumableInfoController
 * @Description: 包装耗材信息表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("dap/info")
public class DapInfoController extends DmsBaseController {

	private static final Log logger = LogFactory.getLog(DapInfoController.class);

	@Autowired
	private DapResource dapResource;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/dap/dapInfo";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/toTableRowIndex")
	public String toTableRowIndex() {
		return "/dap/tableRowInfo";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
    @RequestMapping(value = "/toReprintIndex")
    public String toReprintIndex() {
        return "/dap/reprintInfo";
    }

	@Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DapInfo> listData(@RequestParam("dbId") Integer dbId) {

		PagerResult<DapInfo> pagerResult = new PagerResult<>();
		List<DapInfo> list = null;
		if (dbId == 1) {
			list = dapResource.getUndiv().getData();
		} else if (dbId == 2) {
			list = dapResource.getTask().getData();
		} else if (dbId == 3) {
			list = dapResource.getDiv().getData();
		}
		pagerResult.setRows(list);
		pagerResult.setTotal(list == null ? 0 : list.size());
		return pagerResult;
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/listTableRowData")
	public @ResponseBody PagerResult<DapInfo> listTableRowData(@RequestParam("dbId") Integer dbId) {

		PagerResult<DapInfo> pagerResult = new PagerResult<>();
		List<DapInfo> list = null;
		if (dbId == 1) {
			list = dapResource.getUndivTableRows().getData();
		} else if (dbId == 2) {
			list = dapResource.getTaskTableRows().getData();
		}
		pagerResult.setRows(list);
		pagerResult.setTotal(list == null ? 0 : list.size());
		return pagerResult;
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
    @RequestMapping(value = "/listPddReprintData")
    public @ResponseBody PagerResult<DapInfo> listPddReprintData(@RequestParam("dateGap") Integer dateGap) {

        PagerResult<DapInfo> pagerResult = new PagerResult<>();
        if (dateGap >= 0) {
            List<DapInfo> list = dapResource.getPddReprintInfo(dateGap).getData();

            pagerResult.setRows(list);
            pagerResult.setTotal(list == null ? 0 : list.size());
        }

        return pagerResult;
    }

    /**
     * 导出
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
    @RequestMapping(value = "/toPddReprintExport", method = RequestMethod.POST)
    public ModelAndView toPddReprintExport(Integer dateGap, Model model) {

        logger.info("导出转运清场异常结果");
        try{
            List<List<Object>> resList = new ArrayList<List<Object>>();
            List<Object> heads = new ArrayList<>();
            //添加表头
            heads.add("补打条码");
            heads.add("操作单位");
            heads.add("操作人");
            heads.add("操作时间");

            resList.add(heads);
            List<DapInfo> list = dapResource.getPddReprintInfo(dateGap).getData();
            if (list != null && ! list.isEmpty()) {
                //表格信息
                for(DapInfo dapInfo : list){
                    List<Object> body = Lists.newArrayList();
                    body.add(dapInfo.getSpace1());
                    body.add(dapInfo.getSpace2());
                    body.add(dapInfo.getSpace3());
                    body.add(dapInfo.getSpace4());
                    resList.add(body);
                }
            }
            model.addAttribute("filename", "PDD补打记录.xls");
            model.addAttribute("sheetname", "PDD补打记录");
            model.addAttribute("contents", resList);
            return new ModelAndView(new DefaultExcelView(), model.asMap());
        }catch (Exception e){
            logger.error("导出PDD补打记录失败:" + e.getMessage(), e);
            return null;
        }
    }
}
