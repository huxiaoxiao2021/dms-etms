package com.jd.bluedragon.distribution.collect.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
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

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: CollectGoodsDetailController
 * @Description: --Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("collect/collectGoodsDetail")
public class CollectGoodsDetailController {

	private static final Logger log = LoggerFactory.getLogger(CollectGoodsDetailController.class);

	@Autowired
	private CollectGoodsDetailService collectGoodsDetailService;

	@Autowired
	BaseMajorManager baseMajorManager;

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/toIndex")
	public String toIndex(Model model) {

		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		Long createSiteCode = new Long(-1);
		Integer orgId = new Integer(-1);

		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
				createSiteCode = new Long(bssod.getSiteCode());
				orgId = bssod.getOrgId();
			}
		}

		model.addAttribute("orgId",orgId).addAttribute("createSiteCode",createSiteCode);
		return "/collect/collectGoodsDetail";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<CollectGoodsDetail> detail(@PathVariable("id") Long id) {
		JdResponse<CollectGoodsDetail> rest = new JdResponse<CollectGoodsDetail>();
		rest.setData(collectGoodsDetailService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param collectGoodsDetail
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody CollectGoodsDetail collectGoodsDetail) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(collectGoodsDetailService.saveOrUpdate(collectGoodsDetail));
	} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(collectGoodsDetailService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsDetailCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<CollectGoodsDetail> listData(@RequestBody CollectGoodsDetailCondition collectGoodsDetailCondition) {
		JdResponse<PagerResult<CollectGoodsDetail>> rest = new JdResponse<PagerResult<CollectGoodsDetail>>();
		rest.setData(collectGoodsDetailService.queryByPagerCondition(collectGoodsDetailCondition));
		return rest.getData();
	}

	/**
	 * 获取明细
	 * @param collectGoodsDetailCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/showViews")
	public @ResponseBody JdResponse<List<CollectGoodsDetail>> showViews(@RequestBody CollectGoodsDetailCondition collectGoodsDetailCondition) {
		JdResponse<List<CollectGoodsDetail>> rest = new JdResponse<>();
		rest.setData(collectGoodsDetailService.queryByCondition(collectGoodsDetailCondition));
		return rest;
	}

	@RequestMapping(value = "/checkConcurrencyLimit")
	@ResponseBody
	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@JProfiler(jKey = "com.jd.bluedragon.distribution.collect.controller.CollectGoodsDetailController.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public InvokeResult checkConcurrencyLimit(){
		InvokeResult result = new InvokeResult();
		try {
			//校验并发
			if(!exportConcurrencyLimitService.checkConcurrencyLimit(Constants.DMS_WEB_COLLECT_REPORT)){
				result.customMessage(InvokeResult.RESULT_EXPORT_LIMIT_CODE,InvokeResult.RESULT_EXPORT_LIMIT_MESSAGE);
				return result;
			}
		}catch (Exception e){
			log.error("校验导出并发接口异常-暂存记录统计表",e);
			result.customMessage(InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_CODE,InvokeResult.RESULT_EXPORT_CHECK_CONCURRENCY_LIMIT_MESSAGE);
			return result;
		}
		return result;
	}

	@Authorization(Constants.DMS_WEB_COLLECT_REPORT)
	@RequestMapping(value = "/toExport")
	@ResponseBody
	@JProfiler(jKey = "com.jd.bluedragon.distribution.collect.controller.CollectGoodsDetailController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public InvokeResult toExport(CollectGoodsDetailCondition collectGoodsDetailCondition, HttpServletResponse response) {
		InvokeResult result = new InvokeResult();
		BufferedWriter bfw = null;
		log.info("导出集货报表");
		try {
			String fileName = "集货报表";
			//设置文件后缀
			String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
			bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
			//设置响应
			CsvExporterUtils.setResponseHeader(response, fn);
			collectGoodsDetailService.getExportData(collectGoodsDetailCondition,bfw);
		} catch (Exception e) {
			log.error("导出 集货报表异常:", e);
			result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
		}finally {
			try {
				if (bfw != null) {
					bfw.flush();
					bfw.close();
				}
			} catch (IOException es) {
				log.error("导出集货报表 流关闭异常", es);
				result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
			}
		}
		return result;
	}
}
