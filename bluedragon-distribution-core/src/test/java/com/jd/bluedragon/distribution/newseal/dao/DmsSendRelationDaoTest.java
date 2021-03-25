package com.jd.bluedragon.distribution.newseal.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    	dbData.setLineType(5);
    	
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
        List<Map<String, String>> orders = new ArrayList<Map<String, String>>();
        queryCondition.setOrders(orders);
        queryCondition.setOriginalSiteCode(originalSiteCode);
        
        queryCondition.setLineTypes(new ArrayList<Integer>());
        queryCondition.getLineTypes().add(5);
        
        Map<String,String> col1= new HashMap<String,String>();
        col1.put("orderColumn", "preSealStatus");
        col1.put("orderState", "desc");
        orders.add(col1);

        Map<String,String> col2= new HashMap<String,String>();
        col2.put("orderColumn", "departTime");
        col2.put("orderState", "asc");
        orders.add(col2);
        
        Map<String,String> col3= new HashMap<String,String>();
        col3.put("orderColumn", "jobCreateTime");
        col3.put("orderState", "asc");
        orders.add(col3);
        
        List<PassPreSealRecord> queryPassPreSealData = dmsSendRelationDao.queryPassPreSealData(queryCondition);
        Assert.assertTrue(queryPassPreSealData.size()==1);
        
        Integer countPassPreSealData = dmsSendRelationDao.countPassPreSealData(queryCondition);
        Assert.assertTrue(NumberHelper.gt0(countPassPreSealData));
    }
}