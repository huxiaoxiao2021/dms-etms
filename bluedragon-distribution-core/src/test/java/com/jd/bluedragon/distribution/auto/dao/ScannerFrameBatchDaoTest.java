package com.jd.bluedragon.distribution.auto.dao;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSendSearchArgument;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.utils.DateHelper;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.Calendar;

/**
 * Created by wangtingwei on 2016/12/13.
 */
public class ScannerFrameBatchDaoTest extends AbstractDaoIntegrationTest {
    @Autowired
    private ScannerFrameBatchSendDao scannerFrameBatchSendDao;
    @Test
    public void testgetSplitPageList() throws Exception {
        ScannerFrameBatchSendSearchArgument argument=new ScannerFrameBatchSendSearchArgument();
        argument.setMachineId(1L);
        argument.setStartTime(DateHelper.add(new Date(), Calendar.HOUR, -10));
        argument.setEndTime(new Date());
        argument.setHasPrinted(true);
        Pager<ScannerFrameBatchSendSearchArgument> pager=new Pager<ScannerFrameBatchSendSearchArgument>(1,10);
        pager.setPageSize(10);
        pager.setPageNo(1);
        pager.setData(argument);
        List<ScannerFrameBatchSend> resultList= scannerFrameBatchSendDao.getSplitPageList(pager);
        long count=scannerFrameBatchSendDao.getSplitPageListCount(pager);
        Assert.assertEquals(count,0);
    }

    @Test
    public void testgetCurrentSplitPageList() throws Exception {
        ScannerFrameBatchSendSearchArgument argument=new ScannerFrameBatchSendSearchArgument();
        argument.setMachineId(1L);
        argument.setStartTime(DateHelper.add(new Date(), Calendar.HOUR, -10));
        argument.setEndTime(new Date());
        argument.setHasPrinted(true);
        Pager<ScannerFrameBatchSendSearchArgument> pager=new Pager<ScannerFrameBatchSendSearchArgument>(1,10);
        pager.setPageSize(10);
        pager.setPageNo(1);
        pager.setData(argument);
        List<ScannerFrameBatchSend> resultList= scannerFrameBatchSendDao.getCurrentSplitPageList(pager);
        long count=scannerFrameBatchSendDao.getCurrentSplitPageListCount(pager);
        Assert.assertEquals(count,0);
    }
}
