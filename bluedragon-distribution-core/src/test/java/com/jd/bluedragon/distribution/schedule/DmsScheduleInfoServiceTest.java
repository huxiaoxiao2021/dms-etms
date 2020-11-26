package com.jd.bluedragon.distribution.schedule;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageDDao;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.ldop.utils.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class DmsScheduleInfoServiceTest extends AbstractDaoIntegrationTest{
    @Autowired
    private StoragePackageDDao storagePackageDDao;
    @Test
    public void testPrintEdnPickingList(){
      /*  Map<String,Object> params=new HashMap<>();
        List<String> waybillCodeList=Lists.newArrayList("JDV000465437205","JDVA00119327272","JDV000465428968");
        params.put("waybillCodeList",waybillCodeList);
        params.put("createSiteCode","23822");*/
        StoragePackageD storagePackageD=new StoragePackageD();
        storagePackageD.setWaybillCode("JDV000465437205");
        storagePackageD.setCreateSiteCode(23822L);
        List<String> storageCodeList=  storagePackageDDao.findStorageCodeByWaybillCodeAndSiteCode(storagePackageD);
        if(!CollectionUtils.isEmpty(storageCodeList)) {
            Set<String> stringSet=new HashSet<>();
            //每条包裹下面的储位号，可能相同，需要去重
            for(String storageCode:storageCodeList){
                String[] strings=StringUtils.split(storageCode,",");
                if(strings!=null&&strings.length>0){
                    List<String> stringList=Lists.newArrayList(strings);
                    stringSet.addAll(stringList);
                }

            }
            System.out.printf(StringUtils.join(stringSet,","));
        }
        System.out.printf(JSON.toJSONString(""));
    }
}
