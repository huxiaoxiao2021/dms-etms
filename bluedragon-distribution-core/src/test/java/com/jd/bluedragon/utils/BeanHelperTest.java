package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

/**
 * <p>
 * Created by lixin39 on 2018/10/8.
 */
public class BeanHelperTest {

    @Test
    public void testCopyProperties(){
        ReverseReject source = new ReverseReject();
        source.setOperateTime(new Date());
//        RejectRequest source = new RejectRequest();
//        source.setOperateTime("2018-09-30 01:30:00");

        ReverseReject target = new ReverseReject();
        BeanHelper.copyProperties(target, source);
        System.out.println(target.getOperateTime().toString());
    }
    @Test
    public void testGetId(){
    	Random r = new Random();
    	List<Inspection> inspectionList = new ArrayList<>();
    	Date cur = new Date();
    	for(int i=0;i<10;i++) {
    		Inspection ins = new Inspection();
    		ins.setInspectionId(i+1L);
    		if(r.nextBoolean()) {
    			ins.setOperateTime(DateHelper.addHours(cur, r.nextInt(100)));
    		}
    		inspectionList.add(ins);
    	}
    	System.out.println("list="+JsonHelper.toJson(inspectionList));
    	System.out.println("lastId="+BeanHelper.getLastOperateInspectionId(inspectionList));
    	
    	List<Sorting> sortingList = new ArrayList<>();
    	for(int i=0;i<10;i++) {
    		Sorting sort = new Sorting();
    		sort.setId(i+1L);
    		if(r.nextBoolean()) {
    			sort.setOperateTime(DateHelper.addHours(cur, r.nextInt(100)));
    		}
    		sortingList.add(sort);
    	}
    	System.out.println("list="+JsonHelper.toJson(sortingList));
    	System.out.println("lastId="+BeanHelper.getLastOperateSortingId(sortingList));
    	
    	List<SendDetail> sendList = new ArrayList<>();
    	for(int i=0;i<10;i++) {
    		SendDetail send = new SendDetail();
    		send.setSendDId(i+1L);
    		if(r.nextBoolean()) {
    			send.setOperateTime(DateHelper.addHours(cur, r.nextInt(100)));
    		}
    		sendList.add(send);
    	}
    	System.out.println("list="+JsonHelper.toJson(sendList));
    	System.out.println("lastId="+BeanHelper.getLastOperateSendDetail(sendList));
    	
    }
}
