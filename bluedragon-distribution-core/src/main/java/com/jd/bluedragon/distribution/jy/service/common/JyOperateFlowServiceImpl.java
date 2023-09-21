package com.jd.bluedragon.distribution.jy.service.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.dao.common.JyOperateFlowDao;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.StringUtils;

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
    UccPropertyConfiguration ucc;
    
	@Override
	public int insert(JyOperateFlowDto data) {
		if(data == null || StringUtils.isBlank(data.getOperateBizKey())) {
			logger.warn("jyOperateFlowService-insert-fail! operateBizKey值不能为空！");
			return 0;
		}
		return jyOperateFlowDao.insert(data);
	}

	@Override
	public int sendMq(JyOperateFlowMqData mqData) {
		if(!Boolean.TRUE.equals(ucc.getSendJyOperateFlowMqSwitch())) {
			return 0;
		}
		jyOperateFlowMqProducer.sendOnFailPersistent(mqData.getOperateBizKey(), JsonHelper.toJson(mqData));
		return 1;
	}

	@Override
	public int sendMqList(List<JyOperateFlowMqData> mqDataList) {
		if(!Boolean.TRUE.equals(ucc.getSendJyOperateFlowMqSwitch())) {
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
