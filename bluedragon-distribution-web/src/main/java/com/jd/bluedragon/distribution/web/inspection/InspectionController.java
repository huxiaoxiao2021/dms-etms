package com.jd.bluedragon.distribution.web.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.impl.InspectionExceptionServiceImpl;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.JsonResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/partnerInspection")
public class InspectionController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	InspectionExceptionService inspectionExceptionService;
	
	@Autowired
	BaseService baseService;
	
	
	/**
	 * 将对象转换为Map
	 * 
	 * @param obj
	 * @return
	 */
	private Map<String, Object> makeObject2Map(Object obj) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (obj == null) {
			log.warn("转换对象为空");
			return paramMap;
		}
		try {
			Class objClass = obj.getClass();
			Method[] methods = objClass.getDeclaredMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					Object targetValueObj = method.invoke(obj);
					if (targetValueObj != null) {
						if (targetValueObj instanceof Integer) {
							if ((Integer) targetValueObj < 0) {
								log.warn("方法名：{} 的值小于0", methodName);
								continue;
							}
						} else if (targetValueObj instanceof String 
								&& "".equals(targetValueObj.toString())) {
							//if ("".equals(targetValueObj.toString())) {
								log.warn("方法名：{} 的值为空", methodName);
								continue;
							//}
						}
						paramMap.put(methodName.substring(3, 4).toLowerCase()
								+ methodName.substring(4), targetValueObj);
					}
				}
			}
		} catch (Exception e) {
			log.error("将对象转换为Map异常：", e);
		}
		return paramMap;
	}

	@Authorization(Constants.DMS_WEB_SORTING_PARTNERINSPECTION_R)
	@RequestMapping(value = "/goPartnerDifferentList", method = RequestMethod.GET)
	public String goPartnerDifferentList(Model model){
		initBaseData(model);
		return "inspection/partnerDispose";
	}
	
	private void initBaseData(Model model) {
		//验证登录信息
		//if(null==erpUser || erpUser.getUserId()<0)	return "";//获取用户为空
		Map<Integer,String> sortingCenters = new HashMap<Integer,String>();
		try {
			ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
			sortingCenters = baseService.getSiteInfoByBaseStaffId(erpUser.getUserId());
		} catch (Exception e) {
            log.error("根据用户获取分拣中心异常：", e);
		}
		model.addAttribute("sortingCenters", sortingCenters);
	}

    @Authorization(Constants.DMS_WEB_SORTING_PARTNERINSPECTION_R)
	@RequestMapping(value = "/partnerDifferentList", method = RequestMethod.GET)
	public String partnerDifferentList(InspectionEC inspectionEC, Pager pager, Model model){
		log.debug("按条件查询规则页面");

		List<InspectionEC> inspectionECs = null;
		try {
			Map<String, Object> paramMap = makeObject2Map(inspectionEC);
	
			// 设置分页对象
			if (pager == null) {
				pager = new Pager(Pager.DEFAULT_PAGE_NO);
			} else {
				pager = new Pager(pager.getPageNo(), pager.getPageSize());
			}
			paramMap.putAll(makeObject2Map(pager));
	
			// 获取总数量
			int totalsize = inspectionExceptionService.totalThirdByParams(paramMap);
			pager.setTotalSize(totalsize);
			log.info("查询符合条件的规则数量：{}", totalsize);
			inspectionECs = inspectionExceptionService.queryThirdByParams(paramMap);
			
			initBaseData(model);
		} catch (Exception e) {
			log.error("根据条件查询规则数据异常：", e);
		}
		model.addAttribute("inspectionECs", inspectionECs);
		model.addAttribute("inspectionDto", inspectionEC);
		model.addAttribute("pager", pager);

		return "inspection/partnerDispose";
	}

    @Authorization(Constants.DMS_WEB_SORTING_PARTNERINSPECTION_R)
	@RequestMapping(value="/disposeDifferent",method=RequestMethod.POST)
	@ResponseBody
	public JsonResult disposeDifferent(Long checkId){
		
		
		InspectionEC inspectionEC = inspectionExceptionService.get(checkId);
		
		if(null==inspectionEC)	return new JsonResult(false,"传入参数有误，请稍后再试");//不存在
		if(inspectionEC.getStatus()>InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED){
			return new JsonResult(false,"已经处理，不能再次处理");//已经处理，不能再次处理
		}
		if( InspectionEC.INSPECTIONEC_TYPE_NORMAL==inspectionEC.getInspectionECType() ){
			return new JsonResult(false,"非异常记录，不能再次处理");//正常记录，不能处理
		}
		
		int operationType = 0;
		switch(inspectionEC.getInspectionECType()){
		case InspectionEC.INSPECTIONEC_TYPE_MORE:
			operationType = InspectionEC.INSPECTIONEC_TYPE_SEND_BACK;
			break;
		case InspectionEC.INSPECTIONEC_TYPE_LESS:
			operationType = InspectionEC.INSPECTIONEC_TYPE_CANCEL;
			break;
		default:
			operationType= 0;
		}
		List<InspectionEC> inspectionECs = new ArrayList<InspectionEC>();
		inspectionECs.add(inspectionEC);
		int result = inspectionExceptionService.exceptionCancel(inspectionECs,operationType);
		if(result>0){
			return new JsonResult(true,"ok");
		}else if(InspectionExceptionServiceImpl.WAYBILL_CANCEL==result){
			return new JsonResult(false,"运单下所有包裹已经取消,并收回已验货包裹");
		}else if(InspectionExceptionServiceImpl.PACKAGE_SENDED==result){
			return new JsonResult(false,"包裹或者运单已经发货，不能执行当前操作");
		}else{
			return new JsonResult(false,"请稍后再试");
		}
	}
}
