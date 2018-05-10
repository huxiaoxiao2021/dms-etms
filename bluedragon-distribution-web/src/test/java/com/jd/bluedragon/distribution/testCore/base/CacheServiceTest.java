package com.jd.bluedragon.distribution.testCore.base;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/distribution-core-redis.xml" })
public class CacheServiceTest{
	@Autowired
	@Qualifier("jimdbCacheService")
	CacheService cache;
    public void setup() {
    }
	@Test
	/**
	 * 获取redis数据
	 */
    public void onTestResult() {
		run();
	}
	/**
	 * 获取redis数据
	 */
    public void run() {
    	Random r = new Random();
    	String[] keys = {"test1","test2","test3","test4"};
    	String[] keyFields = {"test1-f","test2-f"};
    	List<String> list = new ArrayList<String>();
    	list.add("1");
    	List<Object> muliList = new ArrayList<Object>();
    	Map<Object,Object> muliMap = new HashMap<Object,Object>();
    	TestEntity t =EntityUtil.getInstance(TestEntity.class);
    	String testEntityStr = JsonHelper.toJsonMs(t);
    	System.err.println("testEntityStr:"+testEntityStr);
    	TestEntity t1 = JsonHelper.fromJsonMs(testEntityStr, TestEntity.class);
    	TestEntity t2 = JsonHelper.fromJson(testEntityStr, TestEntity.class);
    	System.err.println("t1:"+JsonHelper.toJsonMs(t1));
    	System.err.println("t2:"+JsonHelper.toJsonMs(t2));
    	
    	muliList.add(EntityUtil.getInstance(TestEntity.class));
    	muliList.add(EntityUtil.getInstance(TestEntity.class));
    	muliList.add(EntityUtil.getInstance(TestEntity.class));
    	muliMap.put(EntityUtil.getInstance(TestEntity.class), EntityUtil.getInstance(TestEntity.class));
    	muliMap.put(EntityUtil.getInstance(TestEntity.class), EntityUtil.getInstance(TestEntity.class));
    	cache.set("muliList", muliList);
    	cache.set("muliMap", muliMap);
    	cache.set("testEntity", EntityUtil.getInstance(TestEntity.class));
//    	cache.hSet("dms.etms:DmsWeightInfo:VA66699599416-1", "VA66699599416-1-2-", EntityUtil.getInstance(WeightOperFlow.class));
//    	cache.hSet("dms.etms:DmsWeightInfo:VA66699599416-1", "VA66699599416-2-2-", EntityUtil.getInstance(WeightOperFlow.class));
    	Map<String,String> tmp0 = cache.hGetAll("dms.etms:DmsWeightInfo:VA66616673591-1");
    	String tmp1 = cache.get("dms.etms:DmsWeightInfo:VA66616673591-1");
    	muliList = cache.get("muliList",List.class);
    	muliMap = cache.get("muliMap",Map.class);
    	TestEntity testEntity = cache.get("testEntity",TestEntity.class);
    	
    	Object[]vals = {1,2l,5.0d,4.11,"test1-v","test2-v",keys,new Date()
    	,new TestEntity(1,"test1")};
    	for(String k:keys){
    		try {
				cache.set(k, vals[r.nextInt(vals.length)]);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	for(String kf:keyFields){
        		try {
					cache.hSet("h:"+k,kf, vals[r.nextInt(vals.length)]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
    	}
    	for(String k:keys){
    		System.err.println(k+"="+cache.get(k));
    		for(String kf:keyFields){
        		System.err.println(k+":"+kf+"="+cache.hGet("h:"+k,kf));
        	}
    	}
    	System.err.println(cache.setNx("dmstt:k1", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.setNx("dmstt:k1", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.setNx("dmstt:k2", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.setNx("dmstt:k2", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.get("dmstt:k1"));
    	System.err.println(cache.get("dmstt:k2"));
    	System.err.println(cache.hSetNx("dmstt:h:k1","f1", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.hSetNx("dmstt:h:k1","f1", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.hSetNx("dmstt:h:k1","f2", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.hSetNx("dmstt:h:k1","f3", vals[r.nextInt(vals.length)]));
    	System.err.println(cache.hGet("dmstt:h:k1","f1"));
    	System.err.println(JsonHelper.toJsonUseGson(cache.hGetAll("dmstt:h:k1")));
    	
    	for(int i=0;i<10;i++){
    		cache.sAdd("setKey", vals[r.nextInt(vals.length)]);
    	}
    	for(Object obj:cache.sMembers("setKey")){
    		System.err.println(obj);
    	}
    }
    public static void main(String[] args){
    	
    }

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
