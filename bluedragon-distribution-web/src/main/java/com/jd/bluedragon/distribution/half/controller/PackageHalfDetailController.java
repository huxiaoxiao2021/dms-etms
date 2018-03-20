package com.jd.bluedragon.distribution.half.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetailCondition;
import com.jd.bluedragon.distribution.half.service.PackageHalfDetailService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: PackageHalfDetailController
 * @Description: 包裹半收操作明细表--Controller实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
@Controller
@RequestMapping("half/packageHalfDetail")
public class PackageHalfDetailController {

	private static final Log logger = LogFactory.getLog(PackageHalfDetailController.class);

	@Autowired
	private PackageHalfDetailService packageHalfDetailService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/half/packageHalfDetail";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PackageHalfDetail> detail(@PathVariable("id") Long id) {
		JdResponse<PackageHalfDetail> rest = new JdResponse<PackageHalfDetail>();
		rest.setData(packageHalfDetailService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param packageHalfDetail
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackageHalfDetail packageHalfDetail) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(packageHalfDetailService.saveOrUpdate(packageHalfDetail));
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
			rest.setData(packageHalfDetailService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param packageHalfDetailCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackageHalfDetail> listData(@RequestBody PackageHalfDetailCondition packageHalfDetailCondition) {
		JdResponse<PagerResult<PackageHalfDetail>> rest = new JdResponse<PagerResult<PackageHalfDetail>>();
		rest.setData(packageHalfDetailService.queryByPagerCondition(packageHalfDetailCondition));
		return rest.getData();
	}
}
