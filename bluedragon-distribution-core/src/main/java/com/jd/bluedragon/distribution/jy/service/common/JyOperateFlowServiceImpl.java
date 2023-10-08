package com.jd.bluedragon.distribution.jy.service.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.dao.common.JyOperateFlowDao;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 拣运-附件接口实现类
 *
 * @author hujiping
 * @date 2023/4/19 8:48 PM
 */
@Service("jyOperateFlowService")
public class JyOperateFlowServiceImpl implements JyOperateFlowService {
	
	private final Logger logger = LoggerFactory.getLogger(JyOperateFlowServiceImpl.class);
	
    @Autowired
    private JyOperateFlowDao jyOperateFlowDao;
    
    @Autowired
    @Qualifier("jyOperateFlowMqProducer")
    private DefaultJMQProducer jyOperateFlowMqProducer;
    
    @Autowired
    DmsConfigManager dmsConfigManager;
    
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.insert", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public int insert(JyOperateFlowDto data) {
		if(data == null || StringUtils.isBlank(data.getOperateBizKey())) {
			logger.warn("jyOperateFlowService-insert-fail! operateBizKey值不能为空！");
			return 0;
		}
		return jyOperateFlowDao.insert(data);
	}

	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.sendMq", mState = {JProEnum.TP, JProEnum.FunctionError})
	public int sendMq(JyOperateFlowMqData mqData) {
		if(!Boolean.TRUE.equals(dmsConfigManager.getUccPropertyConfig().getSendJyOperateFlowMqSwitch())) {
			return 0;
		}
		jyOperateFlowMqProducer.sendOnFailPersistent(mqData.getOperateBizKey(), JsonHelper.toJson(mqData));
		return 1;
	}
	
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.sendMqList", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public int sendMqList(List<JyOperateFlowMqData> mqDataList) {
		if(!Boolean.TRUE.equals(dmsConfigManager.getUccPropertyConfig().getSendJyOperateFlowMqSwitch())) {
			return 0;
		}
		List<Message> msgList = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(mqDataList)) {
			for(JyOperateFlowMqData mqData : mqDataList) {
				msgList.add(new Message(jyOperateFlowMqProducer.getTopic(), JsonHelper.toJson(mqData), mqData.getOperateBizKey()));
			}
			jyOperateFlowMqProducer.batchSendOnFailPersistent(msgList);
			return mqDataList.size();
		}
		return 0;
	}    
}
