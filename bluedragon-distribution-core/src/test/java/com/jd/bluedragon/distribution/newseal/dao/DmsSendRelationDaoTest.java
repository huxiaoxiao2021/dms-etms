package com.jd.bluedragon.distribution.newseal.dao;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.dao.common.AbstractCoreDaoH2Test;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealRecord;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jddl.common.utils.Assert;

/**
 * 类描述信息
 *
 * @author: wuyoude
 * @date: 2021/1/05 14:34
 */
public class DmsSendRelationDaoTest extends AbstractCoreDaoH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsSendRelationDao dmsSendRelationDao;

    @Before
    public void setUp() {
    }

    @Test
    public void test() throws Exception {
    	Integer originalSiteCode = 910;
    	Integer destinationSiteCode = 39;
    	DmsSendRelation dbData = new DmsSendRelation();
    	dbData.setOriginalSiteCode(originalSiteCode);
    	dbData.setDestinationSiteCode(destinationSiteCode);
    	dbData.setCreateTime(new Date());
    	
        int insert = dmsSendRelationDao.insert(dbData);
        Assert.assertTrue(insert==1);
        
        DmsSendRelation queryByBusinessKey = dmsSendRelationDao.queryByBusinessKey(dbData);
        Assert.assertTrue(queryByBusinessKey != null);
        logger.info("tmsVehicleRouteDao.queryByVehicleRouteCode:结果",JsonHelper.toJson(queryByBusinessKey));
        
        queryByBusinessKey.setDestinationSiteName("destinationSiteName");
        int update = dmsSendRelationDao.update(queryByBusinessKey);
        Assert.assertTrue(update==1);
        
        PassPreSealQueryRequest queryCondition = new PassPreSealQueryRequest();
        queryCondition.setOriginalSiteCode(originalSiteCode);
        queryCondition.setEffectStartTime(DateHelper.addDate(new Date(), -7));
        queryCondition.setOffset(0);
        queryCondition.setLimit(10);
        
        List<PassPreSealRecord> queryPassPreSealData = dmsSendRelationDao.queryPassPreSealData(queryCondition);
        Assert.assertTrue(queryPassPreSealData.size()==1);
        
        Integer countPassPreSealData = dmsSendRelationDao.countPassPreSealData(queryCondition);
        Assert.assertTrue(NumberHelper.gt0(countPassPreSealData));
    }
}