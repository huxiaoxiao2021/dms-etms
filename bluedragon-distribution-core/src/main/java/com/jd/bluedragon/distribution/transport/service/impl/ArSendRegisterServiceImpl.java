package com.jd.bluedragon.distribution.transport.service.impl;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.transport.dao.ArSendCodeDao;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.domain.*;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.*;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.exception.JMQException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    protected TaskService taskService;


    @Override
    public Dao<ArSendRegister> getDao() {
        return this.arSendRegisterDao;
    }

    @Autowired
    private ArSendCodeDao arSendCodeDao;

    @Autowired
    private SendDatailDao sendDetailDao;

    @Qualifier("arSendRegisterMQ")
    @Autowired
    private DefaultJMQProducer arSendRegisterMQ;

    /**
     * 分隔符 逗号
     */
    private final static String COMMA = ",";

    /**
     * 分隔符 冒号
     */
    private final static String COLON = ":";

    /**
     * 时间分隔符
     */
    private final static String DATE_SEPARATOR = "-";

    /**
     * 分隔符 回车
     */
    private final static String ENTER = "\n";

    @Override
    public ArSendRegister getById(Long id) {
        if (id != null) {
            ArSendRegister sendRegister = this.getDao().findById(id);
            sendRegister.setSendCode(this.getSendCodes(id, ENTER));
            return sendRegister;
        }
        return null;
    }

    private String getSendCodes(Long arSendRegisterId, String separator) {
        List<ArSendCode> list = arSendCodeService.getBySendRegisterId(arSendRegisterId);
        if (list != null && list.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (ArSendCode arSendCode : list) {
                sb.append(arSendCode.getSendCode());
                sb.append(separator);
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return null;
    }

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean insert(ArSendRegister arSendRegister, String[] sendCodes) {
        //所有新增发货登记先把发给路由MQ类型置为1，落库
        arSendRegister.setSendRouterMqType(ArSendRouterMqTypeEnum.AIR_NO_SEND.getCode());
        arSendRegister.setOperateType(ArSendRegisterEnum.AIR_INSERT.getCode());

        //向路由推MQ逻辑
        if(arSendRegister.getTransportType().equals(ArTransportTypeEnum.AIR_TRANSPORT.getCode())){
            for(String sendCode:sendCodes){
                String [] sendCodeList={sendCode};
                //查找批次号表里是否已有该批次,只返回时间最近的一条数据
                ArSendCode arSendCode=arSendCodeService.getBySendCode(sendCode);
                //如果有批次号存在，判断航班号有没有改动
                if (arSendCode != null) {
                    //如果有批次号存在，把该批次is_delete置为1，因为新增同样的批次号，之前的批次作废(以免飞常准发老批次MQ)
                    arSendCode.setIsDelete(1);
                    arSendCodeDao.update(arSendCode);

                    ArSendRegister arSendRegisterExits=this.findById(arSendCode.getSendRegisterId());
                    if (arSendRegisterExits != null && arSendRegisterExits.getTransportName() != null) {
                        //航班号有改动,需要向路由推MQ，如果航班号没有改动，不需要向路由推MQ
                        if(!arSendRegister.getTransportName().equals(arSendRegisterExits.getTransportName())){
                            mqToRouterDetail(arSendRegisterExits,arSendRegister,sendCodeList);
                        }else{
                            logger.warn("[空铁项目]重复添加批次号是："+arSendCode+"的航班："+arSendRegisterExits.getTransportName());
                        }
                    }else{
                        logger.warn("[空铁项目]根据批次号表的sendRegisterId："+arSendCode.getSendRegisterId()+"查询不到对应的发货登记记录");
                    }
                }
                //新增之前没有该批次号，向路由发MQ
                else{
                    try{
                        this.mqToRouter(arSendRegister, sendCodeList);
                    }catch (Exception e){
                        logger.error("[空铁项目]发货登记消息体发送给路由时发生异常，航班号:"+arSendRegister.getTransportName(), e);
                    }
                }
            }
        }

        if (this.getDao().insert(arSendRegister)) {
            if (sendCodes != null && sendCodes.length > 0) {
                if (arSendCodeService.batchAdd(arSendRegister.getId(), sendCodes, arSendRegister.getCreateUser())) {
                    // 推送全程跟踪
                    this.sendTrack(arSendRegister, sendCodes);

                    sendDetailMQTask(arSendRegister, sendCodes);
                    // 调用TMS BASIC订阅实时航班JSF接口
                    try {
                        CommonDto<String> commonDto = basicSyncWS.createAirFlightRealtime(arSendRegister.getTransportName(), arSendRegister.getSendDate());
                        if (commonDto != null) {
                            if (commonDto.getCode() != 1) {
                                logger.warn("[空铁项目-发货登记]调用TMS-BASIC订阅实时航班JSF接口失败，返回[状态码：" + commonDto.getCode() + "][消息：" + commonDto.getMessage() + "]！");
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

    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean update(ArSendRegister arSendRegister, String[] sendCodes) {
        ArSendRegister resource = this.getById(arSendRegister.getId());
        if(this.getDao().update(arSendRegister)){
            //判断能否取到发货登记记录以及批次号是否有值
            if (resource != null && sendCodes != null && sendCodes.length > 0) {
                //如果修改航班号,向路由发MQ并发全程跟踪，没有修改航班号不推MQ不发全程跟踪
                if ( !resource.getTransportName().equals(arSendRegister.getTransportName())) {
                    //发全程跟踪
                    this.sendTrackByUpdate(arSendRegister,sendCodes);
                    //只有航空才推向路由MQ
                    if(arSendRegister.getTransportType() != null && arSendRegister.getTransportType().equals(ArTransportTypeEnum.AIR_TRANSPORT.getCode())){
                        mqToRouterDetail(resource,arSendRegister,sendCodes);
                    }
                }
           }else{
                logger.warn("空铁---发货登记记录为空或者批次号列表为空");
            }
        }
        return true;
    }

    /**
     * 向路由推MQ
     * @param arSendRegister
     * @param sendCodes
     */
    private void mqToRouter(ArSendRegister arSendRegister, String[] sendCodes) throws JMQException {
        if (sendCodes != null) {
            for (String arSendCode : sendCodes) {
                List<SendDetail> sendDetailList = sendDetailDao.queryWaybillsBySendCode(arSendCode);
                if (null != sendDetailList && sendDetailList.size() > 0) {
                    for (SendDetail sendDetail : sendDetailList) {
                        /* 运单号 */
                        arSendRegister.setWaybillCode(sendDetail.getWaybillCode());
                        /* 包裹号 */
                        arSendRegister.setPackageCode(sendDetail.getPackageBarcode());
                        /* 批次号 */
                        arSendRegister.setSendCode(arSendCode);
                        /**
                         * 推MQ
                         */
                        arSendRegisterMQ.send(arSendRegister.getWaybillCode(), JsonHelper.toJson(arSendRegister));
                        logger.info("[空铁项目]新增或修改发货登记推送路由MQ消息成功，消息体：" + JsonHelper.toJson(arSendRegister));
                    }
                } else {
                    logger.warn("空铁推路由MQ---根据批次号获取发货明细为空，批次号：" + arSendCode);
                }
            }
        }else{
            logger.warn("空铁推路由MQ---获取批次号列表为空");
        }
    }

    /**
     * 向路由推送MQ方法实现
     * @param resource
     * @param arSendRegister
     * @param sendCodes
     */
    private void mqToRouterDetail(ArSendRegister resource,ArSendRegister arSendRegister, String[] sendCodes){
        //查出当天该航班号起飞的发货登记记录
        List<ArSendRegister> sendRegisterList = this.getListByTransInfo(ArTransportTypeEnum.AIR_TRANSPORT, resource.getTransportName(), null, resource.getSendDate());
        if (sendRegisterList != null && sendRegisterList.size() > 0) {
            //同一个航班号有可能有多条发货登记记录
            //查出时间最近的一条数据
            ArSendRegister arSendRegisterNew=sendRegisterList.get(0);
            //该条发货登记记录已经给路由发过MQ
            if (arSendRegisterNew.getSendRouterMqType().equals(ArSendRouterMqTypeEnum.AIR_ALREADY_SEND.getCode())) {
                arSendRegister.setOperateType(ArSendRegisterEnum.AIR_UPDATE_AFTERFLY.getCode());
                try{
                    this.mqToRouter(arSendRegister, sendCodes);
                }catch (Exception e){
                    logger.error("[空铁项目]发货登记消息体发送给路由时发生异常，航班号:"+arSendRegister.getTransportName(), e);
                }
                //再次更新发货登记表，把operateType字段落进去
                this.getDao().update(arSendRegister);
            } else{
                //如果没有发过MQ，说明飞机还未起飞
                arSendRegister.setOperateType(ArSendRegisterEnum.AIR_UPDATE_BEFOREFLY.getCode());
                try{
                    this.mqToRouter(arSendRegister, sendCodes);
                }catch (Exception e){
                    logger.error("[空铁项目]发货登记消息体发送给路由时发生异常，航班号:"+arSendRegister.getTransportName(), e);
                }
                //再次更新发货登记表，把operateType字段落进去
                this.getDao().update(arSendRegister);
            }
        }else{
            logger.warn("空铁推路由MQ---查不到该航班号发货登记记录，航班号："+resource.getTransportName());
        }

    }

    /**
     * 当航班号发生变化的更新时发送全程跟踪
     *
     * @param resource
     */
    private void sendTrackByUpdate(ArSendRegister resource,String[] sendCodes) {
        List<String> resourceSendCodes=new ArrayList<String>();
        for(String sendCode:sendCodes){
            resourceSendCodes.add(sendCode);
        }
        // 全部批次号发全程跟踪
        this.sendTrack(resource, resourceSendCodes.toArray(new String[resourceSendCodes.size()]));
    }


    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
                arSendRegister.setSendCode(this.getSendCodes(arSendRegister.getId(), COMMA));
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
            logger.warn("从发货登记表找查询到的始发城市信息为空！");
            return null;
        }

        for (ArSendRegister arSendRegister : arSendRegisters) {
            //如果城市的id和name有一个为空，则无法对应，直接舍弃该条记录
            if (arSendRegister.getStartCityId() == null || StringHelper.isEmpty(arSendRegister.getStartCityName())) {
                logger.warn("发货登记表中的始发城市信息城市id或者城市名称为空，不组装，城市id:" + arSendRegister.getStartCityId() +
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
            logger.warn("从发货登记表找查询到的目的城市信息为空！");
            return null;
        }

        for (ArSendRegister arSendRegister : arSendRegisters) {
            //如果城市的id和name有一个为空，则无法对应，直接舍弃该条记录
            if (arSendRegister.getEndCityId() == null || StringHelper.isEmpty(arSendRegister.getEndCityName())) {
                logger.warn("发货登记表中的目的城市信息城市id或者城市名称为空，不组装，城市id:" + arSendRegister.getEndCityId() +
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
    @Override
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
                    arTransportInfo.setAging(basicAirFlightDto.getAging());
                    return arTransportInfo;
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
                    arTransportInfo.setStartCityName(railwayTrainDto.getBeginCityName());
                    arTransportInfo.setEndCityId(railwayTrainDto.getEndCityId());
                    arTransportInfo.setEndCityName(railwayTrainDto.getEndCityName());
                    arTransportInfo.setStartStationId(railwayTrainDto.getBeginNodeCode());
                    arTransportInfo.setStartStationName(railwayTrainDto.getBeginNodeName());
                    arTransportInfo.setEndStationId(railwayTrainDto.getEndNodeCode());
                    arTransportInfo.setEndStationName(railwayTrainDto.getEndNodeName());
                    arTransportInfo.setPlanStartTime(railwayTrainDto.getPlanDepartTime());
                    arTransportInfo.setPlanEndTime(railwayTrainDto.getPlanArriveTime());
                    arTransportInfo.setAging(railwayTrainDto.getAging());
                    return arTransportInfo;
                }
            }
        } catch (Exception e) {
            logger.error("[空铁]调用TMS运输接口获取航班信息/铁路信息出现异常", e);
        }
        return null;
    }

    @Override
    public boolean executeOfflineTask(String body) {
        List<ArPdaSendRegister> registerList = JsonHelper.fromJsonUseGson(body, new TypeToken<List<ArPdaSendRegister>>() {
        }.getType());
        if (registerList != null && registerList.size() > 0) {
            for (ArPdaSendRegister pdaSendRegister : registerList) {
                try {
                    ArSendRegister register = this.toDBDomain(pdaSendRegister);
                    if (register != null) {
                        this.insert(register, COMMA);
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    logger.error("[空铁发货登记]执行离线发货登记新增任务时发生异常", e);
                    return false;
                }
            }
        }
        return true;
    }

    private ArSendRegister toDBDomain(ArPdaSendRegister pdaSendRegister) {
        ArSendRegister sendRegister = new ArSendRegister();
        sendRegister.setTransportName(pdaSendRegister.getTransName());
        sendRegister.setStatus(ArSendStatusEnum.ALREADY_SEND.getType());
        sendRegister.setSendCode(pdaSendRegister.getBatchCode());
        sendRegister.setSendNum(pdaSendRegister.getNum());
        sendRegister.setChargedWeight(pdaSendRegister.getWeight());
        sendRegister.setRemark(pdaSendRegister.getDemo());
        sendRegister.setShuttleBusType(pdaSendRegister.getOperateType());
        sendRegister.setShuttleBusNum(pdaSendRegister.getCarCode());
        sendRegister.setOperatorErp(pdaSendRegister.getSendUserCode());
        sendRegister.setOperatorName(pdaSendRegister.getUserName());
        sendRegister.setOperatorId(pdaSendRegister.getUserCode());
        sendRegister.setOperationDept(pdaSendRegister.getSiteName());
        sendRegister.setOperationDeptCode(pdaSendRegister.getSiteCode());
        sendRegister.setOperationTime(pdaSendRegister.getOperateTime());
        sendRegister.setCreateUser(pdaSendRegister.getSendUserCode());
        sendRegister.setSendDate(getPDASendDate(pdaSendRegister.getBoxCode()));
        ArTransportInfo arTransportInfo;
        try {
            if (StringUtils.isNotBlank(pdaSendRegister.getAirNo())) {
                sendRegister.setOrderCode(pdaSendRegister.getAirNo());
                sendRegister.setTransportType(AIR_TRANSPORT.getCode());
                arTransportInfo = this.getTransportInfo(pdaSendRegister.getTransName(), null, AIR_TRANSPORT);
            } else {
                sendRegister.setSiteOrder(pdaSendRegister.getRailwayNo());
                sendRegister.setTransportType(RAILWAY.getCode());
                arTransportInfo = this.getTransportInfo(pdaSendRegister.getTransName(), pdaSendRegister.getRailwayNo(), RAILWAY);
            }
        } catch (Exception e) {
            logger.error("[空铁发货登记]调用TMS运输接口获取运输信息时发生异常", e);
            return null;
        }
        if (arTransportInfo != null) {
            sendRegister.setTransCompanyCode(arTransportInfo.getTransCompanyCode());
            sendRegister.setTransCompany(arTransportInfo.getTransCompany());
            sendRegister.setStartCityId(arTransportInfo.getStartCityId());
            sendRegister.setStartCityName(arTransportInfo.getStartCityName());
            sendRegister.setEndCityId(arTransportInfo.getEndCityId());
            sendRegister.setEndCityName(arTransportInfo.getEndCityName());
            sendRegister.setStartStationId(arTransportInfo.getStartStationId());
            sendRegister.setStartStationName(arTransportInfo.getStartStationName());
            sendRegister.setEndStationId(arTransportInfo.getEndStationId());
            sendRegister.setEndStationName(arTransportInfo.getEndStationName());
            sendRegister.setAging(arTransportInfo.getAging());
            sendRegister.setPlanStartTime(getPlanDate(sendRegister.getSendDate(), arTransportInfo.getPlanStartTime(), 0));
            sendRegister.setPlanEndTime(getPlanDate(sendRegister.getSendDate(), arTransportInfo.getPlanEndTime(), arTransportInfo.getAging()));
        }
        return sendRegister;
    }

    @Override
    public Date getPlanDate(Date sendDate, String time, Integer aging) {
        if (sendDate != null && StringUtils.isNotEmpty(time)) {
            if (time.indexOf(COLON) > 0) {
                String[] timeArray = time.split(COLON);
                if (timeArray.length > 1) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sendDate);
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
                    if (timeArray.length == 3) {
                        //时间格式 时:分:秒
                        calendar.set(Calendar.SECOND, Integer.parseInt(timeArray[2]));
                    } else {
                        // 时间格式 时:分 设置秒为0
                        calendar.set(Calendar.SECOND, 0);
                    }
                    if (aging != null && aging > 0) {
                        calendar.add(Calendar.DATE, aging);
                    }
                    return calendar.getTime();
                }
            }
            logger.warn("[空铁发货登记]获取预计出发/抵达时间异常，发车时间格式错误");
        }
        return null;
    }

    @Override
    public List<ArSendRegister> getListByTransInfo(ArTransportTypeEnum transportType, String transportName, String siteCode, Date sendDate) {
        if (StringUtils.isNotEmpty(transportName) && sendDate != null) {
            Map<String, Object> parameter = new HashMap<String, Object>(4);
            if (transportType != null) {
                parameter.put("transportType ", transportType.getCode());
            }
            if (StringUtils.isNotEmpty(siteCode)) {
                parameter.put("siteCode", siteCode);
            }
            parameter.put("transportName", transportName);
            parameter.put("sendDate", sendDate);
            return arSendRegisterDao.getListByTransInfo(parameter);
        }
        return null;
    }

    /**
     * 根据发车时间获取pda的发货时间，比较从TMS获取的航班/铁路发车时间和操作时间，若发车时间大于操作时间，取当日发车时间，否则取第二天发车时间
     *
     * @return
     */
    private Date getPDASendDate(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr)) {
            if (dateStr.indexOf(DATE_SEPARATOR) > 0) {
                return DateHelper.parseDate(dateStr, "yyyy-MM-dd");
            }
            logger.warn("[空铁发货登记]离线worker获取PDA起飞/发车时间格式错误");
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
                logger.warn("[运输类型=" + ArTransportTypeEnum.getEnum(arSendRegister.getTransportType())
                        + "][运力名称=" + arSendRegister.getTransportName() + "][航空单号=" + arSendRegister.getOrderCode()
                        + "][铁路站序=" + arSendRegister.getSiteOrder() + "]根据[siteCode=" + arSendRegister.getOperationDeptCode()
                        + "]获取基础资料站点信息[getBaseSiteBySiteId]返回null,[空铁发货登记]不能回传全程跟踪");
            } else {
                for (String sendCode : sendCodes) {
                    try {
                        WaybillStatus waybillStatus = this.getWaybillStatus(arSendRegister, siteDto);
                        waybillStatus.setSendCode(sendCode);
                        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_AR_SEND_REGISTER);
                        waybillStatus.setRemark(this.getTrackRemark(arSendRegister));
                        // 添加到task表
                        taskService.add(toTask(waybillStatus));
                    } catch (Exception e) {
                        logger.error("[SendCode=" + sendCode + "][TransportName=" + arSendRegister.getTransportName()
                                + "][空铁发货登记]回传全程跟踪出现异常");
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

    private WaybillStatus getWaybillStatus(ArSendRegister arSendRegister, BaseStaffSiteOrgDto siteDto) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        //设置站点相关属性
        tWaybillStatus.setCreateSiteCode(siteDto.getSiteCode());
        tWaybillStatus.setCreateSiteName(siteDto.getSiteName());
        tWaybillStatus.setCreateSiteType(siteDto.getSiteType());
        tWaybillStatus.setOperatorId(arSendRegister.getOperatorId());
        tWaybillStatus.setOperateTime(arSendRegister.getOperationTime());
        tWaybillStatus.setOperator(arSendRegister.getOperatorName());
        tWaybillStatus.setOrgId(siteDto.getOrgId());
        tWaybillStatus.setOrgName(siteDto.getOrgName());
        return tWaybillStatus;
    }

    public Task toTask(WaybillStatus tWaybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(tWaybillStatus.getSendCode());
        task.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_TRACK_AR_SEND_REGISTER));
        task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(tWaybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
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

    private void sendDetailMQTask(ArSendRegister arSendRegister, String[] sendCodes) {
        if (sendCodes.length == 0) {
            logger.error("空铁发货登记批次号为空");
            return;
        }
        Task tTask = new Task();
        StringBuilder sb = new StringBuilder();
        for (String sendCode : sendCodes) {
            sb.append(sendCode);
            sb.append(Constants.SEPARATOR_COMMA);
        }
        tTask.setBody(sb.substring(0, sb.length() - 1));
        tTask.setKeyword2(String.valueOf(arSendRegister.getId()));
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        // 发送新发货明细MQ任务
        tTask.setKeyword1("6");
        tTask.setFingerprint(arSendRegister.getId() + "_" + tTask.getKeyword1());
        taskService.add(tTask, false);
    }

}