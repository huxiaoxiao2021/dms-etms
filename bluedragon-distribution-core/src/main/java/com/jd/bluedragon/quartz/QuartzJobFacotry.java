package com.jd.bluedragon.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @ClassName: QuartzJobFacotry
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/28 18:22
 */
public class QuartzJobFacotry extends SpringBeanJobFactory {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {

        Object jobInstance = super.createJobInstance(bundle);
        beanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}
