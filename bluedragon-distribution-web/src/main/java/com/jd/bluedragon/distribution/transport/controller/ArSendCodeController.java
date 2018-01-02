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

import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.domain.ArSendCodeCondition;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: ArSendCodeController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("transport/arSendCode")
public class ArSendCodeController {
	
	private static final Log logger = LogFactory.getLog(ArSendCodeController.class);
	
	@Autowired
	ArSendCodeService arSendCodeService;
	
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	return "/transport/arSendCode";
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArSendCode> detail(@PathVariable("id") Long id) {
    	JdResponse<ArSendCode> rest = new JdResponse<ArSendCode>();
    	rest.setData(arSendCodeService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @param ArSendCode
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(@RequestBody ArSendCode arSendCode) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
    	try {
			rest.setData(arSendCodeService.saveOrUpdate(arSendCode));
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
			rest.setData(arSendCodeService.deleteByIds(ids));
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
    public @ResponseBody PagerResult<ArSendCode> listData(@RequestBody ArSendCodeCondition arSendCodeCondition) {
    	JdResponse<PagerResult<ArSendCode>> rest = new JdResponse<PagerResult<ArSendCode>>();
    	rest.setData(arSendCodeService.queryByPagerCondition(arSendCodeCondition));
    	return rest.getData();
    }
}