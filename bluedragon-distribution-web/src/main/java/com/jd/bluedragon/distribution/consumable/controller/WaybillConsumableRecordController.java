package com.jd.bluedragon.distribution.consumable.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

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
public class WaybillConsumableRecordController {

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
			rest.setData(waybillConsumableRecordService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
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
		rest.setData(waybillConsumableRecordService.queryByPagerCondition(waybillConsumableRecordCondition));
		return rest.getData();
	}
}
