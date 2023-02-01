package com.jd.bluedragon.distribution.abnormalwaybill.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormalwaybill.dao.AbnormalWayBillDao;
import com.jd.bluedragon.distribution.abnormalwaybill.dao.AbnormalWaybillDiffDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWaybillDiff;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.common.annotation.CacheMethod;
import com.jd.ql.dms.print.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 异常操作服务接口实现
 * Created by shipeilin on 2017/11/17.
 */
@Service("abnormalWaybillDiffService")
public class AbnormalWaybillDiffServiceImpl implements AbnormalWaybillDiffService {
    private static final Logger logger = LoggerFactory.getLogger(AbnormalWaybillDiffServiceImpl.class);
    @Autowired
    AbnormalWaybillDiffDao abnormalWaybillDiffDao;

    @Override
    public void importExcel(List<AbnormalWaybillDiff> dataList) {
        if(CollectionUtils.isEmpty(dataList)){
            return;
        }

        try {
            List<List<AbnormalWaybillDiff>> partition = Lists.partition(dataList, 1000);
            for (List<AbnormalWaybillDiff> list:partition){
                abnormalWaybillDiffDao.addBatch(list);
            }
        }catch (Exception e){
            logger.error("批量插入表格数据异常",e);
        }
    }

    @Override
    public List<AbnormalWaybillDiff> query(AbnormalWaybillDiff abnormalWaybillDiff) {
        CallerInfo callerInfo = Profiler.registerInfo("DMS.BASE.AbnormalWaybillDiffServiceImpl.query", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            if(abnormalWaybillDiff != null &&
                    StringUtils.isBlank(abnormalWaybillDiff.getWaybillCodeC()) &&
                    StringUtils.isBlank(abnormalWaybillDiff.getWaybillCodeE())){
                //缺少入参直接返回
                logger.error("query 缺少参数 {},{}",abnormalWaybillDiff.getWaybillCodeC(),abnormalWaybillDiff.getWaybillCodeE());
                return null;
            }
            return abnormalWaybillDiffDao.query(abnormalWaybillDiff);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    @Override
    @CacheMethod(key="AbnormalWaybillDiffServiceImpl.queryCache-{0.waybillCodeC}",cacheBean="redisCache", nullTimeout = 1000 * 60, timeout = 1000 * 60)
    public List<AbnormalWaybillDiff> queryCache(AbnormalWaybillDiff abnormalWaybillDiff) {
        return this.query(abnormalWaybillDiff);
    }

    @Override
    public void save(String waybillCodeC, String waybillCodeE, String type) {
        if(StringUtils.isBlank(waybillCodeC) && StringUtils.isBlank(waybillCodeE)){
            //缺少入参直接返回
            logger.error("query 缺少参数 {},{}",waybillCodeC,waybillCodeE);
            return ;
        }

        AbnormalWaybillDiff diff = new AbnormalWaybillDiff();
        diff.setWaybillCodeC(waybillCodeC);
        diff.setWaybillCodeE(waybillCodeE);
        diff.setType(type);
        abnormalWaybillDiffDao.insert(diff);
    }

    @Override
    public void delByWaybillCodeE(String waybillCodeE) {
        if(StringUtils.isBlank(waybillCodeE)){
            //缺少入参直接返回
            logger.error("query 缺少参数 {}",waybillCodeE);
            return ;
        }
        abnormalWaybillDiffDao.delByWaybillCodeE(waybillCodeE);
    }

    @Override
    public void delByWaybillCodeC(String waybillCodeC) {
        if(StringUtils.isBlank(waybillCodeC)){
            //缺少入参直接返回
            logger.error("query 缺少参数 {}",waybillCodeC);
            return ;
        }
        abnormalWaybillDiffDao.delByWaybillCodeC(waybillCodeC);
    }

    @Override
    public void updateByWaybillCodeE(String waybillCodeE, String type) {
        if(StringUtils.isBlank(waybillCodeE)){
            //缺少入参直接返回
            logger.error("query 缺少参数 {}",waybillCodeE);
            return ;
        }
        abnormalWaybillDiffDao.updateByWaybillCodeE(waybillCodeE,type);
    }

    @Override
    public void updateByWaybillCodeC(String waybillCodeC, String type) {
        if(StringUtils.isBlank(waybillCodeC)){
            //缺少入参直接返回
            logger.error("query 缺少参数 {}",waybillCodeC);
            return ;
        }
        abnormalWaybillDiffDao.updateByWaybillCodeC(waybillCodeC,type);
    }
}
