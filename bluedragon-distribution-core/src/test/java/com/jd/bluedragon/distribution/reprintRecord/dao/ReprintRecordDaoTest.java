package com.jd.bluedragon.distribution.reprintRecord.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.reprint.dao.ReprintRecordDao;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ReprintRecordDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ReprintRecordDao reprintRecordDao;


    @Test
    public void testAdd() {
        ReprintRecord parameter = new ReprintRecord();
        Date nowDate = new Date();
        parameter.setCreateTime(nowDate);
        parameter.setBarCode("JDX000156717833");
        parameter.setSiteCode(364605);
        parameter.setSiteName("北京通州分拣中心");
        parameter.setOperatorCode(889405);
        parameter.setOperatorName("方刚");
        parameter.setOperateTime(nowDate);
        Assert.assertTrue(reprintRecordDao.add(parameter) > 0);
    }

    @Test
    public void testQueryCount() {
        ReprintRecordQuery query = new ReprintRecordQuery();
        query.setSiteCode(364605);
        Assert.assertNotNull(reprintRecordDao.queryCount(query));
    }

    @Test
    public void testQueryList() {
        ReprintRecordQuery query = new ReprintRecordQuery();
        query.setSiteCode(364605);
        Assert.assertNotNull(reprintRecordDao.queryList(query));
    }

}
