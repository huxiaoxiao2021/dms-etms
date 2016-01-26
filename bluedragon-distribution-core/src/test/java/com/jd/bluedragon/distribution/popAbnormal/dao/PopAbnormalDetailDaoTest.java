package com.jd.bluedragon.distribution.popAbnormal.dao;

import org.junit.Assert;

import java.util.Date;

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
        Long abnormalId = (long)50;
        popAbnormalDetailDao.findListByAbnormalId(abnormalId);
    }
	
	@Test
    public void testFindByObj() {
        PopAbnormalDetail parameter = new PopAbnormalDetail();
        parameter.setAbnormalDetailId((long)50);
        popAbnormalDetailDao.findByObj(parameter);
    }
	
	@Test
    public void testAdd() {
        PopAbnormalDetail parameter = new PopAbnormalDetail();
        parameter.setOperatorCode(415);
        parameter.setOperatorName("Jone");
        parameter.setOperatorTime(new Date());
        parameter.setRemark("Jim");
        parameter.setFingerPrint("Stone");
        popAbnormalDetailDao.add(PopAbnormalDetailDao.NAME_SPACE,parameter);
    }
}
