package com.jd.bluedragon.distribution.distribution.sorting.service;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.InspectionTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class InspectionTaskTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Test
    public void executeSingleTaskTest() throws Exception{
        Task task = new Task();
        String ownSign = "DMS";
        InspectionTask inspectionTask = applicationContext.getBean(InspectionTask.class);
        Method singleTask = InspectionTask.class.getDeclaredMethod("executeSingleTask", Task.class, String.class);
        singleTask.setAccessible(true);
        singleTask.invoke(inspectionTask, task, ownSign);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
