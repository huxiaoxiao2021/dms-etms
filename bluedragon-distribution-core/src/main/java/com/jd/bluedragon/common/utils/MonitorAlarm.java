package com.jd.bluedragon.common.utils;

import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Design for ump system
 * @author wangzichen
 *
 */
public class MonitorAlarm {
	
	public static final String UMP_WORKER_SORINTG_WAYBILL_EMPTY = "Bluedragon_dms_center.worker.sorting.waybillEmpty";
	
	private final static Logger log = LoggerFactory.getLogger(MonitorAlarm.class);

	public static void pushAlarm(String key,String msg) {
		log.info("In order to trigger the ump system alarm,and send message to it, the key is -- {}--, and the msg is --{}--",key,msg);
		Profiler.businessAlarm(key, new Date().getTime(), msg);
	}
}
