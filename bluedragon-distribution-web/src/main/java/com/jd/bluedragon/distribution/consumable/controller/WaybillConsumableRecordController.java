package com.jd.bluedragon.distribution.consumable.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableExportCol;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ExportExcel;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRecordController
 * @Description: 运单耗材记录表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/waybillConsumableRecord")
public class WaybillConsumableRecordController extends DmsBaseController{

	private static final Logger log = LoggerFactory.getLogger(WaybillConsumableRecordController.class);

	@Autowired
	private WaybillConsumableRecordService waybillConsumableRecordService;

	@Autowired
	private WaybillConsumableRelationService waybillConsumableRelationService;


	@Autowired
	private BaseMajorManager baseMajorManager;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/waybillConsumableRecord";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<WaybillConsumableRecord> detail(@PathVariable("id") Long id) {
		JdResponse<WaybillConsumableRecord> rest = new JdResponse<WaybillConsumableRecord>();
		rest.setData(waybillConsumableRecordService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param waybillConsumableRecord
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody WaybillConsumableRecord waybillConsumableRecord) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(waybillConsumableRecordService.saveOrUpdate(waybillConsumableRecord));
	} catch (Exception e) {
            log.error("B网耗材保存失败：",e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 校验该运单是否支持修改
	 * @param waybillConsumableRecordCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/check/canModify")
	public @ResponseBody JdResponse<Boolean> canModify(@RequestBody WaybillConsumableRecordCondition waybillConsumableRecordCondition) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(waybillConsumableRecordService.canModify(waybillConsumableRecordCondition.getWaybillCode()));
		} catch (Exception e) {
			log.error("校验该运单是否支持修改异常：",e);
			rest.toError("校验失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(waybillConsumableRecordService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id确认多条数据
	 * @param records
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/confirmByIds")
	public @ResponseBody JdResponse<Integer> confirmByIds(@RequestBody List<WaybillConsumableRecord> records) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
            LoginUser loginUser = getLoginUser();
            List<WaybillConsumableRecord> confirmRecords = new ArrayList<WaybillConsumableRecord>(records.size());
			StringBuilder noPackUserErpWaybillBuilder = new StringBuilder();
            for (WaybillConsumableRecord data : records){
                WaybillConsumableRecord record = new WaybillConsumableRecord();
                String waybillCode = data.getWaybillCode();
                int count = waybillConsumableRelationService.getNoPackUserErpRecordCount(waybillCode);
                if (count > 0) {
                	log.warn("{}相关耗材未完成录入打包人，无法确认！",waybillCode);
                	if (noPackUserErpWaybillBuilder.length() == 0) {
						noPackUserErpWaybillBuilder.append(waybillCode);
					} else {
						noPackUserErpWaybillBuilder.append(",").append(waybillCode);
					}
                	continue;
				}
                record.setId(data.getId());
                record.setConfirmUserName(loginUser.getUserName());
                record.setConfirmUserErp(loginUser.getUserErp());
                record.setConfirmTime(new Date());
                confirmRecords.add(record);
            }
			int successCount = waybillConsumableRecordService.confirmByIds(confirmRecords);
            if (noPackUserErpWaybillBuilder.length() > 0) {
            	rest.setCode(JdResponse.CODE_FAIL);
				rest.setMessage("选中的运单部分未操作成功，未确认成功原因【运单未完成录入打包人ERP】，请录入后再进行确认！");
			} else {
				rest.setMessage("成功操作" + successCount + "条！");
			}

		} catch (IllegalArgumentException e) {
			log.error("B网耗材批量确认失败，参数异常：",e);
			rest.toError(e.getMessage());
		} catch (Exception e) {
			log.error("B网耗材批量确认失败：",e);
			rest.toError("确认失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param waybillConsumableRecordCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<WaybillConsumableRecord> listData(@RequestBody WaybillConsumableRecordCondition waybillConsumableRecordCondition) {
		JdResponse<PagerResult<WaybillConsumableRecord>> rest = new JdResponse<PagerResult<WaybillConsumableRecord>>();
		try {
/*			Integer siteCode = getLoginUser().getSiteCode();
			//如果站点类型非分拣中心，尝试取所属分拣中心
			if (! Constants.BASE_SITE_DISTRIBUTION_CENTER.equals(getLoginUser().getSiteType())) {
				BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteCode);
				if (baseStaffSiteOrgDto != null && baseStaffSiteOrgDto.getDmsId() != null) {
					siteCode = baseStaffSiteOrgDto.getDmsId();
				} else {
					log.warn("调用getBaseSiteInfoBySiteId获取站点所属分拣中心信息为空，siteCode:{}", getLoginUser().getSiteCode());
				}
			}
			waybillConsumableRecordCondition.setDmsId(siteCode);*/
			rest.setData(waybillConsumableRecordService.queryByPagerCondition(waybillConsumableRecordCondition));

		} catch (Exception e) {
			//log.error("调用getBaseSiteInfoBySiteId获取站点所属分拣中心信息异常，siteCode:{}", getLoginUser().getSiteCode(),e);
			log.error("查询包装耗材的信息失败");
		}

		return rest.getData();
	}

    /**
     * 导出
     * @param condition
     * @param response
     * @return
     */
	@Authorization(Constants.DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R)
	@RequestMapping(value = "/export")
	public @ResponseBody JdResponse<Boolean> exportByCondition(WaybillConsumableRecordCondition condition,  HttpServletResponse response) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        if (StringUtils.isEmpty(condition.getStartTimeStr()) || StringUtils.isEmpty(condition.getEndTimeStr()) ) {
            rest.toFail("导出参数未指定时间范围！");
            return rest;
        }else{
            /*设置时间*/
            condition.setStartTime(DateHelper.parseDateTime(condition.getStartTimeStr()));
            condition.setEndTime(DateHelper.parseDateTime(condition.getEndTimeStr()));
        }
        condition.setDmsId(getLoginUser().getSiteCode());
        int count = waybillConsumableRecordService.exportCountByWebCondition(condition);
        if(count == 0){
            rest.toFail("所选日期内没有可导出的数据！");
            return rest;
        }
        if(count > WaybillConsumableRecordService.MAX_ROWS){
            rest.toFail("导出数据量超过最大限制，请缩短导出时间段，最大限制：" + WaybillConsumableRecordService.MAX_ROWS);
            return rest;
        }
        List rows = waybillConsumableRecordService.exportInfoByWebCondition(condition);
        for(Object data : rows){
			WaybillConsumableExportDto dto = (WaybillConsumableExportDto)data;
        	dto.setReceiveTimeStr(DateHelper.formatDateTime(dto.getReceiveTime()));
        	dto.setConfirmTimeStr(DateHelper.formatDateTime(dto.getConfirmTime()));
		}
		try {
            ExportExcel.exportFile(response, WaybillConsumableExportCol.FILENAME,
                    WaybillConsumableExportCol.PROPERTYS,
                    WaybillConsumableExportCol.TITLES,
                    rows, ExportExcel.EXPORT_TYPE_EXCEL);
        }catch (Exception e){
            log.error("B网耗材明细导出失败：,导出条件：{}", JsonHelper.toJson(condition) , e);
            rest.toFail("导出失败，服务异常！");
            return rest;
        }
        return null;
	}
}
