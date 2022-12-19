package com.jd.bluedragon.distribution.base.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;

/**
 * 
 * @ClassName: 工具类管理
 * @Description: 工具类管理
 * @author: wuyoude
 * @date: 2022年12月23日 下午9:49:23
 *
 */
@Controller
@RequestMapping("base/dmsBaseUtils")
public class DmsBaseUtilsController {
	
	private static final Logger log = LoggerFactory.getLogger(DmsBaseUtilsController.class);
	
	@Autowired
	SendPrintService sendPrintService;
	//mq2配置-发送模板
	@Qualifier("spotCheckDetailProducer")
	DefaultJMQProducer defaultJMQ2Producer;
	//mq4配置-发送模板
	@Qualifier("jyUnloadScanProducer")
	DefaultJMQProducer defaultJMQ4Producer;	
    /**
     * 导出邮政-交接清单
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/getPrintRecords/{sign}")
    public @ResponseBody JdResponse<List<PrintHandoverListDto>> getPrintRecords(@PathVariable("sign") String sign,@RequestBody List<SendDetail> sendList) {
        JdResponse<List<PrintHandoverListDto>> rest = new JdResponse<List<PrintHandoverListDto>>();
        if(sign != null && sign.equals(Md5Helper.encode(Md5Helper.encode(JsonHelper.toJson(sendList))))) {
        	rest.toFail("签名认证失败！");
        	return rest;
        }
        if(CollectionUtils.isEmpty(sendList)) {
        	rest.toFail("列表不能为空！");
        	return rest;
        }
        rest.setData(new ArrayList<PrintHandoverListDto>());
        for(SendDetail sendDetail : sendList) {
        	rest.getData().add(sendPrintService.buildPrintHandoverListDtoTmp(sendDetail));
        }
        return rest;
    }
    /**
     * 发送mq
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/sendMq2/{sign}")
    public @ResponseBody JdResponse<List<Boolean>> sendMq2(@PathVariable("sign") String sign,@RequestBody List<Message> mqList) {
        JdResponse<List<Boolean>> rest = new JdResponse<List<Boolean>>();
        if(sign != null && sign.equals(Md5Helper.encode(Md5Helper.encode(JsonHelper.toJson(mqList))))) {
        	rest.toFail("签名认证失败！");
        	return rest;
        }
        if(CollectionUtils.isEmpty(mqList)) {
        	rest.toFail("消息列表不能为空！");
        	return rest;
        }
        DefaultJMQProducer producer = new DefaultJMQProducer();
        
        producer.setTopic(mqList.get(0).getTopic());
        producer.setTaskService(this.defaultJMQ2Producer.getTaskService());
        producer.setJmqProducer(this.defaultJMQ2Producer.getJmqProducer());
        rest.setData(new ArrayList<Boolean>());
        for(Message msg : mqList) {
        	producer.sendOnFailPersistent(msg.getBusinessId(), msg.getText());
        }
        return rest;
    } 
    /**
     * 发送mq
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/sendMq4/{sign}")
    public @ResponseBody JdResponse<List<Boolean>> sendMq4(@PathVariable("sign") String sign,@RequestBody List<JmqMessage> mqList) {
        JdResponse<List<Boolean>> rest = new JdResponse<List<Boolean>>();
        if(sign != null && sign.equals(Md5Helper.encode(Md5Helper.encode(JsonHelper.toJson(mqList))))) {
        	rest.toFail("签名认证失败！");
        	return rest;
        }
        if(CollectionUtils.isEmpty(mqList)) {
        	rest.toFail("消息列表不能为空！");
        	return rest;
        }
        DefaultJMQProducer producer = new DefaultJMQProducer();
        
        producer.setTopic(mqList.get(0).getTopic());
        producer.setTaskService(this.defaultJMQ4Producer.getTaskService());
        producer.setJmqProducer(this.defaultJMQ4Producer.getJmqProducer());
        rest.setData(new ArrayList<Boolean>());
        for(JmqMessage msg : mqList) {
        	producer.sendOnFailPersistent(msg.getBusinessId(), msg.getText());
        }
        return rest;
    } 
    public static class JmqMessage{
        // 主题
        protected String topic;
        // 业务ID
        protected String businessId;
        // 文本
        protected String text;
		public String getTopic() {
			return topic;
		}
		public void setTopic(String topic) {
			this.topic = topic;
		}
		public String getBusinessId() {
			return businessId;
		}
		public void setBusinessId(String businessId) {
			this.businessId = businessId;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
    }
}