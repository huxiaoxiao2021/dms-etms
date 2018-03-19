package com.jd.bluedragon.distribution.abnormal.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: DmsOperateHintController
 * @Description: 运单操作提示--Controller实现
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
@Controller
@RequestMapping("abnormal/dmsOperateHint")
public class DmsOperateHintController {

	private static final Log logger = LogFactory.getLog(DmsOperateHintController.class);

	@Autowired
	private DmsOperateHintService dmsOperateHintService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/abnormal/dmsOperateHint";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsOperateHint> detail(@PathVariable("id") Long id) {
		JdResponse<DmsOperateHint> rest = new JdResponse<DmsOperateHint>();
		rest.setData(dmsOperateHintService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsOperateHint
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsOperateHint dmsOperateHint) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(dmsOperateHintService.saveOrUpdate(dmsOperateHint));
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
			rest.setData(dmsOperateHintService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsOperateHintCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsOperateHint> listData(@RequestBody DmsOperateHintCondition dmsOperateHintCondition) {
		JdResponse<PagerResult<DmsOperateHint>> rest = new JdResponse<PagerResult<DmsOperateHint>>();
		rest.setData(dmsOperateHintService.queryByPagerCondition(dmsOperateHintCondition));
		return rest.getData();
	}
}
