package com.jd.bluedragon.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertTrue;


/**
 * Created by zhanglei51 on 2017/3/13.
 */
public class GantryPackageUtilTest {
    @Test
    public void testIsBoxcode() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String boxCode="2017-03-13 16:48:00";
        try {
            Date curDate = simpleDateFormat.parse(boxCode);
            String key = GantryPackageUtil.getDateRegion(curDate);
            Assert.assertEquals(key,"201703131610");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
