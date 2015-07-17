package com.jd.bluedragon.distribution.test.auto.service;

import com.jd.bluedragon.distribution.auto.service.AbstractSortingPrepareService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;


public class AbstractSortingPrepareServiceTest {



    /**
     * 测试数据转换
     * @throws Exception
     */
    @Test
    public void testDataConvert() throws Exception{
        //dataConvert(Task source,Sorting target)
        Task task=new Task();
        task.setBody("{\n" +
                "  \"barcode\" : \"123456789-1-1-\",\n" +
                "  \"sortCenterNo\" : 1609,\n" +
                "  \"timeStamp\" : \"2014-10-10 23:59:59\",\n" +
                "  \"operatorID\" : 123,\n" +
                "  \"operatorName\" : \"金大中\",\n" +
                "  \"erpAccount\" : \"bjjdz\"\n" +
                "}");
        Sorting sorting=new Sorting();
        Class clazz=AbstractSortingPrepareService.class;
        Method testMethod = clazz.getDeclaredMethod("dataConvert", Task.class,Sorting.class);
        testMethod.setAccessible(true);//设置该方法可见
        testMethod.invoke(null,task, sorting);//jdk新特性：可变参数
        Assert.assertEquals(sorting.getCreateSiteCode(), Integer.valueOf(1609));
        Assert.assertEquals(sorting.getPackageCode(),"123456789-1-1-");
        Assert.assertEquals(sorting.getCreateUserCode(),Integer.valueOf(123));
        Assert.assertEquals(sorting.getCreateUser(),"金大中");
        Assert.assertEquals(sorting.getOperateTime().getTime(), DateHelper.parseDateTime("2014-10-10 23:59:59").getTime());
    }

}