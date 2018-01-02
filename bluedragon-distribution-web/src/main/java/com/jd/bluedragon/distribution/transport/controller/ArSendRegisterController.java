package com.jd.bluedragon.distribution.transport.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.transport.domain.ArFlightInfo;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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

import javax.ws.rs.POST;

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
    private BaseMajorManager baseMajorManager;

    /**
     * 根据id获取实体基本信息
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/transport/arSendRegister";
    }

    /**
     * 根据id获取实体基本信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}")
    public
    @ResponseBody
    JdResponse<ArSendRegister> detail(@PathVariable("id") Long id) {
        JdResponse<ArSendRegister> response = new JdResponse<ArSendRegister>();
        response.setData(arSendRegisterService.findById(id));
        return response;
    }

    /**
     * 保存数据
     *
     * @param ArSendRegisterCondition
     * @return
     */
    @RequestMapping(value = "/save")
    public
    @ResponseBody
    JdResponse<Boolean> save(@RequestBody ArSendRegisterCondition ArSendRegisterCondition) {
        JdResponse<Boolean> response = new JdResponse<Boolean>();
        try {
            response.setData(arSendRegisterService.saveOrUpdate(this.getBean(ArSendRegisterCondition)));
        } catch (Exception e) {
            logger.error("fail to save！" + e.getMessage(), e);
            response.toError("保存失败，服务异常！");
        }
        return response;
    }

    private ArSendRegister getBean(ArSendRegisterCondition ArSendRegisterCondition) throws ParseException {
        ArSendRegister arSendRegister = new ArSendRegister();
        // 默认已发货
        arSendRegister.setStatus(1);
        arSendRegister.setOrderCode(ArSendRegisterCondition.getOrderCode());
        arSendRegister.setTransportName(ArSendRegisterCondition.getTransportName());
        if (StringUtils.isNotEmpty(ArSendRegisterCondition.getSiteOrder())) {
            // 铁路
            arSendRegister.setTransportType(2);
            arSendRegister.setSiteOrder(ArSendRegisterCondition.getSiteOrder());
        } else {
            // 航空
            arSendRegister.setTransportType(1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (StringUtils.isNotEmpty(ArSendRegisterCondition.getSendDate())) {
            arSendRegister.setSendDate(sdf.parse(ArSendRegisterCondition.getSendDate()));
        }

        arSendRegister.setAirlineCompany(ArSendRegisterCondition.getAirlineCompany());

        arSendRegister.setStartCityName(ArSendRegisterCondition.getStartCityName());
        arSendRegister.setStartCityId(ArSendRegisterCondition.getStartCityId());
        arSendRegister.setEndCityName(ArSendRegisterCondition.getEndCityName());
        arSendRegister.setEndCityId(ArSendRegisterCondition.getEndCityId());

        arSendRegister.setStartStationName(ArSendRegisterCondition.getStartStationName());
        arSendRegister.setStartStationId(ArSendRegisterCondition.getStartStationId());
        arSendRegister.setEndStationName(ArSendRegisterCondition.getEndStationName());
        arSendRegister.setEndStationId(ArSendRegisterCondition.getEndStationId());

        if (StringUtils.isNotEmpty(ArSendRegisterCondition.getPlanStartTime())) {
            arSendRegister.setPlanStartTime(sdf.parse(ArSendRegisterCondition.getPlanStartTime()));
        }
        if (StringUtils.isNotEmpty(ArSendRegisterCondition.getPlanEndTime())) {
            arSendRegister.setPlanEndTime(sdf.parse(ArSendRegisterCondition.getPlanEndTime()));
        }
        arSendRegister.setSendNum(ArSendRegisterCondition.getSendNum());
        arSendRegister.setChargedWeight(ArSendRegisterCondition.getChargedWeight());
        arSendRegister.setRemark(ArSendRegisterCondition.getRemark());
        arSendRegister.setShuttleBusNum(ArSendRegisterCondition.getShuttleBusNum());
        arSendRegister.setShuttleBusType(ArSendRegisterCondition.getShuttleBusType());

//        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
//        arSendRegister.setOperatorErp(erpUser.getUserCode());
//        arSendRegister.setCreateUser(erpUser.getUserCode());
//        arSendRegister.setUpdateUser(erpUser.getUserCode());
//
//        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
//        arSendRegister.setOperationDept(dto.getSiteName());
        return arSendRegister;
    }

    /**
     * 根据id删除一条数据
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteByIds")
    public
    @ResponseBody
    JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
        JdResponse<Integer> response = new JdResponse<Integer>();
        try {
            response.setData(arSendRegisterService.deleteByIds(ids));
        } catch (Exception e) {
            logger.error("fail to delete！" + e.getMessage(), e);
            response.toError("删除失败，服务异常！");
        }
        return response;
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param arSendRegisterCondition
     * @return
     */
    @RequestMapping(value = "/listData")
    @POST
    public
    @ResponseBody
    PagerResult<ArSendRegister> listData(@RequestBody ArSendRegisterCondition arSendRegisterCondition) {
        return arSendRegisterService.queryByPagerCondition(arSendRegisterCondition);
    }

    /**
     * 获取航班信息
     *
     * @param orderCode
     * @return
     */
    @RequestMapping(value = "/getFlightInfo")
    public
    @ResponseBody
    JdResponse<ArFlightInfo> getFlightInfo(@RequestBody String orderCode) {
        JdResponse<ArFlightInfo> response = null;
        try {
            if (StringUtils.isNotEmpty(orderCode)) {
                response = new JdResponse<ArFlightInfo>();
                response.setData(arSendRegisterService.getFlightInfoByOrderCode(orderCode));
                return response;
            }
        } catch (Exception e) {
            logger.error("根据航空单号[" + orderCode + "]获取航班信息异常", e);
            response.toError("根据航空单号[" + orderCode + "]获取航班信息异常");
        }
        return response;
    }

}