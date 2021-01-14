package com.jd.bluedragon.distribution.newseal.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractCoreDaoH2Test;
import com.jd.bluedragon.distribution.newseal.entity.PreSealBatch;
import com.jd.jddl.common.utils.Assert;

/**
 * 类描述信息
 *
 * @author: wuyoude
 * @date: 2020/6/24 14:34
 */
public class PreSealBatchDaoTest extends AbstractCoreDaoH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PreSealBatchDao preSealBatchDao;

    @Before
    public void setUp() {
    }

    @Test
    public void test() throws Exception {
    	String preSealUuid = "testuuid";
    	List<PreSealBatch> preSealBatchs = new ArrayList<PreSealBatch>();
    	PreSealBatch preSealBatch = new PreSealBatch();
    	preSealBatch.setBatchCode("batchCode");
    	preSealBatch.setCreateTime(new Date());
    	preSealBatch.setPreSealUuid(preSealUuid);
    	preSealBatchs.add(preSealBatch);
    	
        int batchInsert = preSealBatchDao.batchInsert(preSealBatchs);
        Assert.assertTrue(batchInsert==preSealBatchs.size());
        
        List<String> queryByUuid = preSealBatchDao.queryByUuid(preSealUuid);
        Assert.assertTrue(queryByUuid.size()==preSealBatchs.size());
        
        List<String> preSealUuids = new ArrayList<String>();
        preSealUuids.add(preSealUuid);
        List<String> querySendCodesByUuids = preSealBatchDao.querySendCodesByUuids(preSealUuids);
        Assert.assertTrue(querySendCodesByUuids.size()==preSealBatchs.size());
        
        int batchLogicalDeleteByUuid = preSealBatchDao.batchLogicalDeleteByUuid(preSealUuid);
        Assert.assertTrue(batchLogicalDeleteByUuid==preSealBatchs.size());
        
        
        logger.info("preSealBatchDao.batchInsert:结果{0}",batchInsert);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme