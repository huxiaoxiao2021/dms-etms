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

import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpaceCondition;
import com.jd.bluedragon.distribution.transport.service.ArBookingSpaceService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * 
 * @ClassName: ArBookingSpaceController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("transport/arBookingSpace")
public class ArBookingSpaceController {
	
	private static final Log logger = LogFactory.getLog(ArBookingSpaceController.class);
	
	@Autowired
	ArBookingSpaceService arBookingSpaceService;
	
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
    	return "/transport/arBookingSpace";
    }
    /**
     * 根据id获取实体基本信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody JdResponse<ArBookingSpace> detail(@PathVariable("id") Long id) {
    	JdResponse<ArBookingSpace> rest = new JdResponse<ArBookingSpace>();
    	rest.setData(arBookingSpaceService.findById(id));
    	return rest;
    }
    /**
     * 保存数据
     * @param ArBookingSpace
     * @return
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JdResponse<Boolean> save(@RequestBody ArBookingSpace arBookingSpace) {
    	JdResponse<Boolean> rest = new JdResponse<Boolean>();
    	try {
			rest.setData(arBookingSpaceService.saveOrUpdate(arBookingSpace));
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
			rest.setData(arBookingSpaceService.deleteByIds(ids));
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
    public @ResponseBody PagerResult<ArBookingSpace> listData(@RequestBody ArBookingSpaceCondition arBookingSpaceCondition) {
    	JdResponse<PagerResult<ArBookingSpace>> rest = new JdResponse<PagerResult<ArBookingSpace>>();
    	rest.setData(arBookingSpaceService.queryByPagerCondition(arBookingSpaceCondition));
    	return rest.getData();
    }
}