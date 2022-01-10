package com.jd.bluedragon.distribution.record.dao;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bd.dms.automatic.sdk.common.utils.DateHelper;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.dao.common.AbstractCoreDaoH2Test;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.enums.WaybillHasnoPresiteRecordCallStatusEnum;
import com.jd.bluedragon.distribution.record.enums.WaybillHasnoPresiteRecordStatusEnum;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;
import com.jd.jddl.common.utils.Assert;

/**
 * 类描述信息
 *
 * @author: wuyoude
 * @date: 2021/11/25 14:34
 */
public class WaybillHasnoPresiteRecordDaoTest extends AbstractCoreDaoH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillHasnoPresiteRecordDao waybillHasnoPresiteRecordDao;

    @Before
    public void setUp() {
    }

    @Test
    public void test() throws Exception {
    	Integer siteCode = 910;
    	Integer endDmsId = 364605;
    	String waybillCode = "JDVE00000077323";
    	WaybillHasnoPresiteRecord dbData = new WaybillHasnoPresiteRecord();
    	dbData.setWaybillCode(waybillCode);
    	dbData.setSiteCode(siteCode);
    	dbData.setSiteName("测试站点");
    	dbData.setEndDmsId(endDmsId);
    	dbData.setDealCardUserErp("testErp");
    	dbData.setDealCardUserName("");
    	dbData.setCreateTime(new Date());
    	dbData.setUpdateTime(new Date());
    	dbData.setStatus(WaybillHasnoPresiteRecordStatusEnum.FOR_REVERSE.getCode());
    	dbData.setCallStatus(WaybillHasnoPresiteRecordCallStatusEnum.SUC.getCode());
    	dbData.setCheckTime(new Date());
    	dbData.setCallTime(new Date());
    	dbData.setFinishTime(new Date());
    	dbData.setDealCardTime(new Date());
    	
        int insert = waybillHasnoPresiteRecordDao.insert(dbData);
        Assert.assertTrue(insert==1);
        logger.info("waybillHasnoPresiteRecordDao.insert:结果{}",insert);
        
        WaybillHasnoPresiteRecord queryByWaybillCode = waybillHasnoPresiteRecordDao.queryByWaybillCode(waybillCode);
        Assert.assertTrue(queryByWaybillCode != null);
        logger.info("waybillHasnoPresiteRecordDao.queryByWaybillCode:结果{}",JsonHelper.toJson(queryByWaybillCode));
        
        queryByWaybillCode.setStatus(WaybillHasnoPresiteRecordStatusEnum.FOR_EXCHANGE.getCode());
        queryByWaybillCode.setCallStatus(WaybillHasnoPresiteRecordCallStatusEnum.FAIL.getCode());
        int update = waybillHasnoPresiteRecordDao.update(queryByWaybillCode);
        Assert.assertTrue(update==1);
        logger.info("waybillHasnoPresiteRecordDao.update:结果{}",update);
        
        WaybillHasnoPresiteRecordQo queryCondition = new WaybillHasnoPresiteRecordQo();
        queryCondition.setSiteCode(siteCode);
        queryCondition.setWaybillCode(waybillCode);
        queryCondition.setStatus(WaybillHasnoPresiteRecordStatusEnum.FOR_EXCHANGE.getCode());
        Date endTime = new Date();
        Date startTime = DateHelper.addDate(endTime, -24);
        queryCondition.setStartTimeTs(startTime);
        queryCondition.setEndTimeTs(endTime);
        queryCondition.setOffset(0);
        queryCondition.setLimit(10);
        
        Long selectCount = waybillHasnoPresiteRecordDao.selectCount(queryCondition);
        Assert.assertTrue(selectCount>0);
        logger.info("waybillHasnoPresiteRecordDao.selectCount:结果{}",selectCount);
        
        List<WaybillHasnoPresiteRecord> selectList = waybillHasnoPresiteRecordDao.selectList(queryCondition);
        Assert.assertTrue(selectList.size()>0);
        logger.info("waybillHasnoPresiteRecordDao.queryByWaybillCode:结果{}",JsonHelper.toJson(selectList));
        
        queryByWaybillCode.setStatus(WaybillHasnoPresiteRecordStatusEnum.INIT.getCode());
        int update0 = waybillHasnoPresiteRecordDao.update(queryByWaybillCode);
        
        logger.info("waybillHasnoPresiteRecordDao.update:结果{}",update0);
        queryByWaybillCode.setCallStatus(2);
        queryByWaybillCode.setStatus(2);
        queryByWaybillCode.setCallTime(DateHelper.addDate(dbData.getCallTime(), 1));
        waybillHasnoPresiteRecordDao.updateCallInfo(queryByWaybillCode);
        WaybillHasnoPresiteRecord updateCallInfoResult = waybillHasnoPresiteRecordDao.queryByWaybillCode(waybillCode);
        logger.info("waybillHasnoPresiteRecordDao.updateCallInfoResult:结果{}",JsonHelper.toJson(updateCallInfoResult));
        
        queryByWaybillCode.setSiteCode(300);
        queryByWaybillCode.setSiteName("站点名称300");
        queryByWaybillCode.setStatus(3);
        queryByWaybillCode.setFinishTime(DateHelper.addDate(dbData.getCallTime(), 1));
        waybillHasnoPresiteRecordDao.updateSucFinishInfo(queryByWaybillCode);
        
        WaybillHasnoPresiteRecord updateSucFinishInfoResult = waybillHasnoPresiteRecordDao.queryByWaybillCode(waybillCode);
        logger.info("waybillHasnoPresiteRecordDao.updateSucFinishInfo:结果{}",JsonHelper.toJson(updateSucFinishInfoResult));
        
        queryByWaybillCode.setSiteCode(400);
        queryByWaybillCode.setSiteName("站点名称400");
        queryByWaybillCode.setStatus(4);
        queryByWaybillCode.setFinishTime(DateHelper.addDate(dbData.getCallTime(), 2));
        waybillHasnoPresiteRecordDao.updateFailFinishInfo(queryByWaybillCode);
        WaybillHasnoPresiteRecord updateFailFinishInfo = waybillHasnoPresiteRecordDao.queryByWaybillCode(waybillCode);
        logger.info("waybillHasnoPresiteRecordDao.updateFailFinishInfo:结果{}",JsonHelper.toJson(updateFailFinishInfo));
        
        WaybillHasnoPresiteRecordQo selectScanListQuery = new WaybillHasnoPresiteRecordQo();
        selectScanListQuery.setStartId(0L);
        selectScanListQuery.setEndCreateTime(DateHelper.addDate(dbData.getCallTime(), 1));
        selectScanListQuery.setPageSize(100);
        List<WaybillHasnoPresiteRecord> selectScanListResult = waybillHasnoPresiteRecordDao.selectScanList(selectScanListQuery);
        logger.info("waybillHasnoPresiteRecordDao.selectScanListResult:结果{}",JsonHelper.toJson(selectScanListResult));
    }
}