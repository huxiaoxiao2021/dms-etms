package com.jd.bluedragon.distribution.schedule.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;

/**
 *
 * @ClassName: CollectGoodsDetailController
 * @Description: --Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("schedule/dmsScheduleInfo")
public class DmsScheduleInfoController extends DmsBaseController{

	private static final Logger log = LoggerFactory.getLogger(DmsScheduleInfoController.class);

	@Autowired
	private DmsScheduleInfoService dmsScheduleInfoService;

	@Autowired
	BaseMajorManager baseMajorManager;

	/**
	 * 返回企配仓拣货主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/toDmsEdnPickingIndex")
	public String toIndex(Model model) {
		LoginUser loginUser = this.getLoginUser();
		model.addAttribute("orgId",loginUser.getOrgId()).addAttribute("createSiteCode",loginUser.getSiteCode());
		return "/schedule/dmsEdnPickingIndex";
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsDetailCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/queryEdnPickingList")
	public @ResponseBody PagerResult<DmsEdnPickingVo> queryEdnPickingList(@RequestBody DmsScheduleInfoCondition dmsScheduleInfoCondition) {
		return dmsScheduleInfoService.queryEdnPickingListByPagerCondition(dmsScheduleInfoCondition);
	}
	/**
	 * 导出excel
	 * @param dmsScheduleInfoCondition
	 * @param model
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/exportEdnPickingList")
	public ModelAndView exportEdnPickingList(DmsScheduleInfoCondition dmsScheduleInfoCondition, Model model) {
		try {
			List<List<Object>> resultList = dmsScheduleInfoService.queryEdnPickingExcelData(dmsScheduleInfoCondition);
			model.addAttribute("filename", "企配仓拣货.xls");
			model.addAttribute("sheetname", "企配仓拣货");
			model.addAttribute("contents", resultList);
			return new ModelAndView(new DefaultExcelView(), model.asMap());
		} catch (Exception e) {
			log.error("toExport:", e);
			return null;
		}
	}
	/**
	 * 根据scheduleBillCode获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/queryEdnPickingVo/{scheduleBillCode}")
	public @ResponseBody JdResponse<DmsEdnPickingVo> queryEdnPickingVo(@PathVariable("scheduleBillCode") String scheduleBillCode) {
		JdResponse<DmsEdnPickingVo> rest = new JdResponse<DmsEdnPickingVo>();
		rest.setData(dmsScheduleInfoService.queryDmsEdnPickingVoForView(scheduleBillCode));
		return rest;
	}
	/**
	 * 根据scheduleBillCode打印取件单
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/printEdnPickingList/{scheduleBillCode}")
	public @ResponseBody JdResponse<String> printEdnPickingList(@PathVariable("scheduleBillCode") String scheduleBillCode) {
		return dmsScheduleInfoService.printEdnPickingList(scheduleBillCode);
	}
	/**
	 * 根据scheduleBillCode打印交接单
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/printEdnDeliveryReceipt/{scheduleBillCode}")
	public @ResponseBody JdResponse<List<DmsEdnBatchVo>> printEdnDeliveryReceipt(@PathVariable("scheduleBillCode") String scheduleBillCode) {
		return dmsScheduleInfoService.printEdnDeliveryReceipt(scheduleBillCode);
	}
}
