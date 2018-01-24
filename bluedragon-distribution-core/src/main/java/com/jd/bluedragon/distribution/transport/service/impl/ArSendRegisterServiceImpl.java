package com.jd.bluedragon.distribution.transport.service.impl;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.domain.*;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.tms.basic.dto.BasicAirFlightDto;
import com.jd.tms.basic.dto.BasicRailwayTrainDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.ws.BasicQueryWS;
import com.jd.tms.basic.ws.BasicSyncWS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.jd.bluedragon.distribution.transport.domain.ArTransportTypeEnum.AIR_TRANSPORT;
import static com.jd.bluedragon.distribution.transport.domain.ArTransportTypeEnum.RAILWAY;

/**
 * @author lixin39
 * @ClassName: ArSendRegisterServiceImpl
 * @Description: 发货登记表--Service接口实现
 * @date 2017年12月28日 09:46:12
 */
@Service("arSendRegisterService")
public class ArSendRegisterServiceImpl extends BaseService<ArSendRegister> implements ArSendRegisterService {

    @Autowired
    @Qualifier("arSendRegisterDao")
    private ArSendRegisterDao arSendRegisterDao;

    @Autowired
    ArSendCodeService arSendCodeService;

    @Autowired
    private BasicQueryWS basicQueryWS;

    @Autowired
    private BasicSyncWS basicSyncWS;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SendDatailDao sendDetailDao;

    @Autowired
    protected TaskService taskService;


    @Override
    public Dao<ArSendRegister> getDao() {
        return this.arSendRegisterDao;
    }

    /**
     * 分隔符 逗号
     */
    private final static String COMMA = ",";

    /**
     * 分隔符 冒号
     */
    private final static String COLON = ":";

