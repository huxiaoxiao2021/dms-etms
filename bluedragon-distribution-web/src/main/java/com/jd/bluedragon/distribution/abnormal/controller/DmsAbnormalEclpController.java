package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author wuyoude
 * @ClassName: DmsAbnormalEclpController
 * @Description: ECLP外呼申请表--Controller实现
 * @date 2018年03月14日 16:31:20
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
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ECLPREFUSED_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/abnormal/dmsAbnormalEclp";
    }

    /**
     * 根据id获取基本信息
     *
     * @param id
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ECLPREFUSED_R)
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody
    JdResponse<DmsAbnormalEclp> detail(@PathVariable("id") Long id) {
        JdResponse<DmsAbnormalEclp> rest = new JdResponse<DmsAbnormalEclp>();
        rest.setData(dmsAbnormalEclpService.findById(id));
        return rest;
    }

    /**
     * 保存数据
     *
     * @param dmsAbnormalEclp
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ECLPREFUSED_R)
    @RequestMapping(value = "/save")
    public @ResponseBody
    JdResponse<Boolean> save(@RequestBody DmsAbnormalEclp dmsAbnormalEclp) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        try {
            //有可能录入包裹号
            if (!SerialRuleUtil.isMatchCommonWaybillCode(dmsAbnormalEclp.getWaybillCode())) {
                dmsAbnormalEclp.setWaybillCode(SerialRuleUtil.getWaybillCode(dmsAbnormalEclp.getWaybillCode()));
            }

            if (SerialRuleUtil.isMatchCommonWaybillCode(dmsAbnormalEclp.getWaybillCode())) {
                return dmsAbnormalEclpService.save(dmsAbnormalEclp);
            } else {
                rest.toFail("运单号非法：" + dmsAbnormalEclp.getWaybillCode());
            }
        } catch (Exception e) {
            logger.error("库房拒收外呼申请保存异常：" + JsonHelper.toJson(dmsAbnormalEclp), e);
            rest.toError("保存失败，服务异常！");
        }
        return rest;
    }

    /**
     * 根据id删除多条数据
     *
     * @param ids
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ECLPREFUSED_R)
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody
    JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
        JdResponse<Integer> rest = new JdResponse<Integer>();
        try {
            rest.setData(dmsAbnormalEclpService.deleteByIds(ids));
        } catch (Exception e) {
            logger.error("库房拒收外呼申请删除异常：" + JsonHelper.toJson(ids), e);
            rest.toError("删除失败，服务异常！");
        }
        return rest;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param dmsAbnormalEclpCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_ECLPREFUSED_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody
    PagerResult<DmsAbnormalEclp> listData(@RequestBody DmsAbnormalEclpCondition dmsAbnormalEclpCondition) {
        JdResponse<PagerResult<DmsAbnormalEclp>> rest = new JdResponse<PagerResult<DmsAbnormalEclp>>();
        //查询条件无运单号时，限制只能查自己分拣中心的
        if (StringUtils.isBlank(dmsAbnormalEclpCondition.getWaybillCode())) {
            LoginContext loginContext = LoginContext.getLoginContext();
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
            dmsAbnormalEclpCondition.setDmsSiteCode(dto.getSiteCode());
        }
        rest.setData(dmsAbnormalEclpService.queryByPagerCondition(dmsAbnormalEclpCondition));
        return rest.getData();
    }
}
