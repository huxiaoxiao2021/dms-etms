package com.jd.bluedragon.distribution.transport.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.transport.domain.*;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.impl.BusTypeService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.BusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据id获取实体基本信息
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex(Model model) {
        return "/transport/arSendRegister";
    }

    /**
     * 获取所有车辆类型信息
     *
     * @return
     */
    @RequestMapping(value = "/getAllBusType")
    public @ResponseBody
    JdResponse<List<BusType>> getAllBusType() {
        JdResponse<List<BusType>> response = new JdResponse<List<BusType>>();
        try {
            response.setData(busTypeService.getAllBusType());
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
    @RequestMapping(value = "/detail/{id}")
    public @ResponseBody
    JdResponse<ArSendRegister> detail(@PathVariable("id") Long id) {
        JdResponse<ArSendRegister> response = new JdResponse<ArSendRegister>();
        ArSendRegister arSendRegister = arSendRegisterService.findById(id);
        if (arSendRegister != null) {
            List<ArSendCode> list = arSendCodeService.getBySendRegisterId(id);
            if (list != null && list.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (ArSendCode arSendCode : list) {
                    sb.append(arSendCode.getSendCode());
                    sb.append(SEPARATOR);
                }
                sb.deleteCharAt(sb.length() - 2);
                arSendRegister.setSendCode(sb.toString());
            }
        }
        response.setData(arSendRegister);
        return response;
    }

    /**
     * 新增
     *
     * @param condition
     * @return
     */
    @RequestMapping(value = "/insert")
    public @ResponseBody
    JdResponse<Boolean> insert(@RequestBody ArSendRegisterCondition condition) {
        JdResponse<Boolean> response = new JdResponse<Boolean>();
        try {
            ArSendRegister arSendRegister = this.getBean(condition);
            String sendCodeStr = condition.getSendCode();
            String[] sendCodeArray = null;
            if (StringUtils.isNotEmpty(sendCodeStr)) {
                sendCodeArray = sendCodeStr.split(SEPARATOR);
            }
            response.setData(arSendRegisterService.insert(arSendRegister, sendCodeArray));
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
    @RequestMapping(value = "/update")
    public @ResponseBody
    JdResponse<Boolean> update(@RequestBody ArSendRegisterCondition condition) {
        JdResponse<Boolean> response = new JdResponse<Boolean>();
        try {
            ArSendRegister arSendRegister = this.getBean(condition);
            String sendCodeStr = condition.getSendCode();
            String[] sendCodeArray = null;
            if (StringUtils.isNotEmpty(sendCodeStr)) {
                sendCodeArray = sendCodeStr.split(SEPARATOR);
            }
            response.setData(arSendRegisterService.update(arSendRegister, sendCodeArray));
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
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody
    JdResponse<Integer> deleteByIds(@RequestBody List<Integer> ids) {
        JdResponse<Integer> response = new JdResponse<Integer>();
        try {
            /**
             * 类型转换 默认接受为Integer 需要转换成Long
             */
            List<Long> idsLong = new ArrayList<Long>();
            for (Integer id : ids){
                idsLong.add(Long.valueOf(id));
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            response.setData(arSendRegisterService.deleteByIds(idsLong, erpUser.getUserCode()));
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
    @RequestMapping(value = "/getTransportInfo")
    public
    @ResponseBody
    JdResponse<ArFlightInfo> getTransportInfo(@RequestBody ArSendRegisterCondition condition) {
        JdResponse<ArFlightInfo> response = null;
        try {
            String transportName = condition.getTransportName();
            if (StringUtils.isNotEmpty(transportName)) {
                response = new JdResponse<ArFlightInfo>();
                String orderCode = condition.getOrderCode();
                if (StringUtils.isNotEmpty(orderCode)){
                    response.setData(arSendRegisterService.getTransportInfo(orderCode, TransportTypeEnum.AIR_TRANSPORT));
                } else {
                    String siteOrder = condition.getSiteOrder();
                    if (StringUtils.isNotEmpty(siteOrder)){
                        response.setData(arSendRegisterService.getTransportInfo(siteOrder, TransportTypeEnum.RAILWAY));
                    }
                }
                return response;
            }
        } catch (Exception e) {
            logger.error("根据单号获取信息异常", e);
            response.toError("根据单号获取信息异常");
        }
        return response;
    }

    private ArSendRegister getBean(ArSendRegisterCondition ArSendRegisterCondition) throws ParseException {
        ArSendRegister arSendRegister = new ArSendRegister();
        arSendRegister.setId(ArSendRegisterCondition.getId());
        // 默认已发货
        arSendRegister.setStatus(1);

        arSendRegister.setTransportName(ArSendRegisterCondition.getTransportName());
        if (StringUtils.isNotEmpty(ArSendRegisterCondition.getSiteOrder())) {
            // 铁路
            arSendRegister.setTransportType(TransportTypeEnum.RAILWAY.getCode());
            arSendRegister.setSiteOrder(ArSendRegisterCondition.getSiteOrder());
        } else {
            // 航空
            arSendRegister.setTransportType(TransportTypeEnum.AIR_TRANSPORT.getCode());
            arSendRegister.setOrderCode(ArSendRegisterCondition.getOrderCode());
        }
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
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        arSendRegister.setOperatorErp(erpUser.getUserCode());
        arSendRegister.setCreateUser(erpUser.getUserCode());
        arSendRegister.setUpdateUser(erpUser.getUserCode());
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
        arSendRegister.setOperationDept(dto.getSiteName());
        return arSendRegister;
    }

}