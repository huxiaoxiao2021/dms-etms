package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
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

import java.util.*;

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
public class DmsOperateHintController extends DmsBaseController{

	private static final Logger log = LoggerFactory.getLogger(DmsOperateHintController.class);

	@Autowired
	private DmsOperateHintService dmsOperateHintService;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TOOL_DMSOPERATEHINT_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/abnormal/dmsOperateHint";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TOOL_DMSOPERATEHINT_R)
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
	@Authorization(Constants.DMS_WEB_TOOL_DMSOPERATEHINT_R)
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsOperateHint dmsOperateHint) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			//根据ID判断，如果是新增，则把updateuser也设置为创建人
			if(SerialRuleUtil.isMatchCommonWaybillCode(dmsOperateHint.getWaybillCode())){
				LoginUser loginUser = super.getLoginUser();
				dmsOperateHint.setUpdateUser(loginUser.getUserErp());
				dmsOperateHint.setUpdateUserCode(loginUser.getStaffNo());
				dmsOperateHint.setUpdateUserName(loginUser.getUserName());
				dmsOperateHint.setDmsSiteCode(loginUser.getSiteCode());
				dmsOperateHint.setDmsSiteName(loginUser.getSiteName());
				if(dmsOperateHint.getId() != null && dmsOperateHint.getId() > 0){//修改时
					DmsOperateHint temp = dmsOperateHintService.findById(dmsOperateHint.getId());
					if(temp == null){
						dmsOperateHint.setId(null);
					}else if(!dmsOperateHint.getWaybillCode().equals(temp.getWaybillCode())){
						rest.toFail("运单号不可修改，原始单号：" + temp.getWaybillCode());
					}
				}else{//新增时
					DmsOperateHintCondition condition = new DmsOperateHintCondition();
					condition.setWaybillCode(dmsOperateHint.getWaybillCode());
					condition.setHintType(DmsOperateHint.HINT_TYPE_USER);
					PagerResult list = dmsOperateHintService.queryByPagerCondition(condition);
					if(list != null && list.getTotal() > 0){
						rest.toFail("运单号已存在提示语：" + dmsOperateHint.getWaybillCode());
					}else {
						dmsOperateHint.setCreateUser(loginUser.getUserErp());
						dmsOperateHint.setCreateUserCode(loginUser.getStaffNo());
						dmsOperateHint.setCreateUserName(loginUser.getUserName());
					}
				}
				if(rest.isSucceed()){
					rest.setData(dmsOperateHintService.saveOrUpdate(dmsOperateHint));
				}
			} else{
				rest.toFail("运单号非法：" + dmsOperateHint.getWaybillCode());
			}
		} catch (Exception e) {
			log.error("PDA验货发货提示信息保存失败：{}", JsonHelper.toJson(dmsOperateHint), e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsOperateHint
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TOOL_DMSOPERATEHINT_R)
	@RequestMapping(value = "/batchsave")
	public @ResponseBody JdResponse<Boolean> batchSave(@RequestBody DmsOperateHint dmsOperateHint) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			//根据ID判断，如果是新增，则把updateuser也设置为创建人
			if(StringUtils.isNotEmpty(dmsOperateHint.getWaybillCode())){
				LoginUser loginUser = super.getLoginUser();
				dmsOperateHint.setCreateUser(loginUser.getUserErp());
				dmsOperateHint.setCreateUserCode(loginUser.getStaffNo());
				dmsOperateHint.setCreateUserName(loginUser.getUserName());
				dmsOperateHint.setUpdateUser(loginUser.getUserErp());
				dmsOperateHint.setUpdateUserCode(loginUser.getStaffNo());
				dmsOperateHint.setUpdateUserName(loginUser.getUserName());
				dmsOperateHint.setDmsSiteCode(loginUser.getSiteCode());
				dmsOperateHint.setDmsSiteName(loginUser.getSiteName());
				String[] waybills = dmsOperateHint.getWaybillCode().split("\n");
				//运单参数去重
				Set<String> waybillSet = new HashSet<String>(Arrays.asList(waybills));
				List<String> existWaybills = new ArrayList<String>();
				List<String> illegalWaybills = new ArrayList<String>();
				DmsOperateHintCondition condition = new DmsOperateHintCondition();
				condition.setHintType(DmsOperateHint.HINT_TYPE_USER);
				for(String waybill : waybillSet){
					waybill = waybill.trim();
					if(StringHelper.isEmpty(waybill)){
						continue;
					}
					if(SerialRuleUtil.isMatchCommonWaybillCode(waybill)){
						condition.setWaybillCode(waybill);
						PagerResult list = dmsOperateHintService.queryByPagerCondition(condition);
						if(list != null && list.getTotal() > 0){
							existWaybills.add(waybill);
							continue;
						}
						dmsOperateHint.setId(null);
						dmsOperateHint.setWaybillCode(waybill);
						dmsOperateHintService.saveOrUpdate(dmsOperateHint);
					} else{
						illegalWaybills.add(waybill);
					}
				}
				if(existWaybills.isEmpty() && illegalWaybills.isEmpty()){
					rest.setData(true);
				}else{
					String msg = "以下运单新增失败：";
					if(!existWaybills.isEmpty()){
						msg += "\n已存在运单：" + existWaybills.toString();
					}
					if(!illegalWaybills.isEmpty()){
						msg += "\n非法运单号：" + illegalWaybills.toString();
					}
					rest.toFail(msg);
				}
			}
		} catch (Exception e) {
			log.error("PDA验货发货提示信息保存失败：{}", JsonHelper.toJson(dmsOperateHint), e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_TOOL_DMSOPERATEHINT_R)
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(dmsOperateHintService.deleteByIds(ids));
		} catch (Exception e) {
			log.error("fail to delete！",e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsOperateHintCondition
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_TOOL_DMSOPERATEHINT_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsOperateHint> listData(@RequestBody DmsOperateHintCondition dmsOperateHintCondition) {
		JdResponse<PagerResult<DmsOperateHint>> rest = new JdResponse<PagerResult<DmsOperateHint>>();
		rest.setData(dmsOperateHintService.queryByPagerCondition(dmsOperateHintCondition));
		return rest.getData();
	}
}
