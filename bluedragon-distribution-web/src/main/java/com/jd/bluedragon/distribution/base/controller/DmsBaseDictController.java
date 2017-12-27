package com.jd.bluedragon.distribution.base.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDictCondition;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: DmsBaseDictController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("base/dmsBaseDict")
public class DmsBaseDictController {
	
	private static final Log logger = LogFactory.getLog(DmsBaseDictController.class);
	
	@Autowired
	DmsBaseDictService dmsBaseDictService;
	
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	String rest ="/infoSite/infoSiteList";
    	rest ="/base/dmsBaseDict";
    	return rest;
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<DmsBaseDict> detail(@PathVariable("id") Long id) {
    	JdResponse<DmsBaseDict> rest = new JdResponse<DmsBaseDict>();
    	rest.setData(dmsBaseDictService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @param DmsBaseDict
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsBaseDict dmsBaseDict) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
    	try {
			rest.setData(dmsBaseDictService.saveOrUpdate(dmsBaseDict));
		} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据id删除一条数据
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
    	JdResponse<Integer> rest = new JdResponse<Integer>();
    	try {
			rest.setData(dmsBaseDictService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
    	return rest;
    }
    /**
     * 根据条件分页查询数据信息
     * @param pagerCondition
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<DmsBaseDict> listData(@RequestBody DmsBaseDictCondition dmsBaseDictCondition) {
    	JdResponse<PagerResult<DmsBaseDict>> rest = new JdResponse<PagerResult<DmsBaseDict>>();
    	rest.setData(dmsBaseDictService.queryByPagerCondition(dmsBaseDictCondition));
    	return rest.getData();
    }
}