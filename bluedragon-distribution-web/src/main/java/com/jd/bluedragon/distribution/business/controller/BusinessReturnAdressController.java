package com.jd.bluedragon.distribution.business.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.bluedragon.distribution.business.service.BusinessReturnAdressService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;

/**
 *
 * @ClassName: CollectGoodsDetailController
 * @Description: --Controller实现
 * @author wuyoude
 * @date 2020年07月28日 13:56:21
 *
 */
@Controller
@RequestMapping("business/businessReturnAdress")
public class BusinessReturnAdressController extends DmsBaseController{

	private static final Logger log = LoggerFactory.getLogger(BusinessReturnAdressController.class);

	@Autowired
	private BusinessReturnAdressService businessReturnAdressService;

	@Autowired
	BaseMajorManager baseMajorManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_BUSINESS_RETURN_ADRESS_R)
	@RequestMapping(value = "/toBusinessReturnAdressIndex")
	public String toIndex(Model model) {
		LoginUser loginUser = this.getLoginUser();
		model.addAttribute("orgId",loginUser.getOrgId()).addAttribute("createSiteCode",loginUser.getSiteCode());
		return "/business/businessReturnAdressIndex";
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsDetailCondition
	 * @return
	 */
	@Authorization(Constants.DMS_BUSINESS_RETURN_ADRESS_R)
	@RequestMapping(value = "/queryBusinessReturnAdressList")
	public @ResponseBody PagerResult<BusinessReturnAdress> queryEdnPickingList(@RequestBody BusinessReturnAdressCondition businessReturnAdressCondition) {
		return businessReturnAdressService.queryBusinessReturnAdressListByPagerCondition(businessReturnAdressCondition);
	}
	/**
	 * 导出excel
	 * @param dmsScheduleInfoCondition
	 * @param model
	 * @return
	 */
	@Authorization(Constants.DMS_BUSINESS_RETURN_ADRESS_R)
	@RequestMapping(value = "/exportBusinessReturnAdressList")
	public ModelAndView exportEdnPickingList(BusinessReturnAdressCondition businessReturnAdressCondition, Model model) {
		try {
			List<List<Object>> resultList = businessReturnAdressService.queryBusinessReturnAdressExcelData(businessReturnAdressCondition);
			model.addAttribute("filename", "商家退货地址.xls");
			model.addAttribute("sheetname", "商家退货地址");
			model.addAttribute("contents", resultList);
			return new ModelAndView(new DefaultExcelView(), model.asMap());
		} catch (Exception e) {
			log.error("toExport:", e);
			return null;
		}
	}
}
