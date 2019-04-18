package com.jd.bluedragon.distribution.transport.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.transport.domain.*;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.transport.service.impl.BusTypeService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.BusType;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.POST;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ArSendRegisterController
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年12月23日 下午9:49:23
 */
@Controller
@RequestMapping("transport/arSendRegister")
public class ArSendRegisterController {

    private static final Log logger = LogFactory.getLog(ArSendRegisterController.class);

    @Autowired
    ArSendRegisterService arSendRegisterService;

    @Autowired
    ArSendCodeService arSendCodeService;

    @Autowired
    private BusTypeService busTypeService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 分隔符 回车
     */
    private final static String SEPARATOR = "\n";

    /**
     * 时间格式化
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 根据id获取实体基本信息
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex(Model model) {
        return "/transport/arSendRegister";
    }

    /**
     * 获取所有车辆类型信息
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/getAllBusType")
    public @ResponseBody
    JdResponse<List<BusType>> getAllBusType() {
        JdResponse<List<BusType>> response = new JdResponse<List<BusType>>();
        try {
            response.setData(busTypeService.getNeedsBusType());
        } catch (Exception e) {
            logger.error("fail to getAllBusType！" + e.getMessage(), e);
            response.toError("获取所有车辆信息异常！");
        }
        return response;
    }

    /**
     * 根据id获取实体基本信息
     *
     * @param id
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody
    JdResponse<ArSendRegister> detail(@PathVariable("id") Long id) {
        JdResponse<ArSendRegister> response = new JdResponse<ArSendRegister>();
        response.setData(arSendRegisterService.getById(id));
        return response;
    }

    /**
     * 新增
     *
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/insert")
    public @ResponseBody
    JdResponse<Boolean> insert(@RequestBody ArSendRegisterCondition condition) {
        JdResponse<Boolean> response = new JdResponse<Boolean>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                ArSendRegister arSendRegister = this.getBean(condition, erpUser);
                String sendCodeStr = condition.getSendCode();
                String[] sendCodeArray = null;
                if (StringUtils.isNotEmpty(sendCodeStr)) {
                    sendCodeArray = sendCodeStr.split(SEPARATOR);
                    response.setData(arSendRegisterService.insert(arSendRegister, sendCodeArray));
                }else {
                    response.toFail("请输入批次号!");
                }
            } else {
                response.toFail("新增失败，获取ERP信息失败，请重新登录后再操作！");
            }
        } catch (Exception e) {
            logger.error("fail to insert！" + e.getMessage(), e);
            response.toError("新增失败，服务异常！");
        }
        return response;
    }

    /**
     * 新增
     *
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/update")
    public @ResponseBody
    JdResponse<Boolean> update(@RequestBody ArSendRegisterCondition condition) {
        JdResponse<Boolean> response = new JdResponse<Boolean>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                ArSendRegister arSendRegister = this.getBean(condition, erpUser);
                String sendCodeStr = condition.getSendCode();
                String[] sendCodeArray = null;
                if (StringUtils.isNotEmpty(sendCodeStr)) {
                    sendCodeArray = sendCodeStr.split(SEPARATOR);
                }
                response.setData(arSendRegisterService.update(arSendRegister, sendCodeArray));
            } else {
                response.toFail("更新失败，获取ERP信息失败，请重新登录后再操作！");
            }
        } catch (Exception e) {
            logger.error("fail to update！" + e.getMessage(), e);
            response.toError("更新失败，服务异常！");
        }
        return response;
    }

    /**
     * 根据id删除一条数据
     *
     * @param ids
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody
    JdResponse<Integer> deleteByIds(@RequestBody List<Integer> ids) {
        JdResponse<Integer> response = new JdResponse<Integer>();
        try {
            /**
             * 类型转换 默认接受为Integer 需要转换成Long
             */
            List<Long> idsLong = new ArrayList<Long>();
            for (Integer id : ids) {
                idsLong.add(Long.valueOf(id));
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                response.setData(arSendRegisterService.deleteByIds(idsLong, erpUser.getUserCode()));
            } else {
                response.toFail("删除失败，获取ERP信息失败，请重新登录后再操作！");
            }
        } catch (Exception e) {
            logger.error("fail to delete！" + e.getMessage(), e);
            response.toError("删除失败，服务异常！");
        }
        return response;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/listData")
    @POST
    public @ResponseBody
    PagerResult<ArSendRegister> listData(@RequestBody ArSendRegisterCondition condition) {
        return arSendRegisterService.queryByPager(condition);
    }

    /**
     * 获取航班信息
     *
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R)
    @RequestMapping(value = "/getTransportInfo")
    public
    @ResponseBody
    JdResponse<ArTransportInfo> getTransportInfo(@RequestBody ArSendRegisterCondition condition) {
        JdResponse<ArTransportInfo> response = new JdResponse<ArTransportInfo>();
        try {
            String transportName = condition.getTransportName();
            if (StringUtils.isNotEmpty(transportName)) {
                ArTransportInfo arTransportInfo = null;
                String orderCode = condition.getOrderCode();
                if (StringUtils.isNotEmpty(orderCode)) {
                    arTransportInfo = arSendRegisterService.getTransportInfo(transportName, null, ArTransportTypeEnum.AIR_TRANSPORT);
                } else {
                    String siteOrder = condition.getSiteOrder();
                    if (StringUtils.isNotEmpty(siteOrder)) {
                        arTransportInfo = arSendRegisterService.getTransportInfo(transportName, siteOrder, ArTransportTypeEnum.RAILWAY);
                    }
                }
                if (arTransportInfo != null) {
                    response.setData(arTransportInfo);
                    return response;
                } else {
                    response.toFail("根据航班号/车次号获取运输信息为空！");
                }
            }
        } catch (Exception e) {
            logger.error("根据航班号/车次号获取运输信息异常", e);
            response.toError("根据航班号/车次号获取运输信息异常");
        }
        return response;
    }

    private ArSendRegister getBean(ArSendRegisterCondition condition, ErpUserClient.ErpUser erpUser) throws ParseException {
        ArSendRegister arSendRegister = new ArSendRegister();
        arSendRegister.setId(condition.getId());
        // 默认已发货
        arSendRegister.setStatus(ArSendStatusEnum.ALREADY_SEND.getType());

        arSendRegister.setTransportName(condition.getTransportName() == null ? "" : condition.getTransportName());
        if (StringUtils.isNotEmpty(condition.getSiteOrder()) && StringUtils.isEmpty(condition.getOrderCode())) {
            // 铁路
            arSendRegister.setTransportType(ArTransportTypeEnum.RAILWAY.getCode());
            arSendRegister.setSiteOrder(condition.getSiteOrder());
            arSendRegister.setOrderCode("");
        } else {
            // 航空
            arSendRegister.setTransportType(ArTransportTypeEnum.AIR_TRANSPORT.getCode());
            arSendRegister.setOrderCode(condition.getOrderCode());
            arSendRegister.setSiteOrder("");
        }

        arSendRegister.setTransCompany(condition.getTransCompany() == null ? "" : condition.getTransCompany());
        arSendRegister.setTransCompanyCode(condition.getTransCompanyCode() == null ? "" : condition.getTransCompanyCode());
        arSendRegister.setStartCityName(condition.getStartCityName() == null ? "" : condition.getStartCityName());
        arSendRegister.setStartCityId(condition.getStartCityId() == null ? 0 : condition.getStartCityId());
        arSendRegister.setEndCityName(condition.getEndCityName() == null ? "" : condition.getEndCityName());
        arSendRegister.setEndCityId(condition.getEndCityId() == null ? 0 : condition.getEndCityId());
        arSendRegister.setStartStationName(condition.getStartStationName() == null ? "" : condition.getStartStationName());
        arSendRegister.setStartStationId(condition.getStartStationId() == null ? "" : condition.getStartStationId());
        arSendRegister.setEndStationName(condition.getEndStationName() == null ? "" : condition.getEndStationName());
        arSendRegister.setEndStationId(condition.getEndStationId() == null ? "" : condition.getEndStationId());
        if (StringUtils.isNotEmpty(condition.getSendDate())) {
            Date sendDate = sdf.parse(condition.getSendDate());
            arSendRegister.setSendDate(sendDate);
            arSendRegister.setPlanStartTime(arSendRegisterService.getPlanDate(sendDate, condition.getPlanStartTime(), condition.getAging()));
            arSendRegister.setPlanEndTime(arSendRegisterService.getPlanDate(sendDate, condition.getPlanEndTime(), condition.getAging()));
        }

        arSendRegister.setSendNum(condition.getSendNum());
        arSendRegister.setChargedWeight(condition.getChargedWeight());
        arSendRegister.setRemark(condition.getRemark() == null ? "" : condition.getRemark());
        arSendRegister.setShuttleBusNum(condition.getShuttleBusNum() == null ? "" : condition.getShuttleBusNum());
        arSendRegister.setShuttleBusType(condition.getShuttleBusType() == null ? 0 : condition.getShuttleBusType());

        arSendRegister.setOperationTime(new Date());
        arSendRegister.setOperatorErp(erpUser.getUserCode());
        arSendRegister.setOperatorId(erpUser.getUserId());
        arSendRegister.setOperatorName(erpUser.getUserName());

        arSendRegister.setCreateUser(erpUser.getUserCode());
        arSendRegister.setUpdateUser(erpUser.getUserCode());
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        arSendRegister.setOperationDept(dto.getSiteName());
        arSendRegister.setOperationDeptCode(dto.getSiteCode());
        return arSendRegister;
    }

}