package com.jd.bluedragon.distribution.test.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingException;
import com.jd.bluedragon.distribution.sorting.service.SortingExceptionService;
import com.jd.bluedragon.distribution.test.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;

public class SortingExceptionServiceTest extends TestBase {

    @Autowired
    private SortingExceptionService sortingExceptionService;


    @Test
    public void testAdd() throws Exception {
        SortingException exception=new SortingException();
        exception.setExceptionCode(200);
        exception.setExceptionMessage("test");
        exception.setCreateUserCode(1);
        exception.setBusinessType(10);
        exception.setYn(1);
        exception.setBoxCode("1234567891-1-1-23");
        exception.setCreateSiteCode(909);
        exception.setCreateTime(new Date(System.currentTimeMillis()));
        exception.setCreateUserName("test");
        exception.setUpdateTime(new Date(System.currentTimeMillis()));
        exception.setUpdateUserCode(2);
        exception.setUpdateUserName("test");
        exception.setReceiveSiteCode(1609);
        int result=sortingExceptionService.add(exception);
        Assert.assertEquals(1,result);
    }
}