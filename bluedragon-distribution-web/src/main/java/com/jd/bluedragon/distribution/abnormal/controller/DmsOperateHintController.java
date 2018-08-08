package com.jd.bluedragon.distribution.abnormal.controller;

import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

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

	private static final Log logger = LogFactory.getLog(DmsOperateHintController.class);

	@Autowired
	private DmsOperateHintService dmsOperateHintService;

    @Autowired
    private BaseMajorManager baseMajorManager;

	@Autowired
	private RedisManager redisManager;
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
			logger.error("PDA验货发货提示信息保存失败：" + JsonHelper.toJson(dmsOperateHint), e);
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
			logger.error("fail to delete！"+e.getMessage(),e);
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
