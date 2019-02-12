package com.jd.bluedragon.distribution.reverse.part.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetailCondition;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: ReversePartDetailController
 * @Description: 半退明细表--Controller实现
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
@Controller
@RequestMapping("reverse.part/reversePartDetail")
public class ReversePartDetailController {

	private static final Log logger = LogFactory.getLog(ReversePartDetailController.class);

	@Autowired
	private ReversePartDetailService reversePartDetailService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/reverse.part/reversePartDetail";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<ReversePartDetail> detail(@PathVariable("id") Long id) {
		JdResponse<ReversePartDetail> rest = new JdResponse<ReversePartDetail>();
		rest.setData(reversePartDetailService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param reversePartDetail
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody ReversePartDetail reversePartDetail) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(reversePartDetailService.saveOrUpdate(reversePartDetail));
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
			rest.setData(reversePartDetailService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param reversePartDetailCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<ReversePartDetail> listData(@RequestBody ReversePartDetailCondition reversePartDetailCondition) {
		JdResponse<PagerResult<ReversePartDetail>> rest = new JdResponse<PagerResult<ReversePartDetail>>();
		rest.setData(reversePartDetailService.queryByPagerCondition(reversePartDetailCondition));
		return rest.getData();
	}
}
