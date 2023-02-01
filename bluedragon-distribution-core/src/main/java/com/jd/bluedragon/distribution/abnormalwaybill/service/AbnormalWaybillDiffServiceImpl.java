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
import com.jd.ql.dms.print.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
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
        return abnormalWaybillDiffDao.query(abnormalWaybillDiff);
    }
}
