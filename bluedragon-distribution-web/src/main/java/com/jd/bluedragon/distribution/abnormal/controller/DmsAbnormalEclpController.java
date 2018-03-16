package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: DmsAbnormalEclpController
 * @Description: ECLP外呼申请表--Controller实现
 * @author wuyoude
 * @date 2018年03月14日 16:31:20
 *
 */
@Controller
@RequestMapping("abnormal/dmsAbnormalEclp")
public class DmsAbnormalEclpController {

	private static final Log logger = LogFactory.getLog(DmsAbnormalEclpController.class);

	@Autowired
	private DmsAbnormalEclpService dmsAbnormalEclpService;

    @Autowired
    private BaseMajorManager baseMajorManager;
	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/abnormal/dmsAbnormalEclp";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsAbnormalEclp> detail(@PathVariable("id") Long id) {
		JdResponse<DmsAbnormalEclp> rest = new JdResponse<DmsAbnormalEclp>();
		rest.setData(dmsAbnormalEclpService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsAbnormalEclp
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsAbnormalEclp dmsAbnormalEclp) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {

            if(SerialRuleUtil.isMatchCommonWaybillCode(dmsAbnormalEclp.getWaybillCode())){
                //1.一个月内该运单只能发起一次库房拒收外呼申请
                DmsAbnormalEclpCondition condition = new DmsAbnormalEclpCondition();
                condition.setWaybillCode(dmsAbnormalEclp.getWaybillCode());
                // 不限制该运单是否在进行外呼中，即，只能发起一次
                //condition.setIsReceipt(0);
                condition.setStartTime(DateHelper.add(new Date(), Calendar.MONTH, -2));
                PagerResult result = dmsAbnormalEclpService.queryByPagerCondition(condition);
                //判断当前运单是否有未进行完毕的外呼
                if(result.getTotal() > 0){
                    rest.toFail("运单有正在进行的外呼申请：" + dmsAbnormalEclp.getWaybillCode());
                }else{
                    LoginContext loginContext = LoginContext.getLoginContext();
                    BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
                    dmsAbnormalEclp.setCreateUser(loginContext.getPin());
                    dmsAbnormalEclp.setCreateUserCode(dto.getStaffNo());
                    dmsAbnormalEclp.setCreateUserName(loginContext.getNick());
                    dmsAbnormalEclp.setDmsSiteCode(dto.getSiteCode());
                    dmsAbnormalEclp.setDmsSiteName(dto.getSiteName());
                    rest.setData(dmsAbnormalEclpService.saveOrUpdate(dmsAbnormalEclp));
                }
            }else{
                rest.toFail("运单号非法：" + dmsAbnormalEclp.getWaybillCode());
            }
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
			rest.setData(dmsAbnormalEclpService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsAbnormalEclpCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsAbnormalEclp> listData(@RequestBody DmsAbnormalEclpCondition dmsAbnormalEclpCondition) {
		JdResponse<PagerResult<DmsAbnormalEclp>> rest = new JdResponse<PagerResult<DmsAbnormalEclp>>();
        //限制只能查自己分拣中心的
        LoginContext loginContext = LoginContext.getLoginContext();
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
        dmsAbnormalEclpCondition.setDmsSiteCode(dto.getSiteCode());
		rest.setData(dmsAbnormalEclpService.queryByPagerCondition(dmsAbnormalEclpCondition));
		return rest.getData();
	}
}
