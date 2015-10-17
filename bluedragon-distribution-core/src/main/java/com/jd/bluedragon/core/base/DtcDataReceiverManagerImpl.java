package com.jd.bluedragon.core.base;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.staig.receiver.rpc.DataReceiver;

@Service("dtcDataReceiverManager")
public class DtcDataReceiverManagerImpl implements DtcDataReceiverManager {

	private Log logger = LogFactory.getLog(DtcDataReceiverManagerImpl.class);
	
	private static final int PRIORITY = 2;

	@Autowired
	@Qualifier("dtcDataReceiverServiceJsf")
	private DataReceiver dtcDataReceiver;

	@Override
	@JProfiler(jKey = "DMS.BASE.DtcDataReceiverManagerImpl.downStreamHandle", mState = {JProEnum.TP, JProEnum.FunctionError})
	public com.jd.staig.receiver.rpc.Result downStreamHandle(String target, String methodName, String outboundType,
			int priority, String messageValue, String messageMd5Value, String source, String outboundNo) {
		return dtcDataReceiver.downStreamHandle(target, methodName, outboundType, priority, messageValue, messageMd5Value,
				source, outboundNo);
	}
	
	@Override
	public com.jd.staig.receiver.rpc.Result downStreamHandle(String target, String outboundType,
			String messageValue, String source, String outboundNo) {
		return downStreamHandle(target, outboundType, outboundType, PRIORITY, messageValue, null,
				source, outboundNo);
	}
}
