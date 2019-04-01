package com.jd.bluedragon.distribution.reverse.part.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetailCondition;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: ReversePartDetailController
 * @Description: 半退明细表--Controller实现
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
@Controller
@RequestMapping("reverse/part/reversePartDetail")
public class ReversePartDetailController {

	private static final Log logger = LogFactory.getLog(ReversePartDetailController.class);

	@Autowired
	private ReversePartDetailService reversePartDetailService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_SORTING_REVERSEPARTDETAIL_CHECK_R)
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

		return "/reverse/part/reversePartDetail";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<ReversePartDetail> detail(@PathVariable("id") Long id) {
		JdResponse<ReversePartDetail> rest = new JdResponse<ReversePartDetail>();
		rest.setData(reversePartDetailService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param reversePartDetail
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody ReversePartDetail reversePartDetail) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(reversePartDetailService.saveOrUpdate(reversePartDetail));
	} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(reversePartDetailService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param reversePartDetailCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<ReversePartDetail> listData(@RequestBody ReversePartDetailCondition reversePartDetailCondition) {
		JdResponse<PagerResult<ReversePartDetail>> rest = new JdResponse<PagerResult<ReversePartDetail>>();


		rest.setData(reversePartDetailService.queryByPagerCondition(reversePartDetailCondition));
		return rest.getData();
	}

	@RequestMapping(value = "/allSendPack/{createSiteCode}/{waybillCode}")
	public @ResponseBody JdResponse<List<ReversePartDetail>> allSendPack(@PathVariable("waybillCode") String waybillCode,@PathVariable("createSiteCode") String createSiteCode) {
		JdResponse<List<ReversePartDetail>> rest = new JdResponse<List<ReversePartDetail>>();
		try{
			rest.setData(reversePartDetailService.queryAllSendPack(waybillCode,createSiteCode));
		}catch (Exception e){
			logger.error("获取累计发货数据异常"+waybillCode+" "+createSiteCode,e);
			rest.setCode(JdResponse.CODE_ERROR);
			rest.setMessage(JdResponse.MESSAGE_ERROR);
		}
		return rest;
	}

	@RequestMapping(value = "/noSendPack/{createSiteCode}/{waybillCode}")
	public @ResponseBody JdResponse<List<String>> noSendPack(@PathVariable("waybillCode") String waybillCode,@PathVariable("createSiteCode") String createSiteCode) {
		JdResponse<List<String>> rest = new JdResponse<List<String>>();
		try{
			rest.setData(reversePartDetailService.queryNoSendPack(waybillCode,createSiteCode));
		}catch (Exception e){
			logger.error("获取未发货数据异常"+waybillCode+" "+createSiteCode,e);
			rest.setCode(JdResponse.CODE_ERROR);
			rest.setMessage(JdResponse.MESSAGE_ERROR);
		}
		return rest;
	}


	/**
	 * 导出
 	 * @param reversePartDetailCondition
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/toExport")
	public ModelAndView toExport(ReversePartDetailCondition reversePartDetailCondition, Model model) {
		try {


			//超过5000条不允许导出
			int count = reversePartDetailService.queryByParamCount(reversePartDetailCondition);

			List<List<Object>> resultList = new ArrayList<List<Object>>();
			if(count>5000){

				List<Object> errorResult = new ArrayList<Object>();
				errorResult.add("导出数据过多，请变更条件筛选");
				resultList.add(errorResult);

			}else{
				resultList = reversePartDetailService.getExportData(reversePartDetailCondition);
			}


			model.addAttribute("filename", "半退发货明细.xls");
			model.addAttribute("sheetname", "半退发货明细");
			model.addAttribute("contents", resultList);

			return new ModelAndView(new DefaultExcelView(), model.asMap());

		} catch (Exception e) {
			logger.error("toExport:" + e.getMessage(), e);
			return null;
		}
	}
}
