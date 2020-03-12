package com.jd.bluedragon.distribution.financialForKA.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/2/27 17:01
 */
public class WaybillCodeCheckDaoTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private WaybillCodeCheckDao waybillCodeCheckDao;

    private WaybillCodeCheckDto detail;

    private KaCodeCheckCondition condition;

    @Before
    public void setUp() throws Exception {
        detail = new WaybillCodeCheckDto();
        detail.setWaybillCode("JDVA00049341283");
        detail.setCompareCode("JDVA00023324786");
        detail.setBusiCode("010K100125");
        detail.setBusiName("010K100125");
        detail.setOperateSiteCode(910);
        detail.setOperateSiteName("北京通州分拣中心");
        detail.setOperateErp("bjxings");
        detail.setCreateTime(new Date());
        detail.setOperateTime(new Date());
        detail.setUpdateTime(new Date());

        condition = new KaCodeCheckCondition();
        condition.setWaybillCode("JDVA00049341283");
        condition.setCheckResult(0);
        condition.setBusiCode("010K100125");
        condition.setOperateErp("bjxings");
    }

    @Test
    public void add() {
        try {
            int count = waybillCodeCheckDao.add(detail);
            Assert.assertEquals(count,1);
        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void queryByCondition() {
        try {
            waybillCodeCheckDao.add(detail);
            List<WaybillCodeCheckDto> list = waybillCodeCheckDao.queryByCondition(condition);
            Assert.assertEquals(list.get(0).getWaybillCode(),"JDVA00049341283");
        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void queryCountByCondition() {
        try {
            int add = waybillCodeCheckDao.add(detail);
            Integer count = waybillCodeCheckDao.queryCountByCondition(condition);
            Assert.assertTrue(add==count);
        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void exportByCondition() {
        try {

        }catch (Exception e){
            //异常直接断言单测失败
            Assert.assertTrue(Boolean.FALSE);
        }
    }
}