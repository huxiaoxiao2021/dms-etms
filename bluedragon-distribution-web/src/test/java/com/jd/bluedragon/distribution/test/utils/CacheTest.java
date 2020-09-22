package com.jd.bluedragon.distribution.test.utils;

import java.io.Closeable;
import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.ql.dms.common.cache.CacheService;
@RunWith(SpringJUnit4ClassRunner.class)
public class CacheTest{
	private static final Logger log = LoggerFactory.getLogger(CacheService.class);
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
    public void run() {}
    public static void main(String[] args){
    	Thread[] ts = new Thread[10];
    	for(int i =0 ;i < ts.length; i++){
        	ts[i] = new Thread(){
        		private Random r= new Random();
    			@Override
    			public void run() {
    				while(true){
    					getFromCache();
    					CacheTest.sleep(r.nextInt(10));
    				}
    			}
        	};
    	}
    	for(Thread t:ts){
    		t.start();
    	}
    }
    private static long cacheTime = 0;
    private static long dbTime = 0;
    
    public static long getFromCache(){
    	long date = new Date().getTime();
    	if(cacheTime < (date-30 * 1000)){
    		log.info("======getFromCache=====");
        	sleep(1);
    		cacheTime = date;
    		return getFromDb();
    	}
    	return cacheTime;
    }
    public static long getFromDb(){
    	long date = new Date().getTime();
    	if(dbTime < (date-60*1000)){
    		log.info("======getFromDb=====");
        	sleep(3);
    		dbTime = date;
    		return dbTime;
    	}
    	return dbTime;
    }
    public static void sleep(long time){
    	try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
