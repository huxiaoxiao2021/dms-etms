package com.jd.bluedragon.utils;

import java.util.Date;

import org.junit.Test;

import com.jd.bluedragon.distribution.command.JdMessage;

public class JdMessageTest {
	
	BusinessHelper helper = new BusinessHelper();

	@Test
	public void testIsBoxcode() {
		Object[] msgs = {null,"","测试单个参数消息格式化",new Date()};
		JdMessage defaultMsg = new JdMessage(100);
		for(Object obj:msgs){
			System.err.println(defaultMsg.formatMsg(obj));
		}
		msgs = new Object[]{null,"","参数1",new Date()};
		JdMessage sigleMsg = new JdMessage(100,"测试单个[%s]消息格式化");
		for(Object obj:msgs){
			System.err.println(sigleMsg.formatMsg(obj));
		}
		msgs = new String[][]{{"","参数1"}};
		JdMessage muliMsg = new JdMessage(100,"测试多个[%s]消息[%s]格式化");
		for(Object obj:msgs){
			System.err.println(muliMsg.formatMsg(obj));
			System.err.println(muliMsg.formatMsg(null,"","参数1",new Date()));
			System.err.println(muliMsg.formatMsg("1","2","e"));
		}
	}

}
