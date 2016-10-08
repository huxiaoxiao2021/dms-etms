package com.jd.bluedragon.distribution.kvIndex.dao;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by wangtingwei on 2016/9/27.
 */
public class KvIndexTest extends AbstractDaoIntegrationTest {

    @Autowired
    private KvIndexDao kvIndexDao;



    @Test
    public void add() {
        Map<String,String> map =new HashMap<String,String>();
        map.put("012345678901234567890123456789012345678901234567890","1");
        map.put("01234567890123456789012345678901234567890123456789","2");
        map.put("0123456789012345678901234567890123456789012345678901","3");
        map.put("0123456789012345678901234567890123456789012345678","4");
        map.put(" 0123456789012345678901234567890123456789012345678901  ","5");
        map.put(" a123456789012345678901234567890123456789012345678901  ","6");
        map.put(" a1234567890123F56789012345678C01234p6789012345678901  ","7");
        map.put("   ","8");
        map.put(" a1234567890123F56789012345678C01234p6789012345678901  ","");
        map.put(" DAFDFDDAF0132  ","1609");
        Set<Map.Entry<String,String>> set=map.entrySet();
        for (Map.Entry<String,String> item:set) {
            KvIndex index = new KvIndex();
            index.setKeyword(item.getKey());
            index.setValue(item.getValue());

            System.out.printf(item.getKey()+"========"+item.getValue());
            Assert.assertEquals(kvIndexDao.add(index)>0, StringUtils.isNotBlank(item.getKey())&&StringUtils.isNotBlank(item.getValue()));

            List<Integer> result=kvIndexDao.queryCreateSiteCodesByKey(item.getKey());
            Boolean ass=StringUtils.isBlank(item.getKey())||StringUtils.isBlank(item.getValue());
            //System.out.printf(ass);
            for (Integer i:result){
                if(StringUtils.isBlank(item.getValue())||item.getValue().equals(i.toString())){
                    ass=true;
                }
            }
            Assert.assertTrue(ass);

        }
    }
    
    
    @Test
    public void testQueryOneByKeyword() {
    	Integer createSiteCode = kvIndexDao.queryOneByKeyword("0123456789012345678901234567890123456789012345678");
    	Assert.assertNotNull(createSiteCode);
    }
    
    
    @Test
    public void testAddRepeat() {
    	 KvIndex index = new KvIndex();
    	 index.setKeyword("hltest1l");
    	 index.setValue("134567");
    	 int count = kvIndexDao.add(index);
    	 count = kvIndexDao.add(index);
    	 Assert.assertEquals(1, count);
    	 
    	 List<Integer> cc = kvIndexDao.queryCreateSiteCodesByKey(index.getKeyword());
    	 Assert.assertEquals(1, cc.size());
    }
}
