package com.jd.bluedragon.distribution.sorting.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;

@Service("sortingReturnService")
public class SortingZhiPeiServiceImple implements SortingZhiPeiService {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
    @Qualifier("zhipeiSortingMQ")
    private DefaultJMQProducer zhipeiSortingMQ;
	
	@Autowired
    private SortingService sortingService;
	
	/** 
	 * 智配中心分拣推送mq消息给终端系统，同时写分拣中心操作日志
	 */
	@Override
	public void doSorting(Task task) throws Exception {
		// TODO Auto-generated method stub
		SortingRequest request = JsonHelper.fromJson(task.getBody(), SortingRequest.class);
		Sorting sorting = Sorting.toSorting(request);
		//生成分拣中心操作日志到Cassandra
		this.sortingService.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING);
		
		//终端发送mq
		zhipeiSortingMQ.send(sorting.getWaybillCode(),createMqBody(request));
	}
	
	private String createMqBody(SortingRequest sorting) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
		sb.append("<SortingZhiPeiTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
		sb.append("<pacakgeCode>" + sorting.getPackageCode() + "</pacakgeCode>");
		sb.append("<operateSiteCode>" + sorting.getSiteCode() + "</operateSiteCode>");
		sb.append("<MessageType>zhipeiSortingMQ</MessageType>");
		sb.append("<OperatTime>" + sorting.getOperateTime() + "</OperatTime>");
		sb.append("<userCode>" + sorting.getUserCode() + "</userCode>");
		sb.append("<userName>" + sorting.getUserName() + "</userName>");
		sb.append("</OrderTaskInfo>");
		logger.info("智配中心给终端发送zhipeiSortingMQ:"+sb);
		return sb.toString();
	}
}
