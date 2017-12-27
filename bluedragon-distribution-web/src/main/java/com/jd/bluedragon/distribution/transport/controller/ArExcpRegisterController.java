package com.jd.bluedragon.distribution.transport.controller;

import java.util.List;

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
	
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	return "/transport/arExcpRegister";
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArExcpRegister> detail(@PathVariable("id") Long id) {
    	JdResponse<ArExcpRegister> rest = new JdResponse<ArExcpRegister>();
    	rest.setData(arExcpRegisterService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @param ArExcpRegister
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(@RequestBody ArExcpRegister arExcpRegister) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
    	try {
			rest.setData(arExcpRegisterService.saveOrUpdate(arExcpRegister));
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
			rest.setData(arExcpRegisterService.deleteByIds(ids));
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
    public @ResponseBody PagerResult<ArExcpRegister> listData(@RequestBody ArExcpRegisterCondition arExcpRegisterCondition) {
    	JdResponse<PagerResult<ArExcpRegister>> rest = new JdResponse<PagerResult<ArExcpRegister>>();
    	rest.setData(arExcpRegisterService.queryByPagerCondition(arExcpRegisterCondition));
    	return rest.getData();
    }
}