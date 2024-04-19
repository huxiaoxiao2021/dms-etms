package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.abnormalwaybill.dao.AbnormalWayBillDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBillQuery;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.reverse.domain.CancelReturnGroupWhiteListConf;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.print.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.Constants.CANCEL_RETURN_GROUP_WHITE_LIST_CONF;
import static com.jd.bluedragon.Constants.DB_SQL_IN_LIMIT_NUM;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

/**
 * 异常操作服务接口实现
 * Created by shipeilin on 2017/11/17.
 */
@Service("abnormalWayBillService")
public class AbnormalWayBillServiceImpl implements AbnormalWayBillService {

    @Autowired
    AbnormalWayBillDao abnormalWayBillDao;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 根据运单号查找异常处理记录
     * @param wayBillCode
     * @return
     */
    @Override
    public AbnormalWayBill getAbnormalWayBillByWayBillCode(String wayBillCode, Integer siteCode) {
        return abnormalWayBillDao.query(wayBillCode, siteCode);
    }

    /**
     * 新增运单的异常处理记录
     * @param abnormalWayBill
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.insertAbnormalWayBill", mState = {JProEnum.TP})
    public int insertAbnormalWayBill(AbnormalWayBill abnormalWayBill) {
        return abnormalWayBillDao.insert(abnormalWayBill);
    }

    /**
     * 批量增加运单的异常处理记录
     * @param wayBillList
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.insertBatchAbnormalWayBill", mState = {JProEnum.TP})
    public int insertBatchAbnormalWayBill(List<AbnormalWayBill> wayBillList) {
        return abnormalWayBillDao.addBatch(wayBillList);
    }

    /**
     * 根据提报异常的条码号和站点编号查询异常处理记录
     * @param createSiteCode
     * @param qcValue
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.getAbnormalWayBillByQcValue", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public AbnormalWayBill getAbnormalWayBillByQcValue(Integer createSiteCode, String qcValue) {
        return abnormalWayBillDao.getAbnormalWayBillByQcValue(createSiteCode, qcValue);
    }
    /**
     * 根据运单号查询提报的异常,返回最后一次提交的异常记录
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalWayBillService.queryAbnormalWayBillByWayBillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public AbnormalWayBill queryAbnormalWayBillByWayBillCode(String waybillCode) {
    	if(StringHelper.isEmpty(waybillCode)){
    		return null;
    	}
        return abnormalWayBillDao.queryAbnormalWayBillByWayBillCode(waybillCode);
    }

    /**
     * 根据查询条件查询数据
     *
     * @param abnormalWayBill 查询入参
     * @return 查询结果列表
     * @author fanggang7
     * @time 2023-08-22 15:21:15 周二
     */
    @Override
    public AbnormalWayBill queryOneByParam(AbnormalWayBill abnormalWayBill) {
        if(abnormalWayBill == null){
            return null;
        }
        return abnormalWayBillDao.queryOneByParam(abnormalWayBill);
    }

    /**
     * 根据查询条件查询数据统计
     *
     * @param abnormalWayBillQuery 查询入参
     * @return 查询结果列表
     * @author fanggang7
     * @time 2023-08-22 15:21:15 周二
     */
    @Override
    public Long queryCountByQueryParam(AbnormalWayBillQuery abnormalWayBillQuery) {
        if(abnormalWayBillQuery == null){
            return null;
        }
        return abnormalWayBillDao.queryCountByQueryParam(abnormalWayBillQuery);
    }

    /**
     * 根据查询条件查询数据
     *
     * @param abnormalWayBillQuery 查询入参
     * @return 查询结果列表
     * @author fanggang7
     * @time 2023-08-22 15:21:15 周二
     */
    @Override
    public List<AbnormalWayBill> queryPageListByQueryParam(AbnormalWayBillQuery abnormalWayBillQuery) {
        if(abnormalWayBillQuery == null){
            return null;
        }
        if(abnormalWayBillQuery.getPageNumber() <= 0){
            abnormalWayBillQuery.setPageNumber(1);
        }
        return abnormalWayBillDao.queryPageListByQueryParam(abnormalWayBillQuery);
    }

    /**
     * 是否为破损订单 1. 只针对一单一件的场景 2. 真针对特定异常编码
     *
     * @param waybillCode
     * @param request
     * @return
     */
    @Override
    public boolean isDamagedWaybill(String waybillCode, QualityControlRequest request) {
        // 只针对一单一件的场景
        com.jd.etms.waybill.domain.Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null || !Objects.equals(waybill.getGoodNumber(), INTEGER_ONE)) {
            return false;
        }

        // 异常编码校验
        SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(CANCEL_RETURN_GROUP_WHITE_LIST_CONF);
        if (sysConfig == null || StringUtils.isEmpty(sysConfig.getConfigContent())) {
            return false;
        }
        CancelReturnGroupWhiteListConf conf = JsonHelper.fromJson(sysConfig.getConfigContent(), CancelReturnGroupWhiteListConf.class);
        if (conf == null || CollectionUtils.isEmpty(conf.getAbnormalCauseList())) {
            return false;
        }
        if (conf.getAbnormalCauseList().contains(request.getQcCode())) {
            return true;
        }
        return false;
    }

    /**
     * 组装参数
     * @param waybillCode
     * @param siteId
     * @return
     */
    private AbnormalWayBillQuery convertAbnormalWayBillQuery(String waybillCode, Integer siteId) {
        AbnormalWayBillQuery query = new AbnormalWayBillQuery();
        query.setWaybillCode(waybillCode);
        query.setCreateSiteCode(siteId);
        query.setPageNumber(INTEGER_ONE);
        // 查询100条记录
        query.setLimit(DB_SQL_IN_LIMIT_NUM);
        return query;
    }
}
