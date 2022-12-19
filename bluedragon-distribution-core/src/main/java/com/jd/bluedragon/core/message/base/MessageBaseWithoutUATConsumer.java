package com.jd.bluedragon.core.message.base;

import org.springframework.beans.factory.annotation.Value;

/**
 * JMQ消费基础类 忽略UAT标识
 * 不设置UAT标识过滤消费数据，在UAT 和 正式环境隔离TOPIC并且发送消息者没有在UAT设置UAT的标识的时候使用
 * 
 * @author liwenji
 * @date 2022-12-19 16:23
 */
public abstract class MessageBaseWithoutUATConsumer extends MessageBaseConsumer {

    //JMQ Split UAT标识开关 忽略UAT标识
    @Override
    public void setUat(String uat) {
        super.setUat(Boolean.FALSE.toString());
    }
    
}
