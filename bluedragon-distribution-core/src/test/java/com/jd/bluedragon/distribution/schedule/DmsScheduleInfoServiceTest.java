package com.jd.bluedragon.distribution.schedule;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DmsScheduleInfoServiceTest extends AbstractDaoIntegrationTest{
    @Autowired
    private StoragePackageMDao storagePackageMDao;
    @Test
    public void testPrintEdnPickingList(){
        Map<String,Object> params=new HashMap<>();
        List<String> waybillCodeList=Lists.newArrayList("JDV000465437205","JDVA00119327272","JDV000465428968");
        params.put("waybillCodeList",waybillCodeList);
        params.put("createSiteCode","23822");
        List<StoragePackageM> storagePackageMList=  storagePackageMDao.queryByWaybillCodeListAndSiteCode(params);
        System.out.printf(JSON.toJSONString(storagePackageMList));
    }
}
