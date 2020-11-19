package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.utils.ListUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ReceiveDaoTest{
	
	@Autowired
	private ReceiveDao receiveDao;
	

	
	@Test
    public void testAdd() {
        List<String> list=new LinkedList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        List<List<String>> list1=ListUtil.splitList(list,3);
        System.out.printf("");
    }


    @Test
    public void testFindReceiveJoinTotalCount() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 910);
        parameter.put("startTime", new Date());
        parameter.put("endTime", new Date());
        parameter.put("createUser", "James");
        parameter.put("queueNo", "James");
        receiveDao.findReceiveJoinTotalCount(parameter);
    }

	@Test
    public void testFindReceiveJoinList() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 910);
        parameter.put("startTime", new Date());
        parameter.put("endTime", new Date());
        parameter.put("createUser", "James");
        parameter.put("queueNo", "James");
        parameter.put("pageSize", 2);
        parameter.put("startIndex", 1);
        receiveDao.findReceiveJoinList(parameter);
    }
}
