package com.jd.bluedragon.common.utils;

import com.jd.ump.profiler.proxy.Profiler;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Design for ump system
 * @author wangzichen
 *
 */
public class MonitorAlarm {
	
	public static final String UMP_WORKER_SORINTG_WAYBILL_EMPTY = "Bluedragon_dms_center.worker.sorting.waybillEmpty";
	
	private final static Logger logger = Logger.getLogger(MonitorAlarm.class);

	public static void pushAlarm(String key,String msg) {
		logger.info("In order to trigger the ump system alarm,and send message to it, the key is -- "+key+"--, and the msg is --"+msg+"--");
		Profiler.businessAlarm(key, new Date().getTime(), msg);
	}
}
