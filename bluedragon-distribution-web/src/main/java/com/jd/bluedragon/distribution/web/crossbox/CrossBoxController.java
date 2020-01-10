package com.jd.bluedragon.distribution.web.crossbox;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.CrossBoxRequest;
import com.jd.bluedragon.distribution.api.response.CrossBoxResponse;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 跨分拣箱号中转维护
 * 
 * @author xumei1
 *
 */
@Controller
@RequestMapping("/base/crossbox")
public class CrossBoxController {
	private static final Logger log = LoggerFactory.getLogger(CrossBoxController.class);

	@Autowired
	private CrossBoxService crossBoxService;

	@Autowired
	private BaseMajorManager baseSiteManager;

	@Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping("/index")
	public String index(CrossBoxRequest crossBoxRequest, Model model) {
			if(!ObjectMapHelper.makeObject2Map(crossBoxRequest).isEmpty()){
				HashMap queryInfo = new HashMap();/** 用于传入查询参数，只用在数据返回 **/
				try{
					queryInfo.put("originateOrg",crossBoxRequest.getOriginateOrg());
					queryInfo.put("originateOrgName",URLDecoder.decode(crossBoxRequest.getOriginateOrgName(),"UTF-8"));
					queryInfo.put("originalDmsName", URLDecoder.decode(crossBoxRequest.getOriginalDmsName(),"UTF-8"));
					queryInfo.put("updateOperatorName",URLDecoder.decode(crossBoxRequest.getUpdateOperatorName(),"UTF-8"));
					queryInfo.put("destinationOrg",crossBoxRequest.getDestinationOrg());
					queryInfo.put("destinationOrgName",URLDecoder.decode(crossBoxRequest.getDestinationOrgName(),"UTF-8"));
					queryInfo.put("destinationDmsName",URLDecoder.decode(crossBoxRequest.getDestinationDmsName(),"UTF-8"));
					queryInfo.put("startDate",crossBoxRequest.getStartDate());
					queryInfo.put("endDate",crossBoxRequest.getEndDate());
					queryInfo.put("transferOrg",crossBoxRequest.getTransferOrg());
					queryInfo.put("transferOrgName",URLDecoder.decode(crossBoxRequest.getTransferOrgName(),"UTF-8"));
					queryInfo.put("transferName",URLDecoder.decode(crossBoxRequest.getTransferName(),"UTF-8"));
					queryInfo.put("yn",crossBoxRequest.getYn());
				}catch(UnsupportedEncodingException e){
					log.error("查询条件参数解码异常：",e);
				}
				model.addAttribute("queryInfo",queryInfo);
			}


		return "crossbox/list";
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping("/query")
	@ResponseBody
	public CrossBoxResponse<Pager<List<CrossBox>>> query(CrossBoxRequest crossBoxRequest, Pager<List<CrossBox>> pager) {
		CrossBoxResponse<Pager<List<CrossBox>>> crossBoxResponse = new CrossBoxResponse<Pager<List<CrossBox>>>();
		try {
			if (crossBoxRequest != null) {
				if (StringUtils.isNotBlank(crossBoxRequest.getCreateDateString())) {
					Date createdate = DateHelper.parseDate(crossBoxRequest.getCreateDateString());
					crossBoxRequest.setCreateDate(createdate);
				}
				if (StringUtils.isNotBlank(crossBoxRequest.getUpdateDateString())) {
					Date updatedate = DateHelper.parseDate(crossBoxRequest.getUpdateDateString());
					crossBoxRequest.setUpdateDate(updatedate);
				}
			}

			List<CrossBox> resultList = crossBoxService.queryByCondition(crossBoxRequest, pager);

			// 设置分页对象
			if (pager == null) {
				pager = new Pager<List<CrossBox>>(Pager.DEFAULT_PAGE_NO);
			}

			pager.setData(resultList);
			crossBoxResponse.setData(pager);
			crossBoxResponse.setCode(CrossBoxResponse.CODE_NORMAL);

		} catch (Exception e) {
			crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
			crossBoxResponse.setData(null);
			crossBoxResponse.setMessage(e.getMessage());
		}
		return crossBoxResponse;
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		return "crossbox/add";
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping("/toEdit")
	public String toEdit(Integer id, CrossBoxRequest crossBoxRequest, Model model) {
		HashMap queryInfo = new HashMap();/** 组装查询条件 **/
		try{
			queryInfo.put("originateOrg",crossBoxRequest.getOriginateOrg());
			queryInfo.put("originateOrgName",URLDecoder.decode(crossBoxRequest.getOriginateOrgName(),"UTF-8"));
			queryInfo.put("originalDmsName", URLDecoder.decode(crossBoxRequest.getOriginalDmsName(),"UTF-8"));
			queryInfo.put("updateOperatorName",URLDecoder.decode(crossBoxRequest.getUpdateOperatorName(),"UTF-8"));
			queryInfo.put("destinationOrg",crossBoxRequest.getDestinationOrg());
			queryInfo.put("destinationOrgName",URLDecoder.decode(crossBoxRequest.getDestinationOrgName(),"UTF-8"));
			queryInfo.put("destinationDmsName",URLDecoder.decode(crossBoxRequest.getDestinationDmsName(),"UTF-8"));
			queryInfo.put("startDate",crossBoxRequest.getStartDate());
			queryInfo.put("endDate",crossBoxRequest.getEndDate());
			queryInfo.put("transferOrg",crossBoxRequest.getTransferOrg());
			queryInfo.put("transferOrgName",URLDecoder.decode(crossBoxRequest.getTransferOrgName(),"UTF-8"));
			queryInfo.put("transferName",URLDecoder.decode(crossBoxRequest.getTransferName(),"UTF-8"));
			queryInfo.put("yn",crossBoxRequest.getYn());
		}catch(UnsupportedEncodingException e){
			log.error("对查询条件中的汉字解码失败：",e);
		}
		try {
			CrossBox crossBox = crossBoxService.getCrossBoxById(id);
			model.addAttribute("originateOrgId", getSiteOrgId(crossBox.getOriginalDmsId()));
			model.addAttribute("originateOrgName", getSiteOrgName(crossBox.getOriginalDmsId()));

			model.addAttribute("destinationOrgId", getSiteOrgId(crossBox.getDestinationDmsId()));
			model.addAttribute("destinationOrgName", getSiteOrgName(crossBox.getDestinationDmsId()));

			model.addAttribute("transferOneOrgId", getSiteOrgId(crossBox.getTransferOneId()));
			model.addAttribute("transferOneOrgName", getSiteOrgName(crossBox.getTransferOneId()));

			model.addAttribute("transferTwoOrgId", getSiteOrgId(crossBox.getTransferTwoId()));
			model.addAttribute("transferTwoOrgName", getSiteOrgName(crossBox.getTransferTwoId()));

			model.addAttribute("transferThreeOrgId", getSiteOrgId(crossBox.getTransferThreeId()));
			model.addAttribute("transferThreeOrgName", getSiteOrgName(crossBox.getTransferThreeId()));

			model.addAttribute("crossDmsBox", crossBox);
			model.addAttribute("queryInfo",queryInfo);/** 渲染出查询参数 **/
		} catch (Exception e) {
			log.error("进入跨分拣箱号中转修改页面异常：", e);
		}
		return "crossbox/add";
	}

	private Integer getSiteOrgId(Integer siteCode) {
		BaseStaffSiteOrgDto dto = baseSiteManager.getBaseSiteBySiteId(siteCode);
		if (dto != null) {
			return dto.getOrgId();
		}
		return null;
	}

	private String getSiteOrgName(Integer siteCode) {
		BaseStaffSiteOrgDto dto = baseSiteManager.getBaseSiteBySiteId(siteCode);
		if (dto != null) {
			return dto.getOrgName();
		}
		return null;
	}

	@Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@ResponseBody
	@RequestMapping("/check")
	public CrossBoxResponse<String> check(CrossBox crossBox) {
		CrossBoxResponse<String> crossBoxResponse = new CrossBoxResponse<String>();
		try {
			if (crossBox != null) {
				// 分拣中心名称校验
				if (crossBox.getOriginalDmsName() == null || crossBox.getOriginalDmsName().equals("") 
						|| crossBox.getDestinationDmsName() == null || crossBox.getDestinationDmsName().equals("") 
						|| crossBox.getTransferOneName() == null || crossBox.getTransferOneName().equals("")) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("始发分拣中心、目的分拣中心、中转1不能为空");
					return crossBoxResponse;
				}

				int id;
				String name;
				int result;

				name = crossBox.getOriginalDmsName();
				if (crossBox.getOriginalDmsId() == null) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
					return crossBoxResponse;
				}
				id = crossBox.getOriginalDmsId();
				result = dmsNameVertify(id, name);
				if (result == -1) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
					return crossBoxResponse;
				}

				name = crossBox.getDestinationDmsName();
				if (crossBox.getDestinationDmsId() == null) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
					return crossBoxResponse;
				}
				id = crossBox.getDestinationDmsId();
				result = dmsNameVertify(id, name);
				if (result == -1) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
					return crossBoxResponse;
				}

