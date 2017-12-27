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

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegisterCondition;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: ArSendRegisterController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("transport/arSendRegister")
public class ArSendRegisterController {
	
	private static final Log logger = LogFactory.getLog(ArSendRegisterController.class);
	
	@Autowired
	ArSendRegisterService arSendRegisterService;
	
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	return "/transport/arSendRegister";
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArSendRegister> detail(@PathVariable("id") Long id) {
    	JdResponse<ArSendRegister> rest = new JdResponse<ArSendRegister>();
    	rest.setData(arSendRegisterService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @param ArSendRegister
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(@RequestBody ArSendRegister arSendRegister) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
    	try {
			rest.setData(arSendRegisterService.saveOrUpdate(arSendRegister));
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
			rest.setData(arSendRegisterService.deleteByIds(ids));
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
    public @ResponseBody PagerResult<ArSendRegister> listData(@RequestBody ArSendRegisterCondition arSendRegisterCondition) {
    	JdResponse<PagerResult<ArSendRegister>> rest = new JdResponse<PagerResult<ArSendRegister>>();
    	rest.setData(arSendRegisterService.queryByPagerCondition(arSendRegisterCondition));
    	return rest.getData();
    }
}