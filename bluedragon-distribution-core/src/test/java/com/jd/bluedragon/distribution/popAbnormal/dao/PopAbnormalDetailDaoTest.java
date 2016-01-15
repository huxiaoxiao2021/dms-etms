package com.jd.bluedragon.distribution.popAbnormal.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;

public class PopAbnormalDetailDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopAbnormalDetailDao popAbnormalDetailDao;
	
	
	@Test
    public void testFindListByAbnormalId() {
        Long abnormalId = (long)1423;
        popAbnormalDetailDao.findListByAbnormalId(abnormalId);
    }
	
	@Test
    public void testFindByObj() {
        PopAbnormalDetail parameter = new PopAbnormalDetail();
        parameter.setAbnormalDetailId((long)8324);
        popAbnormalDetailDao.findByObj(parameter);
    }
	
	@Test
    public void testAdd() {
        PopAbnormalDetail parameter = new PopAbnormalDetail();
        parameter.setAbnormalId((long)5475);
        parameter.setOperatorCode(415);
        parameter.setOperatorName("Jone");
        parameter.setOperatorTime(new Date());
        parameter.setRemark("Jim");
        parameter.setFingerPrint("Stone");
        popAbnormalDetailDao.add(parameter);
    }
}