				name = crossBox.getTransferOneName();
				if (crossBox.getTransferOneId() == null) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
					return crossBoxResponse;
				}
				id = crossBox.getTransferOneId();
				result = dmsNameVertify(id, name);
				if (result == -1) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
					crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
					return crossBoxResponse;
				}

				if (crossBox.getTransferTwoName() != null && (!crossBox.getTransferTwoName().equals(""))) {
					name = crossBox.getTransferTwoName();
					if(crossBox.getTransferTwoId() == null){
						crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
						crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
						return crossBoxResponse;
					}
					id = crossBox.getTransferTwoId();
					result = dmsNameVertify(id, name);
					if (result == -1) {
						crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
						crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
						return crossBoxResponse;
					}
				}
				
				if (crossBox.getTransferThreeName() != null && (!crossBox.getTransferThreeName().equals(""))) {
					name = crossBox.getTransferThreeName();
					if(crossBox.getTransferThreeId() == null){
						crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
						crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
						return crossBoxResponse;
					}
					id = crossBox.getTransferThreeId();
					result = dmsNameVertify(id, name);
					if (result == -1) {
						crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
						crossBoxResponse.setMessage("分拣中心[" + name + "]不存在");
						return crossBoxResponse;
					}
				}

				// 路线校验
				String oldFullLine = checkLineExist(crossBox);
				if (StringUtils.isNotBlank(oldFullLine)) {
					crossBoxResponse.setCode(CrossBoxResponse.CODE_WARN);
					crossBoxResponse.setMessage("已存在'" + oldFullLine + "'的路由，是否更新?");
					return crossBoxResponse;
				}
			}
			crossBoxResponse.setCode(CrossBoxResponse.CODE_NORMAL);
		} catch (Exception e) {
			crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
			crossBoxResponse.setMessage("检查是否存在异常，请检查数据是否选择正确");
			log.error("执行跨分拣箱号中转检查是否存在操作异常：", e);
		}
		return crossBoxResponse;
	}

	private int dmsNameVertify(int id, String name) {
		BaseStaffSiteOrgDto result = baseSiteManager.getBaseSiteBySiteId(id);
		if (result == null) {
			return -1;
		}
		String dmsShortName = result.getDmsShortName();
		String dmsFullName = result.getSiteName();
		if (!(name.equals(dmsFullName) || name.equals(dmsShortName))) {
			return -1;
		}
		return 1;
	}

	private String checkLineExist(CrossBox crossDmsBox) {
		if (crossDmsBox.getOriginalDmsId() != null && crossDmsBox.getDestinationDmsId() != null) {
			String fullLine = crossBoxService.checkLineExist(crossDmsBox);
			return fullLine;
		} else {
			return null;
		}
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@ResponseBody
	@RequestMapping("/doAdd")
	public CrossBoxResponse<String> doAdd(CrossBox crossBox, Model model) {
		CrossBoxResponse<String> crossBoxResponse = new CrossBoxResponse<String>();
		try {
			String userAccount = "demo";
			String userName = "demo";
			ErpUser erpUser = ErpUserClient.getCurrUser();

			if (erpUser != null) {
				userAccount = erpUser.getUserCode();
				userName = erpUser.getUserName();
			}

			// 组合完整路线
			getFullLine(crossBox);
			crossBox.setYn(1);
			crossBox.setUpdateOperatorName(userName);
			crossBox.setCreateOperatorName(userName);
			crossBox.setCreateTime(new Date());
			crossBox.setUpdateTime(new Date());
			crossBoxService.addCrossBox(crossBox);
			log.info("用户帐号【{}】，姓名【{}】执行添加跨分拣中转操作",userAccount,userName);
			crossBoxResponse.setCode(CrossBoxResponse.CODE_NORMAL);
		} catch (Exception e) {
			crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
			crossBoxResponse.setMessage("添加时异常，请检查数据是否选择正确");
			log.error("执行跨分拣箱号中转添加操作异常：", e);
		}
		return crossBoxResponse;
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@ResponseBody
	@RequestMapping("/doUpdate")
	public CrossBoxResponse<String> doUpdate(CrossBox crossDmsBox, Model model) {
		CrossBoxResponse<String> crossBoxResponse = new CrossBoxResponse<String>();
		try {
			String userAccount = "demo";
			String userName = "demo";
			ErpUser erpUser = ErpUserClient.getCurrUser();
			if (erpUser != null) {
				userAccount = erpUser.getUserCode();
				userName = erpUser.getUserName();
			}

			getFullLine(crossDmsBox);
			crossDmsBox.setYn(1);
			crossDmsBox.setUpdateOperatorName(userName);
			crossDmsBox.setUpdateTime(new Date());
			crossBoxService.updateCrossBoxByDms(crossDmsBox);
			log.info("用户帐号【{}】，姓名【{}】执行修改跨箱号中转操作",userAccount,userName);

			crossBoxResponse.setCode(CrossBoxResponse.CODE_NORMAL);
		} catch (Exception e) {
			crossBoxResponse.setCode(CrossBoxResponse.CODE_EXCEPTION);
			crossBoxResponse.setMessage("添加时异常，请检查数据是否选择正确");
			log.error("执行跨箱号中转添加操作异常：", e);
		}
		return crossBoxResponse;
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping("/delete")
	public String delete(Integer id) {
		try {
			String userAccount = "demo";
			String userName = "demo";
			ErpUser erpUser = ErpUserClient.getCurrUser();

			if (erpUser != null) {
				userAccount = erpUser.getUserCode();
				userName = erpUser.getUserName();
			}
			if (id != null) {
				CrossBox crossDmsBox = new CrossBox();
				crossDmsBox.setId(id);
				crossDmsBox.setUpdateOperatorName(userName);
				crossDmsBox.setRemark(userAccount + "删除");
				crossDmsBox.setUpdateTime(new Date());
				crossBoxService.deleteById(crossDmsBox);
			}
		} catch (Exception e) {
			log.error("删除时异常：", e);
		}
		return "redirect:index";
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping("/toImport")
	public String toImport(Model model) {
		return "crossbox/import_data";
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
	public String uploadExcel(Model model, MultipartHttpServletRequest request) {
		log.debug("uploadExcelFile begin...");
		try {
			String userName = "demo";
			ErpUser erpUser = ErpUserClient.getCurrUser();
			if (erpUser != null) {
				userName = erpUser.getUserName();
			}
			MultipartFile file = request.getFile("pitchExcel");
			String fileName = file.getOriginalFilename();

			String errorString = null;
			int type = 0;
			if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
				type = 1;
			} else {
				type = 2;
			}
			DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(type);
			List<CrossBox> dataList = null;
			try {
				dataList = dataResolver.resolver(file.getInputStream(), CrossBox.class, new PropertiesMetaDataFactory("/excel/crossBox.properties"));
				if (dataList != null && dataList.size() > 0) {
					if (dataList.size() > 1000) {
						errorString = "导入数据超出1000条";
						model.addAttribute("excelFile", errorString);
						return "crossbox/import_data";
					}
					for (CrossBox crossDmsBox : dataList) {
						CrossBoxResponse<CrossBox> crossBoxData = verifyFixCrossDmsBox(crossDmsBox, erpUser);
						if (crossBoxData.getCode() == CrossBoxResponse.CODE_WARN) {
							errorString = crossBoxData.getMessage();
							model.addAttribute("excelFile", errorString);
							return "crossbox/import_data";
						}
					}
					errorString = crossBoxService.handleCrossBoxData(dataList, userName);
					if (errorString == null || errorString.equals("")) {
						model.addAttribute("excelFile", "导入成功！");
					} else {
						model.addAttribute("excelFile", errorString);
						return "crossbox/import_data";
					}
				} else {
					errorString = "导入数据过多或者异常，请检查excel数据";
					model.addAttribute("excelFile", errorString);
					return "crossbox/import_data";
				}

			} catch (Exception e) {
				if (e instanceof IllegalArgumentException) {
					errorString = e.getMessage();
				} else {
					log.error("导入异常信息：", e);
					errorString = "导入出现异常";
				}
				model.addAttribute("excelFile", errorString);
				return "crossbox/import_data";
			}
		} catch (Exception e) {
			log.error("执行uploadExcelFile异常", e);
			throw new RuntimeException(e.getMessage());
		}
		return "crossbox/import_data";
	}

	private CrossBoxResponse<CrossBox> verifyFixCrossDmsBox(CrossBox crossBox, ErpUser erpUser) {
		String userName = "demo";
		erpUser = ErpUserClient.getCurrUser();
		if (erpUser != null) {
			userName = erpUser.getUserName();
		}

		CrossBoxResponse<CrossBox> resData = new CrossBoxResponse<CrossBox>();
		resData.setCode(CrossBoxResponse.CODE_NORMAL);
		crossBox.setUpdateOperatorName(userName);
		crossBox.setCreateOperatorName(userName);
		String errorString = null;
		
		int originDmsId = crossBox.getOriginalDmsId();
		String originalDmsName = getDmsName(originDmsId);
		if (originDmsId >= 0 && StringUtils.isNotBlank(originalDmsName)) {
			crossBox.setOriginalDmsName(originalDmsName);
		} else {
			resData.setCode(CrossBoxResponse.CODE_WARN);
			errorString = "始发分拣中心_id[" + originDmsId + "]不存在";
			resData.setMessage(errorString);
			return resData;
		}

		int destDmsId = crossBox.getDestinationDmsId();
		String destinationDmsName = getDmsName(destDmsId);
		if (destDmsId >= 0 && StringUtils.isNotBlank(destinationDmsName)) {
			crossBox.setDestinationDmsName(destinationDmsName);
		} else {
			resData.setCode(CrossBoxResponse.CODE_WARN);
			errorString = "目的分拣中心 id[" + destDmsId + "]不存在";
			resData.setMessage(errorString);
			return resData;
		}

		int transferOneId = crossBox.getTransferOneId();
		String transferOneName = getDmsName(transferOneId);
		if (transferOneId >= 0 && StringUtils.isNotBlank(transferOneName)) {
			crossBox.setTransferOneName(transferOneName);
		} else {
			resData.setCode(CrossBoxResponse.CODE_WARN);
			errorString = "中转1_id[" + transferOneId + "]不存在";
			resData.setMessage(errorString);
			return resData;
		}

		if (null != crossBox.getTransferTwoId() && crossBox.getTransferTwoId() >= 0) {
			if(crossBox.getTransferTwoId() == 0){
				crossBox.setTransferTwoId(null);
				crossBox.setTransferTwoName(null);
			}else {
				String transferTwoName = getDmsName(crossBox.getTransferTwoId());
				if (null != transferTwoName) {
					crossBox.setTransferTwoName(transferTwoName);
				} else {
					resData.setCode(CrossBoxResponse.CODE_WARN);
					errorString = "中转2_id[" + crossBox.getTransferTwoId() + "]不存在";
					resData.setMessage(errorString);
					return resData;
				}
			}

		}

		if (null != crossBox.getTransferThreeId() && crossBox.getTransferThreeId() >= 0) {
			if(crossBox.getTransferThreeId() == 0){
				crossBox.setTransferThreeId(null);
				crossBox.setTransferThreeName(null);
			}else {
				String transferThreeName = getDmsName(crossBox.getTransferThreeId());
				if (null != transferThreeName) {
					crossBox.setTransferThreeName(transferThreeName);
				} else {
					resData.setCode(CrossBoxResponse.CODE_WARN);
					errorString = "中转3_id[" + crossBox.getTransferThreeId() + "]不存在";
					resData.setMessage(errorString);
					return resData;
				}
			}
		}

		if (originDmsId == destDmsId) {
			resData.setCode(CrossBoxResponse.CODE_WARN);
			errorString = "始发分拣中与目的分拣中心不能重复";
			resData.setMessage(errorString);
			return resData;
		}

		String fullLine = getFullLine(crossBox);
		crossBox.setFullLine(fullLine);
		resData.setData(crossBox);
		return resData;
	}

    @Authorization(Constants.DMS_WEB_SORTING_CROSSBOX_R)
	@RequestMapping(value = "/toExport")
	public ModelAndView toExport(CrossBoxRequest crossBoxRequest, Model model) {
		try {
			if (crossBoxRequest != null) {
				if (StringUtils.isNotBlank(crossBoxRequest.getCreateDateString())) {
					Date createdate = DateHelper.parseDate(crossBoxRequest.getCreateDateString());
					crossBoxRequest.setCreateDate(createdate);
				}
				if (StringUtils.isNotBlank(crossBoxRequest.getUpdateDateString())) {
					Date updatedate = DateHelper.parseDate(crossBoxRequest.getUpdateDateString());
					crossBoxRequest.setUpdateDate(updatedate);
				}
			}
			List<List<Object>> resultList = crossBoxService.getExportDataByCrossBox(crossBoxRequest);

			model.addAttribute("filename", "crossBoxExport.xls");
			model.addAttribute("sheetname", "跨箱号中转维护导出结果");
			model.addAttribute("contents", resultList);

			return new ModelAndView(new DefaultExcelView(), model.asMap());

		} catch (Exception e) {
			log.error("toExport:", e);
			return null;
		}
	}

	private String getFullLine(CrossBox crossDmsBox) {
		StringBuffer fullLineBuffer = new StringBuffer();
		String originalDms = null;
		String destinationDms = null;
		String transfer1 = null;
		String transfer2 = null;
		String transfer3 = null;
		if (crossDmsBox.getOriginalDmsId() != null) {
			originalDms = getDmsNameForLine(crossDmsBox.getOriginalDmsId());
		}
		if (crossDmsBox.getDestinationDmsId() != null) {
			destinationDms = getDmsNameForLine(crossDmsBox.getDestinationDmsId());
		}
		if (crossDmsBox.getTransferOneId() != null) {
			transfer1 = getDmsNameForLine(crossDmsBox.getTransferOneId());
		}
		if (crossDmsBox.getTransferTwoId() != null) {
			transfer2 = getDmsNameForLine(crossDmsBox.getTransferTwoId());
		}
		if (crossDmsBox.getTransferThreeId() != null) {
			transfer3 = getDmsNameForLine(crossDmsBox.getTransferThreeId());
		}
		if (StringUtils.isNotBlank(originalDms)) {
			fullLineBuffer.append(originalDms);
		}
		if (StringUtils.isNotBlank(transfer1)) {
			fullLineBuffer.append(transfer1);
		}
		if (StringUtils.isNotBlank(transfer2)) {
			fullLineBuffer.append(transfer2);
		}
		if (StringUtils.isNotBlank(transfer3)) {
			fullLineBuffer.append(transfer3);
		}
		if (StringUtils.isNotBlank(destinationDms)) {
			fullLineBuffer.append(destinationDms);
		}
		String temp = fullLineBuffer.toString();
		String fullline = temp.substring(0, temp.length() - 2);
		crossDmsBox.setFullLine(fullline);
		return fullline;
	}

	private String getDmsName(Integer dmsCode) {
		StringBuffer sf = null;
		if (dmsCode != null) {
			sf = new StringBuffer();
			BaseStaffSiteOrgDto result = baseSiteManager.getBaseSiteBySiteId(dmsCode);
			if (result != null) {
				sf.append(result.getSiteName());
			}
			return sf.toString();
		} else {
			return null;
		}
	}

	private String getDmsNameForLine(Integer dmsCode) {
		StringBuffer sf = null;
		if (dmsCode != null) {
			sf = new StringBuffer();
			BaseStaffSiteOrgDto result = baseSiteManager.getBaseSiteBySiteId(dmsCode);
			if (result != null) {
				if (StringUtils.isNotBlank(result.getDmsShortName())) {
					sf.append(result.getDmsShortName());
				} else {
					sf.append(result.getSiteName());
				}
				sf.append("--");
			}
			return sf.toString();
		} else {
			return null;
		}
	}

	protected <T> T newObject(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			log.error("实例化{}错误",cls.getSimpleName(), e);
			throw new IllegalArgumentException("实例化" + cls.getSimpleName() + "错误", e);
		} catch (IllegalAccessException e) {
			log.error("不合法的访问{}错误",cls.getSimpleName(), e);
			throw new IllegalArgumentException("实例化" + cls.getSimpleName() + "错误", e);
		}
	}
}
