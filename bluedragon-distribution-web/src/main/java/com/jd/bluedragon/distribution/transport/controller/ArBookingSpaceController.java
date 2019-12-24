package com.jd.bluedragon.distribution.transport.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpaceCondition;
import com.jd.bluedragon.distribution.transport.service.ArBookingSpaceService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

/**
 * 
 * @ClassName: ArBookingSpaceController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("transport/arBookingSpace")
public class ArBookingSpaceController {
	
	private static final Logger log = LoggerFactory.getLogger(ArBookingSpaceController.class);
	
	@Autowired
	ArBookingSpaceService arBookingSpaceService;

	@Autowired
	BaseMajorManager baseMajorManager;
    /**
     * 根据id获取实体基本信息
     * @ id
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
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

    	return "/transport/arBookingSpace";
    }


    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArBookingSpace> detail(@PathVariable("id") Long id) {
    	JdResponse<ArBookingSpace> rest = new JdResponse<ArBookingSpace>();
		ArBookingSpace arBookingSpace = arBookingSpaceService.findById(id);
		convertDate(arBookingSpace,false);
    	rest.setData(arBookingSpace);
		return rest;
    }
    /**
     * 保存数据
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(ArBookingSpace arBookingSpace) {
    	convertDate(arBookingSpace,true);

    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		String userName = "";
		Long createSiteCode = new Long(-1);
		String createSiteName = "";

		if(erpUser!=null){
			 userCode = erpUser.getUserCode();
			 userName = erpUser.getUserName();
		}

		BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
		if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
			createSiteCode = new Long(bssod.getSiteCode());
			createSiteName = bssod.getSiteName();
		}

    	try {
			rest.setData(arBookingSpaceService.saveOrUpdate(arBookingSpace,userCode,userName,createSiteCode,createSiteName));
		} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("保存失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据id删除一条数据
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
    	JdResponse<Integer> rest = new JdResponse<Integer>();
    	try {
			rest.setData(arBookingSpaceService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据条件分页查询数据信息
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<ArBookingSpace> listData(@RequestBody ArBookingSpaceCondition arBookingSpaceCondition) {
    	JdResponse<PagerResult<ArBookingSpace>> rest = new JdResponse<PagerResult<ArBookingSpace>>();
    	convertDate(arBookingSpaceCondition);
    	//模糊字段 运力名称
		if(StringUtils.isNotBlank(arBookingSpaceCondition.getTransportName())){
			arBookingSpaceCondition.setTransportName("%"+arBookingSpaceCondition.getTransportName()+"%");
		}
    	rest.setData(arBookingSpaceService.queryByPagerCondition(arBookingSpaceCondition));
    	return rest.getData();
    }

	@Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
	@RequestMapping(value = "/toExport")
	public ModelAndView toExport(ArBookingSpaceCondition arBookingSpaceCondition, Model model) {
		try {
			convertDate(arBookingSpaceCondition);
			List<List<Object>> resultList = arBookingSpaceService.getExportData(arBookingSpaceCondition);

			model.addAttribute("filename", "arBookingSpaceExport.xls");
			model.addAttribute("sheetname", "订舱登记导出结果");
			model.addAttribute("contents", resultList);

			return new ModelAndView(new DefaultExcelView(), model.asMap());

		} catch (Exception e) {
			log.error("toExport:{}", e.getMessage(), e);
			return null;
		}
	}


	@Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
	@RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
	public @ResponseBody JdResponse uploadExcel( @RequestParam("importExcelFile") MultipartFile file) {
		log.debug("uploadExcelFile begin...");
		String errorString = "";
		try {

			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			String userCode = "";
			String userName = "";
			Long createSiteCode = new Long(-1);
			String createSiteName = "";

			if(erpUser!=null){
				userCode = erpUser.getUserCode();
				userName = erpUser.getUserName();
			}
			BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
			if (bssod!=null && bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
				createSiteCode = new Long(bssod.getSiteCode());
				createSiteName = bssod.getSiteName();
			}

			String fileName = file.getOriginalFilename();


			int type = 0;
			if (fileName.endsWith("xls") || fileName.endsWith("XLS")) {
				type = 1;
			} else if(fileName.endsWith("xlsx") || fileName.endsWith("XLSX")){
				type = 2;
			}
			DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(type);
			List<ArBookingSpace> dataList = null;

			dataList = dataResolver.resolver(file.getInputStream(), ArBookingSpace.class, new PropertiesMetaDataFactory("/excel/arBookingSpace.properties"));
			if (dataList != null && dataList.size() > 0) {
				if (dataList.size() > 1000) {
					errorString = "导入数据超出1000条";
					return new JdResponse(JdResponse.CODE_FAIL,errorString);
				}

				//批量插入数据
				arBookingSpaceService.importExcel(dataList,userCode,userName,createSiteCode,createSiteName);

			} else {
				errorString = "导入数据表格为空，请检查excel数据";
				return new JdResponse(JdResponse.CODE_FAIL,errorString);
			}

		} catch (Exception e) {
			if (e instanceof IllegalArgumentException) {
				errorString = e.getMessage();
			} else {
				log.error("导入异常信息：", e);
				errorString = "导入出现异常";
			}
			return new JdResponse(JdResponse.CODE_FAIL,errorString);
		}

		return new JdResponse();
	}

	private void convertDate(ArBookingSpaceCondition arBookingSpaceCondition){
		Date planStartDateLE = DateHelper.parseDate(arBookingSpaceCondition.getPlanStartDateLEStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		Date planStartDateGE = DateHelper.parseDate(arBookingSpaceCondition.getPlanStartDateGEStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		arBookingSpaceCondition.setPlanStartDateGE(planStartDateGE);
		arBookingSpaceCondition.setPlanStartDateLE(planStartDateLE);

	}

	private void convertDate(ArBookingSpace arBookingSpace,boolean str2Date){
    	if(str2Date){
			Date planStartDate = DateHelper.parseDate(arBookingSpace.getPlanStartDateStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
			Date bookingSpaceTime = DateHelper.parseDate(arBookingSpace.getBookingSpaceTimeStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
			Date planStartTime = DateHelper.parseDate(arBookingSpace.getPlanStartTimeStr(),DateHelper.DATE_FORMAT_HHmmss);
			Date planEndTime = DateHelper.parseDate(arBookingSpace.getPlanEndTimeStr(),DateHelper.DATE_FORMAT_HHmmss);

			arBookingSpace.setPlanStartDate(planStartDate);
			arBookingSpace.setBookingSpaceTime(bookingSpaceTime);
			arBookingSpace.setPlanStartTime(planStartTime);
			arBookingSpace.setPlanEndTime(planEndTime);
		}else{
			String planStartDateStr = DateHelper.formatDate(arBookingSpace.getPlanStartDate(),DateHelper.DATE_FORMAT_YYYYMMDD);
			String bookingSpaceTimeStr = DateHelper.formatDate(arBookingSpace.getBookingSpaceTime(),DateHelper.DATE_FORMAT_YYYYMMDD);
			String planStartTimeStr = DateHelper.formatDate(arBookingSpace.getPlanStartTime(),DateHelper.DATE_FORMAT_HHmmss);
			String planEndTimeStr = DateHelper.formatDate(arBookingSpace.getPlanEndTime(),DateHelper.DATE_FORMAT_HHmmss);

			arBookingSpace.setPlanStartDateStr(planStartDateStr);
			arBookingSpace.setBookingSpaceTimeStr(bookingSpaceTimeStr);
			arBookingSpace.setPlanStartTimeStr(planStartTimeStr);
			arBookingSpace.setPlanEndTimeStr(planEndTimeStr);
		}

	}
}