package com.jd.bluedragon.distribution.consumable.controller;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableExportCol;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.utils.ExportExcel;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static final Log logger = LogFactory.getLog(WaybillConsumableRecordController.class);

	@Autowired
	private WaybillConsumableRecordService waybillConsumableRecordService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/waybillConsumableRecord";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
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
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody WaybillConsumableRecord waybillConsumableRecord) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(waybillConsumableRecordService.saveOrUpdate(waybillConsumableRecord));
	} catch (Exception e) {
            logger.error("B网耗材保存失败："+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 校验该运单是否支持修改
	 * @param waybillConsumableRecordCondition
	 * @return
	 */
	@RequestMapping(value = "/check/canModify")
	public @ResponseBody JdResponse<Boolean> canModify(@RequestBody WaybillConsumableRecordCondition waybillConsumableRecordCondition) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(waybillConsumableRecordService.canModify(waybillConsumableRecordCondition.getWaybillCode()));
		} catch (Exception e) {
			logger.error("校验该运单是否支持修改异常："+e.getMessage(),e);
			rest.toError("校验失败，服务异常！");
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
			rest.setData(waybillConsumableRecordService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id确认多条数据
	 * @param records
	 * @return
	 */
	@RequestMapping(value = "/confirmByIds")
	public @ResponseBody JdResponse<Integer> confirmByIds(@RequestBody List<WaybillConsumableRecord> records) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		if(records.size() > 500){

        }
		try {
            LoginUser loginUser = getLoginUser();
            List<WaybillConsumableRecord> confirmRecords = new ArrayList<WaybillConsumableRecord>(records.size());
            for (WaybillConsumableRecord data : records){
                WaybillConsumableRecord record = new WaybillConsumableRecord();
                record.setId(data.getId());
                record.setConfirmUserName(loginUser.getUserName());
                record.setConfirmUserErp(loginUser.getUserErp());
                record.setConfirmTime(new Date());
                confirmRecords.add(record);
            }
			rest.setData(waybillConsumableRecordService.confirmByIds(confirmRecords));
		} catch (IllegalArgumentException e) {
			logger.error("B网耗材批量确认失败，参数异常："+e.getMessage(),e);
			rest.toError(e.getMessage());
		} catch (Exception e) {
			logger.error("B网耗材批量确认失败："+e.getMessage(),e);
			rest.toError("确认失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param waybillConsumableRecordCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<WaybillConsumableRecord> listData(@RequestBody WaybillConsumableRecordCondition waybillConsumableRecordCondition) {
		JdResponse<PagerResult<WaybillConsumableRecord>> rest = new JdResponse<PagerResult<WaybillConsumableRecord>>();
		waybillConsumableRecordCondition.setDmsId(getLoginUser().getSiteCode());
		rest.setData(waybillConsumableRecordService.queryByPagerCondition(waybillConsumableRecordCondition));
		return rest.getData();
	}

    /**
     * 导出
     * @param condition
     * @param response
     * @return
     */
	@RequestMapping(value = "/export")
	public @ResponseBody JdResponse<Boolean> exportByCondition(@RequestBody WaybillConsumableRecordCondition condition,  HttpServletResponse response) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        if (condition.getStartTime() == null || condition.getEndTime() == null) {
            rest.toFail("导出参数未指定时间范围！");
            return rest;
        }
        condition.setDmsId(getLoginUser().getSiteCode());
        int count = waybillConsumableRecordService.exportCountByWebCondition(condition);
        if(count > WaybillConsumableRecordService.MAX_ROWS){
            rest.toFail("导出数据量超过最大限制，请缩短导出时间段，最大限制：" + WaybillConsumableRecordService.MAX_ROWS);
            return rest;
        }
        List rows = waybillConsumableRecordService.exportInfoByWebCondition(condition);
		try {
            ExportExcel.exportFile(response, WaybillConsumableExportCol.FILENAME,
                    WaybillConsumableExportCol.PROPERTYS,
                    WaybillConsumableExportCol.TITLES,
                    rows, ExportExcel.EXPORT_TYPE_EXCEL);
        }catch (Exception e){
            logger.error("B网耗材明细导出失败：,导出条件：" + JsonHelper.toJson(condition) , e);
            rest.toFail("导出失败，服务异常！");
            return rest;
        }
        return null;
	}
}
