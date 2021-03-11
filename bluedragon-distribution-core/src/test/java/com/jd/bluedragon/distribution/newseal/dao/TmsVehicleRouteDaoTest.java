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
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteCondition;
import com.jd.jddl.common.utils.Assert;

/**
 * 类描述信息
 *
 * @author: wuyoude
 * @date: 2020/6/24 14:34
 */
public class TmsVehicleRouteDaoTest extends AbstractCoreDaoH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TmsVehicleRouteDao tmsVehicleRouteDao;

    @Before
    public void setUp() {
    }

    @Test
    public void test() throws Exception {
    	String vehicleRouteCode = "vehicleRouteCode";
    	Integer originalSiteCode = 910;
    	TmsVehicleRoute dbData = new TmsVehicleRoute();
    	dbData.setOriginalSiteCode(originalSiteCode);
    	dbData.setDestinationSiteCode(39);
    	dbData.setVehicleRouteCode(vehicleRouteCode);
    	dbData.setVehicleJobCode("vehicleJobCode");
    	dbData.setJobCreateTime(new Date());
    	dbData.setCancelTime(new Date());
    	dbData.setCreateTime(new Date()); 
    	dbData.setCarrierTeamCode("carrierTeamCode");
    	
        int insert = tmsVehicleRouteDao.insert(dbData);
        Assert.assertTrue(insert==1);
        
        TmsVehicleRoute queryByVehicleRouteCode = tmsVehicleRouteDao.queryByVehicleRouteCode(vehicleRouteCode);
        Assert.assertTrue(queryByVehicleRouteCode != null);
        logger.info("tmsVehicleRouteDao.queryByVehicleRouteCode:结果",JsonHelper.toJson(queryByVehicleRouteCode));
        
        int logicalDeleteById = tmsVehicleRouteDao.logicalDeleteById(queryByVehicleRouteCode.getId());
        Assert.assertTrue(logicalDeleteById==1);
    }
}