package com.jd.bluedragon.distribution.business.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.bluedragon.distribution.business.service.BusinessReturnAdressService;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

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

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

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
	 * @param businessReturnAdressCondition
	 * @return
	 */
	@Authorization(Constants.DMS_BUSINESS_RETURN_ADRESS_R)
	@RequestMapping(value = "/queryBusinessReturnAdressList")
	public @ResponseBody PagerResult<BusinessReturnAdress> queryEdnPickingList(@RequestBody BusinessReturnAdressCondition businessReturnAdressCondition) {
		return businessReturnAdressService.queryBusinessReturnAdressListByPagerCondition(businessReturnAdressCondition);
	}

	/**
	 * 导出excel
	 * @param businessReturnAdressCondition
	 * @return
	 */
	@Authorization(Constants.DMS_BUSINESS_RETURN_ADRESS_R)
	@RequestMapping(value = "/exportBusinessReturnAdressList")
	@JProfiler(jKey = "com.jd.bluedragon.distribution.business.controller.BusinessReturnAdressController.exportEdnPickingList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public InvokeResult exportEdnPickingList(BusinessReturnAdressCondition businessReturnAdressCondition, HttpServletResponse response) {
		InvokeResult result = new InvokeResult();
		BufferedWriter bfw = null;
		try {
			exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.BUSINESS_RETURN_ADDRESS_REPORT.getCode());
			String fileName = "商家退货地址";
			//设置文件后缀
			String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
			bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
			//设置响应
			CsvExporterUtils.setResponseHeader(response, fn);
			businessReturnAdressService.export(businessReturnAdressCondition,bfw);
			exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.BUSINESS_RETURN_ADDRESS_REPORT.getCode());
		} catch (Exception e) {
			log.error("商家退货地址 export error",e);
			result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
		}finally {
			try {
				if (bfw != null) {
					bfw.flush();
					bfw.close();
				}
			} catch (IOException es) {
				log.error("平台收货差异订单数据 流关闭异常", es);
				result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
			}
		}
		return result;
	}
}
