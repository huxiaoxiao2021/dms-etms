package com.jd.bluedragon.distribution.test.auto.service;

import com.jd.bluedragon.distribution.auto.service.AbstractSortingPrepareService;
import com.jd.bluedragon.distribution.auto.service.SimpleSortingPrepareServiceImpl;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.test.TestBase;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleSortingPrepareServiceImplTest extends TestBase {

    private static SimpleSortingPrepareServiceImpl service;
    private static Sorting sorting;
    private static Class clazz;
    @BeforeClass
    public static void init() throws Exception{
        service=new SimpleSortingPrepareServiceImpl();
        Task task=new Task();
        task.setBody("{\n" +
                "  \"timeStamp\" : \"2014-10-23 15:19:52\",\n" +
                "  \"weight\" : 0.021,\n" +
                "  \"barcode\" : \"61673311221-1-2-\",\n" +
                "  \"sortCenterNo\" : 910,\n" +
                "  \"chute\" : \"49\",\n" +
                "  \"operatorName\" : \"zhangsan\",\n" +
                "  \"operatorID\" : 1111,\n" +
                "  \"erpAccount\" : \"zhangsan4\",\n" +
                "  \"sortCenterName\" : \"广州麻涌分拣中心\"\n" +
                "}");
        sorting=new Sorting();
        clazz=AbstractSortingPrepareService.class;
        Method testMethod = clazz.getDeclaredMethod("dataConvert", Task.class,Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        testMethod.invoke(null,task, sorting);//jdk新特性：可变参数


    }

    @Test
    public void testPrepareSite() throws Exception {
        Assert.assertEquals(sorting.getWaybillCode(),"61673311221");
        Method testMethod = clazz.getDeclaredMethod("prepareSite", Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        boolean result=(Boolean) testMethod.invoke(service, sorting);//jdk新特性：可变参数
        Assert.assertEquals(result,true);
        Assert.assertNotNull(sorting.getReceiveSiteCode());
    }

    @Test
    public void testPrepareSorting() throws Exception {
        Method testMethod = clazz.getDeclaredMethod("prepareSorting", Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        boolean result=(Boolean) testMethod.invoke(service, sorting);//jdk新特性：可变参数
        Assert.assertEquals(result,true);
        Assert.assertNotNull(sorting.getBoxCode());
    }

    @Test
    public void testIntercept() throws Exception {
        Method testMethod = clazz.getDeclaredMethod("intercept", Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        boolean result=(Boolean) testMethod.invoke(service, sorting);//jdk新特性：可变参数
        Assert.assertEquals(result,true);
    }

    @Test
    public void testPush() throws Exception {
        Method testMethod = clazz.getDeclaredMethod("push", Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        boolean result=(Boolean) testMethod.invoke(service, sorting);//jdk新特性：可变参数
        Assert.assertEquals(result,true);
    }

    @Test
    public void testFilter() throws Exception {
        Sorting sorting1=new Sorting();
        sorting1.setCreateSiteCode(3011);
        sorting1.setReceiveSiteCode(303);
        Method testMethod = clazz.getDeclaredMethod("filter", Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        boolean result=(Boolean) testMethod.invoke(service, sorting);//jdk新特性：可变参数

        Assert.assertEquals(false,result);
    }


}