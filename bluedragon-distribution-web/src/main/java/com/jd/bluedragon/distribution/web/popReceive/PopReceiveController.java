package com.jd.bluedragon.distribution.web.popReceive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceiveDto;
import com.jd.bluedragon.distribution.popReveice.service.PopReceiveService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.JsonResult;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午06:39:49
 * 
 *             POP收货处理
 */
@Controller
@RequestMapping("/popReceive")
public class PopReceiveController {
	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private BaseService baseService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private PopReceiveService popReceiveService;
	
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListPage(Model model) {
		this.logger.info("跳转到查询POP收货处理列表页面");
		initSelectObject(null, model);
		return "popReceive/pop_receive_list";
	}

	/**
	 * 按条件查询POP收货处理数据集合
	 * 
	 * @param popReceiveDTO
	 * @param pager
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(PopReceiveDto popReceiveDTO, Pager pager, Model model) {
		this.logger.info("按条件查询POP收货处理数据集合");
		Map<String, Object> paramMap = ObjectMapHelper
				.makeObject2Map(popReceiveDTO);

		// 设置分页对象
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager(pager.getPageNo(), pager.getPageSize());
		}
		paramMap.putAll(ObjectMapHelper.makeObject2Map(pager));

		initSelectObject(paramMap, model);

		List<PopPrint> popReceives = null;

		if (!paramMap.isEmpty()) {
			try {
				// 获取总数量
				int totalSize = popReceiveService.findTotalCount(paramMap);
				logger.info("按条件查询POP收货处理数据集合 --> 获取总数量为：" + totalSize);
				if (totalSize > 0) {
					pager.setTotalSize(totalSize);
					popReceives = popReceiveService.findListNoReceive(paramMap);
				}

			} catch (Exception e) {
				logger.error("根据条件查询POP收货处理集合异常：", e);
			}
		}
		model.addAttribute("popReceives", popReceives);
		model.addAttribute("popReceiveDTO", popReceiveDTO);
		model.addAttribute("pager", pager);
		return "popReceive/pop_receive_list";
	}

	/**
	 * 补全订单收货信息
	 * 
	 * @param paramMap
	 * @return
	 */
	@RequestMapping(value = "/saveRecevie", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult saveRecevie(@RequestBody Map<String, String> paramMap) {
		this.logger.info("补全订单收货信息开始");
		String resultMsg = "";
		String dataStr = null;
		// 验证传入参数
		if (paramMap == null) {
			this.logger.info("补全订单收货信息，传入参数为空!");
			resultMsg = "传入参数为空!";
		} else {
			dataStr = paramMap.get("datas");
			if (StringUtils.isBlank(dataStr)) {
				this.logger.info("补全订单收货信息，传入参数有误：" + paramMap);
				resultMsg = "传入参数有误！";
			}
		}
		if (StringUtils.isNotBlank(resultMsg)) {
			this.logger.info("补全订单收货信息，" + resultMsg + " 返回");
			return new JsonResult(false, resultMsg);
		}
		try {
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String[] datas = dataStr.split(",");
			int resultCode = 0;
			for (String data : datas) {
				String[] codes = data.split("#");
				if (codes.length != 2) {
					this.logger.error("补全订单收货信息,转换数据有误，data: 【" + data + "】");
					continue;
				}
				String waybillCode = codes[0];
				String createSiteCode = codes[1];
				if (StringUtils.isBlank(waybillCode)) {
					this.logger.error("补全订单收货信息,需要保存运单为空");
					continue;
				}
				PopPrint popPrint = popReceiveService.findPopPrint(waybillCode);
				if (StringUtils.isBlank(createSiteCode)) {
					createSiteCode = popPrint.getCreateSiteCode().toString();
				}
				
				Inspection inspection = new Inspection();
				inspection.setWaybillCode(waybillCode);
				inspection.setInspectionType(Constants.BUSSINESS_TYPE_POP);
				inspection.setCreateUserCode(erpUser.getUserId());
				inspection.setCreateUser(Constants.POP_RECEIVE_NAME);
//				inspection.setCreateUser(erpUser.getUserName());
				inspection.setCreateTime(popPrint.getCreateTime());
				inspection.setCreateSiteCode(Integer.valueOf(createSiteCode));
				
				inspection.setPopSupId(popPrint.getPopSupId());
				inspection.setPopSupName(popPrint.getPopSupName());
				inspection.setQuantity(popPrint.getQuantity());
				inspection.setCrossCode(popPrint.getCrossCode());
				inspection.setWaybillType(popPrint.getWaybillType());
				inspection.setPopFlag(1);
				
				resultCode += popReceiveService.saveRecevie(inspection);
			}
			if (resultCode >= 1) {
				logger.info("补全订单收货信息，应操作总数【" + datas.length + "】，实际成功数【" + resultCode + "】");
				return new JsonResult(true, "操作成功");
			} else {
				logger.info("补全订单收货信息，应操作总数【" + datas.length + "】");
				return new JsonResult(false, "操作失败，请稍后重试！");
			}

		} catch (Exception e) {
			this.logger.error("补全订单收货信息:“" + paramMap + "” 异常：", e);
			return new JsonResult(false, "服务器异常，请稍后重试！");
		}
	}

	/**
	 * 初始化查询条件
	 * 
	 * @param paramMap
	 * @param model
	 */
	private void initSelectObject(Map<String, Object> paramMap, Model model) {
		// 验证登陆信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		List<BaseOrg> orgList = new ArrayList<BaseOrg>();
		List<BaseStaffSiteOrgDto> siteList = new ArrayList<BaseStaffSiteOrgDto>();
		try {
			this.logger.info("初始化查询条件-->调用基础资料获取某个员工信息开始");
			BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
					.getBaseStaffByStaffId(erpUser.getUserId());
			if (baseStaffSiteOrgDto != null) {
				Integer defaultOrgId = baseStaffSiteOrgDto.getOrgId();
				Integer defaultSiteCode = baseStaffSiteOrgDto.getSiteCode();
				Integer defaultSiteType = baseStaffSiteOrgDto.getSiteType();
				if (defaultSiteCode != null
						&& Constants.DMS_SITE_TYPE.equals(defaultSiteType)) {
					this.logger.info("初始化查询条件-->员工信息 属于分拣中心");
					if (paramMap != null) {
						paramMap.put("createSiteCode", defaultSiteCode);
					}
					siteList.add(baseStaffSiteOrgDto);
				} else {
					this.logger
							.info("初始化查询条件-->员工信息 不属于分拣中心，调用基础资料查询所属机构下的所有分拣中心站点信息开始");
					siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(
							defaultOrgId, Constants.DMS_SITE_TYPE);
					this.logger
							.info("初始化查询条件-->员工信息 不属于分拣中心，调用基础资料查询所属机构下的所有分拣中心站点信息结束");
				}
				if (defaultOrgId != null) {
					BaseOrg baseOrg = new BaseOrg();
					baseOrg.setOrgId(defaultOrgId);
					baseOrg.setOrgName(baseStaffSiteOrgDto.getOrgName());
					orgList.add(baseOrg);
				}
			} else {
				this.logger.info("初始化查询条件-->调用基础资料获取某个员工信息 为空");
				orgList = baseService.getAllOrg();
				if (paramMap != null && paramMap.get("orgCode") != null) {
					siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(
							(Integer)paramMap.get("orgCode"), Constants.DMS_SITE_TYPE);
				}
			}
		} catch (Exception e) {
			this.logger.error("初始化查询条件异常", e);
		}
		model.addAttribute("orgList", orgList);
		model.addAttribute("siteList", siteList);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}
	/**
	 * POP实收列表
	 * @return
	 */
	@RequestMapping("/getPopRecieveList")
	public String getPopRecieveList(PopReceiveDto popReceiveDTO,Model model,Pager pager){
		select(model,popReceiveDTO);
		if(StringUtils.isEmpty(popReceiveDTO.getOperateStartTime())&&StringUtils.isEmpty(popReceiveDTO.getWaybillCode())&&StringUtils.isEmpty(popReceiveDTO.getThirdWaybillCode())){
			String operateStartTime=DateHelper.formatDate(new Date())+ " 00:00:00";
			String operateEndTime=DateHelper.formatDate(new Date())+ " 23:59:59";
			popReceiveDTO.setOperateStartTime(operateStartTime);
			popReceiveDTO.setOperateEndTime(operateEndTime);
			model.addAttribute("query", popReceiveDTO);
			return "popReceive/getPopRecieveList";
		}
		if (pager == null) {
			pager = new Pager(Pager.DEFAULT_PAGE_NO);
		} else {
			pager= new Pager(pager.getPageNo(), pager.getPageSize());
		}
		Map<String,Object> queryMap = new HashMap<String,Object>();
		
		if(StringUtils.isNotEmpty(popReceiveDTO.getWaybillCode())){
			queryMap.put("waybillCode", popReceiveDTO.getWaybillCode());
		}
		if(StringUtils.isNotEmpty(popReceiveDTO.getThirdWaybillCode())){
			queryMap.put("thirdWaybillCode", popReceiveDTO.getThirdWaybillCode());
		}
		if(popReceiveDTO.getCreateSiteCode()!=null){
			queryMap.put("createSiteCode", popReceiveDTO.getCreateSiteCode());
		}
		if(StringUtils.isNotEmpty(popReceiveDTO.getOperateStartTime())){
			queryMap.put("operateStartTime", popReceiveDTO.getOperateStartTime());
		}
		if(StringUtils.isNotEmpty(popReceiveDTO.getOperateEndTime())){
			queryMap.put("operateEndTime", popReceiveDTO.getOperateEndTime());
		}
		int count =this.popReceiveService.count(queryMap);
		pager.setTotalSize(count);
		queryMap.put("start", pager.getStartIndex());
		queryMap.put("end", pager.getEndIndex());
		List<PopReceive> dataList =this.popReceiveService.findPopReceiveList(queryMap);
		logger.info("count:"+count);
		model.addAttribute("dataList", dataList);
		model.addAttribute("pager", pager);
		model.addAttribute("query", popReceiveDTO);
		return "popReceive/getPopRecieveList";
	}
	
	private void select(Model model, PopReceiveDto query) {
		LoginContext loginContext = LoginContext.getLoginContext();
		Long userId = loginContext.getUserId();
		List<BaseOrg> orgList = new ArrayList<BaseOrg>();
		Integer defaultSiteCode = null;
		Integer defaultOrgId = null;
		Integer defaultSiteType = null;
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager.getBaseStaffByStaffId(userId.intValue());
		if (baseStaffSiteOrgDto != null) {
			defaultSiteCode = baseStaffSiteOrgDto.getSiteCode();
			defaultOrgId = baseStaffSiteOrgDto.getOrgId();
			defaultSiteType = baseStaffSiteOrgDto.getSiteType();
			if (defaultSiteCode != null && defaultSiteType.equals(64)) {
				model.addAttribute("defaultSiteCode", defaultSiteCode);
				model.addAttribute("defaultSiteType", defaultSiteType);
			}
			if (defaultOrgId != null) {
				model.addAttribute("defaultOrgId", defaultOrgId);
				BaseOrg baseOrg = new BaseOrg();
				baseOrg.setOrgId(defaultOrgId);
				baseOrg.setOrgName(baseStaffSiteOrgDto.getOrgName());
				orgList.add(baseOrg);
			}
		} else {

			orgList = baseService.getAllOrg();
		}
		model.addAttribute("orgList", orgList);
		List<BaseStaffSiteOrgDto> siteList = new ArrayList<BaseStaffSiteOrgDto>();
		if (defaultSiteType != null && defaultSiteType.equals( Constants.DMS_SITE_TYPE)) {
			siteList.add(baseStaffSiteOrgDto);
		} else if (defaultOrgId != null) {
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(defaultOrgId,Constants.DMS_SITE_TYPE);
		} else if (query.getOrgCode() != null) {
			siteList = this.baseMajorManager.getBaseSiteByOrgIdSubType(query.getOrgCode(),Constants.DMS_SITE_TYPE);
		}
		model.addAttribute("siteList", siteList);
	}
}
