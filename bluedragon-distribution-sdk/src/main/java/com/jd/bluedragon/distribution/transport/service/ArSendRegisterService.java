package com.jd.bluedragon.distribution.transport.service;

import com.jd.bluedragon.distribution.transport.domain.ArTransportInfo;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegisterCondition;
import com.jd.bluedragon.distribution.transport.domain.ArTransportTypeEnum;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wuyoude
 * @ClassName: ArSendRegisterService
 * @Description: 发货登记表--Service接口
 * @date 2017年12月28日 09:46:12
 */
public interface ArSendRegisterService extends Service<ArSendRegister> {

    /**
     * 根据id获取发货登记信息
     *
     * @param id
     * @return
     */
    ArSendRegister getById(Long id);

    /**
     * 新增
     *
     * @param arSendRegister
     * @param sendCodes
     * @return
     */
    boolean insert(ArSendRegister arSendRegister, String[] sendCodes);

    /**
     * 新增
     *
     * @param arSendRegister
     * @param separator      批次号分隔符
     * @return
     */
    boolean insert(ArSendRegister arSendRegister, String separator);

    /**
     * 修改
     *
     * @param arSendRegister
     * @param sendCodes
     * @return
     */
    boolean update(ArSendRegister arSendRegister, String[] sendCodes);

    /**
     * 根据ID删除
     *
     * @param ids
     * @param userCode
     * @return
     */
    int deleteByIds(List<Long> ids, String userCode);

    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    PagerResult<ArSendRegister> queryByPager(ArSendRegisterCondition condition);

    /**
     * 从发货登记表中获取已经登记的所有始发城市的信息
     *
     * @return
     */
    List<City> queryStartCityInfo();

    /**
     * 从发货登记表中获取已经登记的所有目的城市的信息
     *
     * @return
     */
    List<City> queryEndCityInfo();

    /**
     * 从发货登记表中获取24小时内到达所选城市的航班/铁路信息
     *
     * @param arSendRegister
     * @return
     */
    List<ArSendRegister> queryWaitReceive(ArSendRegister arSendRegister);

    /**
     * 根据单号获取运输信息
     *
     * @param code
     * @param siteOrder
     * @param transportType
     * @return
     */
    ArTransportInfo getTransportInfo(String code, String siteOrder, ArTransportTypeEnum transportType);

    /**
     * 处理pda上传任务
     *
     * @return
     */
    boolean executeOfflineTask(String body);

    /**
     * 根据发货日期、时间、时效（跨天）获取计划日期
     *
     * @param sendDate
     * @param time
     * @param aging
     * @return
     */
    Date getPlanDate(Date sendDate, String time, Integer aging);

    /**
     * 根据航班号、飞行日期获取航空发货登记信息
     *
     * @param transportName
     * @param sendDate
     * @return
     */
    List<ArSendRegister> getAirListByTransParam(String transportName, Date sendDate);

    /**
     * 根据中铁运单号获取铁路发货登记信息
     *
     * @param creTransBillCode 中铁单号
     * @return
     */
    List<ArSendRegister> getRailwayListByTransParam(String creTransBillCode);

}
