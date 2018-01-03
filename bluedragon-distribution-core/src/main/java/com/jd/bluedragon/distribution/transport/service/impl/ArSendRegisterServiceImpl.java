package com.jd.bluedragon.distribution.transport.service.impl;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.distribution.transport.domain.*;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Override
    public Dao<ArSendRegister> getDao() {
        return this.arSendRegisterDao;
    }

    /**
     * 分隔符 逗号
     */
    private final static String COMMA = ",";

    @Transactional
    @Override
    public boolean insert(ArSendRegister arSendRegister, String[] sendCodes) {
        if (this.getDao().insert(arSendRegister)) {
            if (sendCodes != null && sendCodes.length > 0) {
                if (arSendCodeService.batchAdd(arSendRegister.getId(), sendCodes, arSendRegister.getCreateUser())) {
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
    public ArFlightInfo getTransportInfo(String code, TransportTypeEnum transportType) {
        // 调用TMS接口，根据航空单号获取航班信息
        ArFlightInfo arFlightInfo = new ArFlightInfo();
        arFlightInfo.setAirlineCompany("四川航空");
        arFlightInfo.setStartCityId(1);
        arFlightInfo.setStartCityName("北京");
        arFlightInfo.setEndCityId(2);
        arFlightInfo.setEndCityName("上海");
        arFlightInfo.setPlanStartTime(new Date());
        arFlightInfo.setPlanEndTime(new Date());
        return arFlightInfo;
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
        ArFlightInfo arFlightInfo;
        if (org.apache.commons.lang.StringUtils.isNotBlank(pdaSendRegister.getRailwayNo())) {
            sendRegister.setSiteOrder(pdaSendRegister.getRailwayNo());
            arFlightInfo = this.getTransportInfo(pdaSendRegister.getRailwayNo(), TransportTypeEnum.RAILWAY);
        } else {
            sendRegister.setOrderCode(pdaSendRegister.getAirNo());
            arFlightInfo = this.getTransportInfo(pdaSendRegister.getAirNo(), TransportTypeEnum.AIR_TRANSPORT);
        }
        if (arFlightInfo != null) {
            BeanUtils.copyProperties(arFlightInfo, sendRegister);
        }
        sendRegister.setSendCode(pdaSendRegister.getBatchNo());
        sendRegister.setSendNum(pdaSendRegister.getNum());
        sendRegister.setChargedWeight(pdaSendRegister.getWeight());
        sendRegister.setRemark(pdaSendRegister.getDemo());
        sendRegister.setShuttleBusType(pdaSendRegister.getOperateType());
        return sendRegister;
    }

}
