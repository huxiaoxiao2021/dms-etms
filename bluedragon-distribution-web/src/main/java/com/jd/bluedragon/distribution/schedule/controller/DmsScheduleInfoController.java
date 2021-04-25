package com.jd.bluedragon.distribution.schedule.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchPdfDto;
import com.jd.jp.print.templet.center.sdk.dto.EdnDeliveryReceiptBatchRequest;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.service.DmsScheduleInfoService;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;

import javax.servlet.http.HttpServletResponse;

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

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

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
	 * @param dmsScheduleInfoCondition
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
	 * @param response
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/exportEdnPickingList")
	@ResponseBody
	@JProfiler(jKey = "com.jd.bluedragon.distribution.schedule.controller.DmsScheduleInfoController.exportEdnPickingList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public InvokeResult exportEdnPickingList(DmsScheduleInfoCondition dmsScheduleInfoCondition, HttpServletResponse response) {
		InvokeResult result = new InvokeResult();
		BufferedWriter bfw = null;
		log.info("企配仓拣货导出");
		try {
			exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.DMS_SCHEDULE_INFO_REPORT.getCode());
			String fileName = "企配仓拣货";
			//设置文件后缀
			String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
			bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
			//设置响应
			CsvExporterUtils.setResponseHeader(response, fn);
			dmsScheduleInfoService.export(dmsScheduleInfoCondition,bfw);
			exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.DMS_SCHEDULE_INFO_REPORT.getCode());
		} catch (Exception e) {
			log.error("企配仓拣货导出 toExport:", e);
			result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
		}finally {
			try {
				if (bfw != null) {
					bfw.flush();
					bfw.close();
				}
			} catch (IOException es) {
				log.error("导出企配仓拣货 流关闭异常", es);
				result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
			}
		}
		return result;
	}

	/**
	 * 根据scheduleBillCode获取基本信息
	 * @param scheduleBillCode
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
	 * @param scheduleBillCode
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/printEdnPickingList/{scheduleBillCode}")
	public @ResponseBody JdResponse<String> printEdnPickingList(@PathVariable("scheduleBillCode") String scheduleBillCode) {
		return dmsScheduleInfoService.printEdnPickingList(scheduleBillCode,this.getLoginUser());
	}
	/**
	 * 根据scheduleBillCode打印交接单
	 * @param scheduleBillCode
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/printEdnDeliveryReceipt/{scheduleBillCode}")
	public @ResponseBody JdResponse<List<DmsEdnBatchVo>> printEdnDeliveryReceipt(@PathVariable("scheduleBillCode") String scheduleBillCode) {
		return dmsScheduleInfoService.printEdnDeliveryReceipt(scheduleBillCode,this.getLoginUser());
	}

	/**
	 *  批量打印
	 * @param param
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EDN_PICKING_R)
	@RequestMapping(value = "/generatePdfUrlByBatchList")
	@ResponseBody
	public JdResponse<EdnDeliveryReceiptBatchPdfDto> generatePdfUrlByBatchList(@RequestBody EdnDeliveryReceiptBatchRequest param){
		 return dmsScheduleInfoService.generatePdfUrlByBatchList(param);
	}
}
