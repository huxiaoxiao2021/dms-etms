package com.jd.bluedragon.utils;

import java.util.Random;

import org.junit.Test;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

public class ProfilerHelperTest {
	
	@Test
	public void test_genKeyByQuantity() {
		int[] numbers = {-1,0,1,4,10,50,300,500,1000,2000,4000,6000,6001,8000,8001,10000};
		Random r = new Random();
		for(int number:numbers){
			String rest = ProfilerHelper.genKeyByQuantity("key", number);
			CallerInfo callerInfo = ProfilerHelper.registerInfo(rest);
			CallerInfo callerInfo1 = ProfilerHelper.registerInfo(rest,Constants.UMP_APP_NAME_DMSWORKER);
			String msg = ""+number+":"+rest;
			try {
				Thread.sleep(r.nextInt(300));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Profiler.registerInfoEnd(callerInfo);
			Profiler.registerInfoEnd(callerInfo1);
			if(rest != null){
				System.out.println(msg);
			}else{
				System.err.println(msg);
			}
		}
	}
}
