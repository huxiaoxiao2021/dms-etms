package com.jd.bluedragon.distribution.transport.controller;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.transport.domain.ArExcpRegister;
import com.jd.bluedragon.distribution.transport.domain.ArExcpRegisterCondition;
import com.jd.bluedragon.distribution.transport.service.ArExcpRegisterService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: ArExcpRegisterController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("transport/arExcpRegister")
public class ArExcpRegisterController {
	
	private static final Log logger = LogFactory.getLog(ArExcpRegisterController.class);
	
	@Autowired
	ArExcpRegisterService arExcpRegisterService;

	@Autowired
	BaseMajorManager baseMajorManager;

    @Autowired
    DmsBaseDictService dmsBaseDictService;
    /**
     * 根据id获取实体基本信息
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	return "/transport/arExcpRegister";
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArExcpRegister> detail(@PathVariable("id") Long id) {
    	JdResponse<ArExcpRegister> rest = new JdResponse<ArExcpRegister>();
		ArExcpRegister arExcpRegister=arExcpRegisterService.findById(id);
		convertDate(arExcpRegister,false);
		rest.setData(arExcpRegister);
    	return rest;
    }
    /**
     * 保存数据
     * @param arExcpRegister
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(ArExcpRegister arExcpRegister) {
		convertDate(arExcpRegister,true);

    	JdResponse<Boolean> rest = new JdResponse<Boolean>();

		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		String userCode = "";
		String userName = "";

		if(erpUser!=null){
			userCode = erpUser.getUserCode();
			userName = erpUser.getUserName();
		}

    	try {
			rest.setData(arExcpRegisterService.saveOrUpdate(arExcpRegister,userCode,userName));
		} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据id删除一条数据
     * @param ids
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
    	JdResponse<Integer> rest = new JdResponse<Integer>();
    	try {
			rest.setData(arExcpRegisterService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据条件分页查询数据信息
     * @param arExcpRegisterCondition
     * @return
     */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<ArExcpRegister> listData(@RequestBody ArExcpRegisterCondition arExcpRegisterCondition) {
		convertDate(arExcpRegisterCondition);

    	JdResponse<PagerResult<ArExcpRegister>> rest = new JdResponse<PagerResult<ArExcpRegister>>();

		//模糊字段 运力名称
		if(StringUtils.isNotBlank(arExcpRegisterCondition.getTransportName())){
			arExcpRegisterCondition.setTransportName("%"+arExcpRegisterCondition.getTransportName()+"%");
		}
		//模糊字段 航空单号
		if(StringUtils.isNotBlank(arExcpRegisterCondition.getOrderCode())){
			arExcpRegisterCondition.setOrderCode("%"+arExcpRegisterCondition.getOrderCode()+"%");
		}

    	rest.setData(arExcpRegisterService.queryByPagerCondition(arExcpRegisterCondition));
    	return rest.getData();
    }

	/**
	 * 根据条件分页查询数据信息
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
	@RequestMapping(value = "/airRailwayExceptionType")
	public @ResponseBody JdResponse<List<DmsBaseDict>> getAirRailwayExceptionType() {
		JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
		rest.setData(dmsBaseDictService.queryByParentIdAndTypeGroup(1, null));
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
	@RequestMapping(value = "/airRailwayExceptionReason/{typeGroup}")
	public @ResponseBody JdResponse<List<DmsBaseDict>> getAirRailwayExceptionReason(@PathVariable("typeGroup") Integer typeGroup) {
		JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
		rest.setData(dmsBaseDictService.queryByParentIdAndTypeGroup(2, typeGroup));
		return rest;
	}

	/**
	 * 根据条件分页查询数据信息
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TRANSPORT_AREXCPREGISTER_R)
	@RequestMapping(value = "/airRailwayExceptionResult/{typeGroup}")
	public @ResponseBody JdResponse<List<DmsBaseDict>> getAirRailwayExceptionResult(@PathVariable("typeGroup") Integer typeGroup) {
		JdResponse<List<DmsBaseDict>> rest = new JdResponse<List<DmsBaseDict>>();
		rest.setData(dmsBaseDictService.queryByParentIdAndTypeGroup(3, typeGroup));
		return rest;
	}

	private void convertDate(ArExcpRegister arExcpRegister,boolean str2Date){
    	if(str2Date){
			Date planStartTime = DateHelper.parseDate(arExcpRegister.getPlanStartTimeStr(),DateHelper.DATE_FORMAT_HHmmss);
			Date planEndTime = DateHelper.parseDate(arExcpRegister.getPlanEndTimeStr(),DateHelper.DATE_FORMAT_HHmmss);
			Date excpTime = DateHelper.parseDate(arExcpRegister.getExcpTimeStr(),DateHelper.DATE_FORMAT_YYYYMMDD);

			arExcpRegister.setPlanStartTime(planStartTime);
			arExcpRegister.setPlanEndTime(planEndTime);
			arExcpRegister.setExcpTime(excpTime);
		}else{
			String planStartTimeStr = DateHelper.formatDate(arExcpRegister.getPlanStartTime(),DateHelper.DATE_FORMAT_HHmmss);
			String planEndTimeStr = DateHelper.formatDate(arExcpRegister.getPlanEndTime(),DateHelper.DATE_FORMAT_HHmmss);
			String excpTimeStr = DateHelper.formatDate(arExcpRegister.getExcpTime(),DateHelper.DATE_FORMAT_YYYYMMDD);

			arExcpRegister.setPlanStartTimeStr(planStartTimeStr);
			arExcpRegister.setPlanEndTimeStr(planEndTimeStr);
			arExcpRegister.setExcpTimeStr(excpTimeStr);
		}

	}

	private void convertDate(ArExcpRegisterCondition arExcpRegisterCondition){
		Date excpTimeLE = DateHelper.parseDate(arExcpRegisterCondition.getExcpTimeLEStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		Date excpTimeGE = DateHelper.parseDate(arExcpRegisterCondition.getExcpTimeGEStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		arExcpRegisterCondition.setExcpTimeLE(excpTimeLE);
		arExcpRegisterCondition.setExcpTimeGE(excpTimeGE);
	}
}