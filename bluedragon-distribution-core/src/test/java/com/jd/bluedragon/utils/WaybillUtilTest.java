package com.jd.bluedragon.utils;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.junit.Test;

public class WaybillUtilTest {
    WaybillUtil waybillUtil = new WaybillUtil();

    @Test
    public void testGetWaybillCodeByPackageBarcode(){
        String packageCode = "12345678901-1-5-";
        System.out.println(waybillUtil.generateAllPackageCodes(packageCode));

    }
}
