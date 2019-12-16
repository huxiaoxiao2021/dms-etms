package com.jd.bluedragon.distribution.half.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.half.domain.PackageHalfApprove;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedeliveryCondition;
import com.jd.bluedragon.distribution.half.service.PackageHalfApproveService;
import com.jd.bluedragon.distribution.half.service.PackageHalfRedeliveryService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PackageHalfRedeliveryController
 * @Description: 包裹半收协商再投业务表--Controller实现
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
@Controller
@RequestMapping("half/packageHalfRedelivery")
public class PackageHalfRedeliveryController {

	private static final Logger log = LoggerFactory.getLogger(PackageHalfRedeliveryController.class);

	@Autowired
	private PackageHalfRedeliveryService packageHalfRedeliveryService;

	@Autowired
	private PackageHalfApproveService packageHalfApproveService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALFREDELIVERY_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/half/packageHalfRedelivery";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALFREDELIVERY_R)
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PackageHalfRedelivery> detail(@PathVariable("id") Long id) {
		JdResponse<PackageHalfRedelivery> rest = new JdResponse<PackageHalfRedelivery>();
		rest.setData(packageHalfRedeliveryService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param packageHalfRedelivery
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALFREDELIVERY_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackageHalfRedelivery packageHalfRedelivery) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(packageHalfRedeliveryService.saveOrUpdate(packageHalfRedelivery));
	} catch (Exception e) {
			log.error("fail to save！",e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALFREDELIVERY_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(packageHalfRedeliveryService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param packageHalfRedeliveryCondition
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_EXPRESS_PACKAGEHALFREDELIVERY_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackageHalfRedelivery> listData(@RequestBody PackageHalfRedeliveryCondition packageHalfRedeliveryCondition) {

        PagerResult<PackageHalfRedelivery> data = packageHalfRedeliveryService.queryByPagerCondition(packageHalfRedeliveryCondition);
        List<String> wayBillCodes = new ArrayList<String>();
        for(PackageHalfRedelivery vo : data.getRows()){
            if(StringUtils.isBlank(vo.getDmsSiteName()) || StringUtils.isBlank(vo.getCreateUser())){
                wayBillCodes.add(vo.getWaybillCode());
            }
        }
        if(!wayBillCodes.isEmpty()){
            List<PackageHalfApprove> approves = packageHalfApproveService.queryListByWaybillCode(wayBillCodes);
            Map<String, PackageHalfApprove> approveMap = new HashMap<String, PackageHalfApprove>();
            for (PackageHalfApprove vo : approves){
                approveMap.put(vo.getWaybillCode(), vo);
            }
            for(PackageHalfRedelivery vo : data.getRows()){
                PackageHalfApprove approve = approveMap.get(vo.getWaybillCode());
                if(approve != null){
                    vo.setDmsSiteCode(approve.getDmsSiteCode());
                    vo.setDmsSiteName(approve.getDmsSiteName());
                    vo.setCreateUser(approve.getCreateUser());
                    vo.setCreateUserName(approve.getCreateUserName());
                    vo.setCreateUserCode(approve.getCreateUserCode());
                }
            }
        }

		return data;
	}

}
