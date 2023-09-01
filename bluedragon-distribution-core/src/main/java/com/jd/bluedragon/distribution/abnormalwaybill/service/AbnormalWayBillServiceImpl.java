package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormalwaybill.dao.AbnormalWayBillDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBillQuery;
import com.jd.ql.dms.print.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 异常操作服务接口实现
 * Created by shipeilin on 2017/11/17.
 */
@Service("abnormalWayBillService")
public class AbnormalWayBillServiceImpl implements AbnormalWayBillService {

    @Autowired
    AbnormalWayBillDao abnormalWayBillDao;

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
}