    @Transactional
    @Override
    public boolean insert(ArSendRegister arSendRegister, String[] sendCodes) {
        if (this.getDao().insert(arSendRegister)) {
            if (sendCodes != null && sendCodes.length > 0) {
                if (arSendCodeService.batchAdd(arSendRegister.getId(), sendCodes, arSendRegister.getCreateUser())) {
                    // 推送全程跟踪
                    this.sendTrack(arSendRegister, sendCodes);
                    // 调用TMS BASIC订阅实时航班JSF接口
                    try {
                        CommonDto<String> commonDto = basicSyncWS.createAirFlightRealtime(arSendRegister.getTransportName(), arSendRegister.getSendDate());
                        if (commonDto != null) {
                            if (commonDto.getCode() != 1) {
                                logger.error("[空铁项目-发货登记]调用TMS-BASIC订阅实时航班JSF接口失败，返回[状态码：" + commonDto.getCode() + "][消息：" + commonDto.getMessage() + "]！");
                            }
                        }
                    } catch (Exception e) {
                        logger.error("[空铁项目-发货登记]调用TMS-BASIC订阅实时航班JSF接口异常！", e);
                    }
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean insert(ArSendRegister arSendRegister, String separator) {
        String sendCodeStr = arSendRegister.getSendCode();
        String[] sendCodeArray = null;
        if (StringUtils.isNotEmpty(sendCodeStr)) {
            sendCodeArray = sendCodeStr.split(separator);
        }
        return this.insert(arSendRegister, sendCodeArray);
    }

    @Transactional
    @Override
    public boolean update(ArSendRegister arSendRegister, String[] sendCodes) {
        if (this.getDao().update(arSendRegister)) {
            String user = arSendRegister.getCreateUser();
            if (sendCodes != null && sendCodes.length > 0) {
                List<ArSendCode> arSendCodes = arSendCodeService.getBySendRegisterId(arSendRegister.getId());
                if (arSendCodes != null && arSendCodes.size() > 0) {
                    arSendCodeService.deleteBySendRegisterId(arSendRegister.getId(), user);
                }
                if (arSendCodeService.batchAdd(arSendRegister.getId(), sendCodes, arSendRegister.getCreateUser())) {
                    return true;
                }
            } else {
                arSendCodeService.deleteBySendRegisterId(arSendRegister.getId(), user);
            }
        }
        return true;
    }

    @Transactional
    @Override
    public int deleteByIds(List<Long> ids, String userCode) {
        int count = 0;
        if (arSendRegisterDao.deleteByIds(ids, userCode) > 0) {
            for (Long id : ids) {
                arSendCodeService.deleteBySendRegisterId(id, userCode);
            }
            count++;
        }
        return count;
    }

    @Override
    public PagerResult<ArSendRegister> queryByPager(ArSendRegisterCondition condition) {
        PagerResult<ArSendRegister> pagerResult = this.getDao().queryByPagerCondition(condition);
        List<ArSendRegister> data = pagerResult.getRows();
        if (data != null && data.size() > 0) {
            Iterator<ArSendRegister> iterable = data.iterator();
            while (iterable.hasNext()) {
                ArSendRegister arSendRegister = iterable.next();
                List<ArSendCode> list = arSendCodeService.getBySendRegisterId(arSendRegister.getId());
                if (list != null && list.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (ArSendCode arSendCode : list) {
                        sb.append(arSendCode.getSendCode());
                        sb.append(COMMA);
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    arSendRegister.setSendCode(sb.toString());
                }
            }
        }
        return pagerResult;
    }

    /**
     * 从发货登记表中获取所有的已经登记的始发城市的信息
     *
     * @return
     */
    @Override
    public List<City> queryStartCityInfo() {
        List<ArSendRegister> arSendRegisters = arSendRegisterDao.queryStartCityInfo();
        List<City> cities = new ArrayList<City>();
        if (arSendRegisters == null) {
            logger.error("从发货登记表找查询到的始发城市信息为空！");
            return null;
        }

        for (ArSendRegister arSendRegister : arSendRegisters) {
            //如果城市的id和name有一个为空，则无法对应，直接舍弃该条记录
            if (arSendRegister.getStartCityId() == null || StringHelper.isEmpty(arSendRegister.getStartCityName())) {
                logger.error("发货登记表中的始发城市信息城市id或者城市名称为空，不组装，城市id:" + arSendRegister.getStartCityId() +
                        "，城市名称：" + arSendRegister.getStartCityName());
                continue;
            }
            cities.add(new City(arSendRegister.getStartCityId(), arSendRegister.getStartCityName()));
        }

        return cities;
    }

    /**
     * 从发货登记表中获取所有的已经登记的目的城市的信息
     *
     * @return
     */
    @Override
    public List<City> queryEndCityInfo() {
        List<ArSendRegister> arSendRegisters = arSendRegisterDao.queryEndCityInfo();
        List<City> cities = new ArrayList<City>();
        if (arSendRegisters == null) {
            logger.error("从发货登记表找查询到的目的城市信息为空！");
            return null;
        }

        for (ArSendRegister arSendRegister : arSendRegisters) {
            //如果城市的id和name有一个为空，则无法对应，直接舍弃该条记录
            if (arSendRegister.getEndCityId() == null || StringHelper.isEmpty(arSendRegister.getEndCityName())) {
                logger.error("发货登记表中的目的城市信息城市id或者城市名称为空，不组装，城市id:" + arSendRegister.getEndCityId() +
                        "，城市名称：" + arSendRegister.getEndCityName());
                continue;
            }
            cities.add(new City(arSendRegister.getEndCityId(), arSendRegister.getEndCityName()));
        }

        return cities;
    }

    /**
     * 从发货登记表中获取24小时内到达所选城市的航班/铁路信息
     *
     * @param arSendRegister
     * @return
     */
    public List<ArSendRegister> queryWaitReceive(ArSendRegister arSendRegister) {
        return arSendRegisterDao.queryWaitReceive(arSendRegister);
    }

    @Override
    public ArTransportInfo getTransportInfo(String code, String siteOrder, ArTransportTypeEnum transportType) {
        ArTransportInfo arTransportInfo = new ArTransportInfo();
        try {
            if (transportType == AIR_TRANSPORT) {
                CommonDto<BasicAirFlightDto> commonDto = basicQueryWS.getAirFlightByFlightNumber(code);
                if (commonDto != null && commonDto.getCode() == CommonDto.CODE_SUCCESS && commonDto.getData() != null) {
                    BasicAirFlightDto basicAirFlightDto = commonDto.getData();
                    // 调用TMS接口，根据航空单号获取航班信息
                    arTransportInfo.setTransCompany(basicAirFlightDto.getAirCompanyName());
                    arTransportInfo.setTransCompanyCode(basicAirFlightDto.getAirCompanyCode());
                    arTransportInfo.setStartCityId(basicAirFlightDto.getBeginCityId());
                    arTransportInfo.setStartCityName(basicAirFlightDto.getBeginCityName());
                    arTransportInfo.setEndCityId(basicAirFlightDto.getEndCityId());
                    arTransportInfo.setEndCityName(basicAirFlightDto.getEndCityName());
                    arTransportInfo.setStartStationId(basicAirFlightDto.getBeginNodeCode());
                    arTransportInfo.setStartStationName(basicAirFlightDto.getBeginNodeName());
                    arTransportInfo.setEndStationId(basicAirFlightDto.getEndNodeCode());
                    arTransportInfo.setEndStationName(basicAirFlightDto.getEndNodeName());
                    arTransportInfo.setPlanStartTime(basicAirFlightDto.getTakeOffTime());
                    arTransportInfo.setPlanEndTime(basicAirFlightDto.getTouchDownTime());
                }
            } else if (transportType == RAILWAY) {
                BasicRailwayTrainDto param = new BasicRailwayTrainDto();
                param.setTrainNumber(code);
                param.setTrainSiteOrder(siteOrder);
                CommonDto<BasicRailwayTrainDto> commonDto = basicQueryWS.getRailwayTrainByCondition(param);
                if (commonDto != null && commonDto.getCode() == CommonDto.CODE_SUCCESS && commonDto.getData() != null) {
                    BasicRailwayTrainDto railwayTrainDto = commonDto.getData();
                    arTransportInfo.setTransCompany(railwayTrainDto.getRailwayActName());
                    arTransportInfo.setTransCompanyCode(railwayTrainDto.getRailwayActCode());
                    arTransportInfo.setStartCityId(railwayTrainDto.getBeginCityId());
                    arTransportInfo.setStartCityName(railwayTrainDto.getEndCityName());
                    arTransportInfo.setEndCityId(railwayTrainDto.getEndCityId());
                    arTransportInfo.setEndCityName(railwayTrainDto.getEndCityName());
                    arTransportInfo.setStartStationId(railwayTrainDto.getBeginNodeCode());
                    arTransportInfo.setStartStationName(railwayTrainDto.getBeginNodeName());
                    arTransportInfo.setEndStationId(railwayTrainDto.getEndNodeCode());
                    arTransportInfo.setEndStationName(railwayTrainDto.getEndNodeName());
                    arTransportInfo.setPlanStartTime(railwayTrainDto.getPlanDepartTime());
                    arTransportInfo.setPlanEndTime(railwayTrainDto.getPlanArriveTime());
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("[空铁]调用TMS运输接口获取航班信息/铁路信息出现异常", e);
        }
        return arTransportInfo;
    }

    @Override
    public boolean executeOfflineTask(String body) {
        List<ArPdaSendRegister> registerList = JsonHelper.fromJsonUseGson(body, new TypeToken<List<ArPdaSendRegister>>() {
        }.getType());
        if (registerList != null && registerList.size() > 0) {
            for (ArPdaSendRegister pdaSendRegister : registerList) {
                this.insert(this.toDBDomain(pdaSendRegister), COMMA);
            }
        }
        return true;
    }

    private ArSendRegister toDBDomain(ArPdaSendRegister pdaSendRegister) {
        ArSendRegister sendRegister = new ArSendRegister();
        sendRegister.setTransportName(pdaSendRegister.getTransName());
        ArTransportInfo arTransportInfo;
        if (org.apache.commons.lang.StringUtils.isNotBlank(pdaSendRegister.getAirNo())) {
            sendRegister.setOrderCode(pdaSendRegister.getAirNo());
            sendRegister.setTransportType(AIR_TRANSPORT.getCode());
            arTransportInfo = this.getTransportInfo(pdaSendRegister.getTransName(), null, AIR_TRANSPORT);
        } else {
            sendRegister.setSiteOrder(pdaSendRegister.getRailwayNo());
            sendRegister.setTransportType(RAILWAY.getCode());
            arTransportInfo = this.getTransportInfo(pdaSendRegister.getTransName(), pdaSendRegister.getRailwayNo(), RAILWAY);
        }
        if (arTransportInfo != null) {
            BeanUtils.copyProperties(arTransportInfo, sendRegister);
        }
        sendRegister.setStatus(ArSendStatusEnum.ALREADY_SEND.getType());
        sendRegister.setSendCode(pdaSendRegister.getBatchCode());
        sendRegister.setSendNum(pdaSendRegister.getNum());
        sendRegister.setChargedWeight(pdaSendRegister.getWeight());
        sendRegister.setRemark(pdaSendRegister.getDemo());
        sendRegister.setShuttleBusType(pdaSendRegister.getOperateType());
        sendRegister.setShuttleBusNum(pdaSendRegister.getCarCode());
        sendRegister.setOperatorErp(pdaSendRegister.getSendUserCode());
        sendRegister.setOperationDept(pdaSendRegister.getSiteName());
        sendRegister.setOperationDeptCode(pdaSendRegister.getSiteCode());
        sendRegister.setSendDate(getPDASendDate(sendRegister.getPlanStartTime(), pdaSendRegister.getOperateTime()));
        sendRegister.setOperationTime(pdaSendRegister.getOperateTime());
        sendRegister.setCreateUser(pdaSendRegister.getSendUserCode());
        return sendRegister;
    }

    /**
     * 根据发车时间获取pda的发货时间，比较从TMS获取的航班/铁路发车时间和操作时间，若发车时间大于操作时间，取当日发车时间，否则取第二天发车时间
     *
     * @return
     */
    private Date getPDASendDate(String time, Date operateTime) {
        if (StringUtils.isNotEmpty(time)) {
            if (time.indexOf(COLON) > 0) {
                String[] timeArray = time.split(COLON);
                if (timeArray.length > 1) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
                    if (timeArray.length == 3) {
                        //时间格式 时:分:秒
                        calendar.set(Calendar.SECOND, Integer.parseInt(timeArray[2]));
                    } else {
                        // 时间格式 时:分 设置秒为0
                        calendar.set(Calendar.SECOND, 0);
                    }
                    if (operateTime.after(calendar.getTime())) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        return calendar.getTime();
                    } else {
                        return calendar.getTime();
                    }
                }
            }
            logger.error("[空铁发货登记]离线worker获取PDA发货时间，发车时间格式错误");
        }
        return null;
    }

    /**
     * 发送全程跟踪
     *
     * @param arSendRegister
     * @param sendCodes
     */
    private void sendTrack(ArSendRegister arSendRegister, String[] sendCodes) {
        try {
            BaseStaffSiteOrgDto siteDto = baseMajorManager.getBaseSiteBySiteId(arSendRegister.getOperationDeptCode());
            if (siteDto == null) {
                logger.error("[运输类型=" + ArTransportTypeEnum.getEnum(arSendRegister.getTransportType())
                        + "][运力名称=" + arSendRegister.getTransportName() + "][航空单号=" + arSendRegister.getOrderCode()
                        + "][铁路站序=" + arSendRegister.getSiteOrder() + "]根据[siteCode=" + arSendRegister.getOperationDeptCode()
                        + "]获取基础资料站点信息[getBaseSiteBySiteId]返回null,[空铁发货登记]不能回传全程跟踪");
            } else {
                for (String sendCode : sendCodes) {
                    List<SendDetail> sendDetailList = sendDetailDao.queryWaybillsBySendCode(sendCode);
                    if (null != sendDetailList && sendDetailList.size() > 0) {
                        for (SendDetail sendDetail : sendDetailList) {
                            try {
                                WaybillStatus waybillStatus = this.getWaybillStatus(arSendRegister, siteDto, sendDetail);
                                waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_AR_SEND_REGISTER);
                                waybillStatus.setRemark(this.getTrackRemark(arSendRegister));
                                // 添加到task表
                                taskService.add(toTask(waybillStatus));
                            } catch (Exception e) {
                                logger.error("[SendCode=" + sendCode + "][PackageCode=" + sendDetail.getPackageBarcode()
                                        + "][boxCode=" + sendDetail.getBoxCode()
                                        + "][],[空铁发货登记]回传全程跟踪出现异常");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[运输类型=" + ArTransportTypeEnum.getEnum(arSendRegister.getTransportType())
                    + "][运力名称=" + arSendRegister.getTransportName() + "][航空单号=" + arSendRegister.getOrderCode()
                    + "][铁路站序=" + arSendRegister.getSiteOrder() + "]根据[siteCode=" + arSendRegister.getOperationDeptCode()
                    + "][空铁发货登记]回传全程跟踪出现异常");
        }
    }

    private WaybillStatus getWaybillStatus(ArSendRegister arSendRegister, BaseStaffSiteOrgDto siteDto, SendDetail sendDetail) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        tWaybillStatus.setPackageCode(sendDetail.getPackageBarcode());
        //设置站点相关属性
        tWaybillStatus.setCreateSiteCode(siteDto.getSiteCode());
        tWaybillStatus.setCreateSiteName(siteDto.getSiteName());
        tWaybillStatus.setCreateSiteType(siteDto.getSiteType());
        BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());
        if (receiveSiteDto != null) {
            tWaybillStatus.setReceiveSiteCode(receiveSiteDto.getSiteCode());
            tWaybillStatus.setReceiveSiteName(receiveSiteDto.getSiteName());
            tWaybillStatus.setReceiveSiteType(receiveSiteDto.getSiteType());
        }
        tWaybillStatus.setOperatorId(arSendRegister.getOperatorId());
        tWaybillStatus.setOperateTime(arSendRegister.getOperationTime());
        tWaybillStatus.setOperator(arSendRegister.getOperatorErp());
        tWaybillStatus.setOrgId(siteDto.getOrgId());
        tWaybillStatus.setOrgName(siteDto.getOrgName());
        tWaybillStatus.setWaybillCode(sendDetail.getWaybillCode());
        tWaybillStatus.setBoxCode(sendDetail.getBoxCode());
        return tWaybillStatus;
    }

    public Task toTask(WaybillStatus tWaybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_WAYBILL);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(tWaybillStatus.getWaybillCode());
        task.setKeyword2(tWaybillStatus.getPackageCode());
        task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(tWaybillStatus));
        task.setType(Task.TASK_TYPE_AR_SEND_REGISTER);
        task.setOwnSign(BusinessHelper.getOwnSign());
        StringBuffer fingerprint = new StringBuffer();
        fingerprint
                .append(tWaybillStatus.getCreateSiteCode())
                .append("_")
                .append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
                        : tWaybillStatus.getReceiveSiteCode())).append("_")
                .append(tWaybillStatus.getOperateType()).append("_")
                .append(tWaybillStatus.getWaybillCode()).append("_")
                .append(tWaybillStatus.getOperateTime()).append("_")
                .append(Task.TASK_TYPE_AR_SEND_REGISTER);
        if (tWaybillStatus.getPackageCode() != null
                && !"".equals(tWaybillStatus.getPackageCode())) {
            fingerprint.append("_").append(tWaybillStatus.getPackageCode());
        }
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        return task;
    }

    private String getTrackRemark(ArSendRegister arSendRegister) {
        switch (ArTransportTypeEnum.getEnum(arSendRegister.getTransportType())) {
            case AIR_TRANSPORT: {
                return "航空：货物已发航空，" + arSendRegister.getStartStationName() + " — " + arSendRegister.getEndStationName() + "  航班号：" + arSendRegister.getTransportName();
            }
            case RAILWAY: {
                return "铁路：货物已发铁路，" + arSendRegister.getStartStationName() + " — " + arSendRegister.getEndStationName() + "  车次号：" + arSendRegister.getTransportName();
            }
        }
        return null;
    }

}