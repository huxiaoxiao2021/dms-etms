package com.jd.bluedragon.distribution.sorting.service;


import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("sortingZhiPeiService")
public class SortingZhiPeiServiceImple implements SortingZhiPeiService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
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
		String body = task.getBody().substring(1, task.getBody().length() - 1);
		SortingRequest request = JsonHelper.fromJson(body, SortingRequest.class);
		Sorting sorting = Sorting.toSorting(request);
		try {
			//终端发送mq
			zhipeiSortingMQ.send(sorting.getWaybillCode(),createMqBody(request));
		}catch (Exception e) {
			this.log.error("智配中心分拣推送给终端系统信息失败，运单号：{}", sorting.getWaybillCode(), e);
		}
		
		try{
			//生成分拣中心操作日志到Cassandra
			this.sortingService.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING);
		}catch (Exception e) {
			this.log.error("全程跟踪失败，运单号：{}", sorting.getWaybillCode(), e);
		}
		
	}
	
	private String createMqBody(SortingRequest sorting) {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
//		sb.append("<SortingZhiPeiTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
//		sb.append("<pacakgeCode>" + sorting.getPackageCode() + "</pacakgeCode>");
//		sb.append("<operateSiteCode>" + sorting.getSiteCode() + "</operateSiteCode>");
//		sb.append("<operatTime>" + sorting.getOperateTime() + "</operatTime>");
//		sb.append("<userCode>" + sorting.getUserCode() + "</userCode>");
//		sb.append("<userName>" + sorting.getUserName() + "</userName>");
//		sb.append("</SortingZhiPeiTaskInfo>");
		SortingRequest request = new SortingRequest();
		request.setPackageCode(sorting.getPackageCode());
		request.setSiteCode(sorting.getSiteCode());
		request.setSiteName(sorting.getSiteName());
		request.setOperateTime(sorting.getOperateTime());
		request.setUserCode(sorting.getUserCode());
		request.setUserName(sorting.getUserName());
		String str = JsonHelper.toJson(request);
		log.info("智配中心给终端发送zhipeiSortingMQ:{}", str);
		return str;
	}
	
}
